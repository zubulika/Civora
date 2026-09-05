#!/usr/bin/env python3
"""
Civora Android Development Supervisor & Auto-Reload Runtime (android.py)
Standard: android-dev-supervisor

Features:
- Zero-config Android SDK & JDK 21 detection across custom Windows & system paths
- Automatic AVD lifecycle management on isolated port 5556 (Pixel 9 Pro XL)
- Streamlined incremental Gradle build & ADB install
- Live file watcher for instant Compose / Kotlin reload on save
"""

import argparse
import os
import shutil
import subprocess
import sys
import time
from pathlib import Path

# --- Configuration & Constants ---
APP_PACKAGE = "com.civora.app"
MAIN_ACTIVITY = ".MainActivity"
DEFAULT_AVD_NAME = "Pixel_9_Pro_XL"
DEFAULT_PORT = "5556"
PROJECT_ROOT = Path(__file__).resolve().parent
APP_MODULE_DIR = PROJECT_ROOT / "app"
GRADLEW_BAT = PROJECT_ROOT / "gradlew.bat"
GRADLEW_SH = PROJECT_ROOT / "gradlew"

# --- Toolchain Resolution ---
def resolve_environment():
    env = os.environ.copy()

    # 1. Resolve Android SDK
    sdk_dir = None
    local_props = PROJECT_ROOT / "local.properties"
    if local_props.exists():
        with open(local_props, "r", encoding="utf-8") as f:
            for line in f:
                if line.strip().startswith("sdk.dir"):
                    raw_val = line.strip().split("=", 1)[1].strip()
                    # Clean escaped slashes (e.g. D\:\\SDK -> D:\SDK)
                    raw_val = raw_val.replace("\\:", ":").replace("\\\\", "\\")
                    if os.path.exists(raw_val):
                        sdk_dir = Path(raw_val)
                        break

    common_sdk_candidates = [
        sdk_dir,
        Path("D:/SDK"),
        Path(os.environ.get("ANDROID_HOME", "")) if os.environ.get("ANDROID_HOME") else None,
        Path(os.environ.get("LOCALAPPDATA", "")) / "Android" / "Sdk" if os.environ.get("LOCALAPPDATA") else None,
        Path("C:/Android/Sdk"),
        Path("E:/Build Tools/Android Studio SDK"),
    ]

    resolved_sdk = None
    for candidate in common_sdk_candidates:
        if candidate and candidate.exists() and (candidate / "platform-tools" / ("adb.exe" if sys.platform == "win32" else "adb")).exists():
            resolved_sdk = candidate
            break

    if not resolved_sdk:
        print("[!] Warning: Could not locate Android SDK. Ensure local.properties has valid sdk.dir")
    else:
        env["ANDROID_HOME"] = str(resolved_sdk)
        env["ANDROID_SDK_ROOT"] = str(resolved_sdk)
        platform_tools = resolved_sdk / "platform-tools"
        emulator_dir = resolved_sdk / "emulator"
        env["PATH"] = f"{platform_tools};{emulator_dir};" + env.get("PATH", "")

    # 2. Resolve Java JDK
    common_jbr_candidates = [
        Path(os.environ.get("JAVA_HOME", "")) if os.environ.get("JAVA_HOME") else None,
        Path("E:/Build Tools/Android Studio SDK/jbr"),
        Path("C:/Program Files/Android/Android Studio/jbr"),
        Path("C:/Program Files/Java/jdk-21"),
    ]

    resolved_java = None
    for candidate in common_jbr_candidates:
        if candidate and candidate.exists() and (candidate / "bin" / ("java.exe" if sys.platform == "win32" else "java")).exists():
            resolved_java = candidate
            break

    if resolved_java:
        env["JAVA_HOME"] = str(resolved_java)
        env["PATH"] = f"{resolved_java / 'bin'};" + env.get("PATH", "")

    return env, resolved_sdk, resolved_java


def get_adb_path(resolved_sdk):
    exe_name = "adb.exe" if sys.platform == "win32" else "adb"
    if resolved_sdk:
        adb_candidate = resolved_sdk / "platform-tools" / exe_name
        if adb_candidate.exists():
            return str(adb_candidate)
    return shutil.which(exe_name) or "adb"


def get_emulator_path(resolved_sdk):
    exe_name = "emulator.exe" if sys.platform == "win32" else "emulator"
    if resolved_sdk:
        candidate = resolved_sdk / "emulator" / exe_name
        if candidate.exists():
            return str(candidate)
    return shutil.which(exe_name) or "emulator"


# --- Device Management ---
def get_responsive_devices(adb_path, env):
    responsive = []
    try:
        output = subprocess.check_output([adb_path, "devices"], text=True, stderr=subprocess.DEVNULL, env=env)
        lines = [line.split()[0] for line in output.strip().split("\n")[1:] if line.strip() and "offline" not in line and "device" in line]
        for dev_id in lines:
            try:
                res = subprocess.run(
                    [adb_path, "-s", dev_id, "shell", "echo", "ready"],
                    stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True, timeout=3, env=env
                )
                if res.returncode == 0 and "ready" in res.stdout:
                    responsive.append(dev_id)
            except Exception:
                continue
    except Exception:
        pass
    return responsive


def is_boot_completed(adb_path, dev_id, env):
    try:
        res = subprocess.run(
            [adb_path, "-s", dev_id, "shell", "getprop", "sys.boot_completed"],
            stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True, timeout=3, env=env
        )
        return res.stdout.strip() == "1"
    except Exception:
        return False


def launch_emulator(emulator_path, avd_name, port=DEFAULT_PORT, env=None):
    print(f"[*] Launching AVD '{avd_name}' on dedicated port {port}...")
    cmd = [
        emulator_path,
        "-avd", avd_name,
        "-port", port,
        "-netdelay", "none",
        "-netspeed", "full"
    ]
    subprocess.Popen(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, env=env)


def wait_for_device(adb_path, env, target_port=DEFAULT_PORT, timeout=120):
    expected_serial = f"emulator-{target_port}"
    start_time = time.time()
    print(f"[*] Waiting for device '{expected_serial}' to become responsive and finish boot...")

    while time.time() - start_time < timeout:
        devices = get_responsive_devices(adb_path, env)
        if expected_serial in devices:
            if is_boot_completed(adb_path, expected_serial, env):
                print(f"[+] Device '{expected_serial}' boot confirmed!")
                return expected_serial
        elif devices:
            # Pick first responsive device if port is different
            first_dev = devices[0]
            if is_boot_completed(adb_path, first_dev, env):
                print(f"[+] Using connected device: {first_dev}")
                return first_dev
        time.sleep(2)

    raise TimeoutError("Timed out waiting for Android device / emulator.")


# --- Build & Install Pipeline ---
def find_built_apk():
    apk_debug = APP_MODULE_DIR / "build" / "outputs" / "apk" / "debug" / "app-debug.apk"
    if apk_debug.exists():
        return apk_debug
    # Search recursively in app/build
    matches = list(APP_MODULE_DIR.glob("build/outputs/apk/**/*.apk"))
    return matches[0] if matches else None


def run_gradle_build(env, task="assembleDebug"):
    gradle_cmd = str(GRADLEW_BAT if sys.platform == "win32" else GRADLEW_SH)
    print(f"\n[*] Running Gradle: {task}...")
    start_time = time.time()
    proc = subprocess.run([gradle_cmd, task], env=env, cwd=str(PROJECT_ROOT))
    duration = time.time() - start_time

    if proc.returncode != 0:
        print(f"[!] Build failed with exit code {proc.returncode}")
        return False
    print(f"[+] Build succeeded in {duration:.2f}s")
    return True


def install_and_launch(adb_path, dev_id, apk_path, env):
    print(f"[*] Installing APK onto {dev_id}...")
    install_res = subprocess.run(
        [adb_path, "-s", dev_id, "install", "-r", str(apk_path)],
        capture_output=True, text=True, env=env
    )
    if install_res.returncode != 0:
        print(f"[!] Streamed install failed: {install_res.stderr}. Attempting fallback...")
        remote_tmp = f"/data/local/tmp/{apk_path.name}"
        subprocess.run([adb_path, "-s", dev_id, "push", str(apk_path), remote_tmp], env=env)
        subprocess.run([adb_path, "-s", dev_id, "shell", "pm", "install", "-r", remote_tmp], env=env)

    print(f"[*] Launching {APP_PACKAGE}/{MAIN_ACTIVITY}...")
    subprocess.run(
        [adb_path, "-s", dev_id, "shell", "am", "start", "-n", f"{APP_PACKAGE}/{MAIN_ACTIVITY}"],
        env=env
    )
    print("[+] App running on screen.")


# --- File Watcher ---
def compute_source_snapshot(watch_paths):
    snapshot = {}
    extensions = {".kt", ".kts", ".xml", ".properties", ".gradle"}
    for p in watch_paths:
        if not p.exists():
            continue
        if p.is_file() and p.suffix in extensions:
            try:
                snapshot[str(p)] = p.stat().st_mtime
            except OSError:
                pass
        else:
            for item in p.rglob("*"):
                if item.is_file() and item.suffix in extensions:
                    # Ignore build caches
                    if "build" in item.parts or ".gradle" in item.parts:
                        continue
                    try:
                        snapshot[str(item)] = item.stat().st_mtime
                    except OSError:
                        pass
    return snapshot


def start_watch_loop(adb_path, dev_id, env):
    watch_dirs = [
        APP_MODULE_DIR / "src",
        PROJECT_ROOT / "gradle",
        PROJECT_ROOT / "build.gradle.kts",
        PROJECT_ROOT / "settings.gradle.kts",
        PROJECT_ROOT / "gradle.properties"
    ]
    print("\n" + "=" * 60)
    print("  [Civora Live Watcher] Watching source files for instant reload...")
    print("  Press Ctrl+C to terminate.")
    print("=" * 60 + "\n")

    current_snapshot = compute_source_snapshot(watch_dirs)

    while True:
        try:
            time.sleep(1.0)
            new_snapshot = compute_source_snapshot(watch_dirs)
            if new_snapshot != current_snapshot:
                # Find changed file
                changed = [k for k in new_snapshot if current_snapshot.get(k) != new_snapshot.get(k)]
                for ch in changed[:3]:
                    print(f"\n[~] File changed: {Path(ch).name}")
                current_snapshot = new_snapshot

                # Recompile and relaunch
                if run_gradle_build(env, "assembleDebug"):
                    apk = find_built_apk()
                    if apk:
                        install_and_launch(adb_path, dev_id, apk, env)
        except KeyboardInterrupt:
            print("\n[*] Stopping Civora supervisor.")
            break


# --- Main CLI ---
def main():
    parser = argparse.ArgumentParser(description="Civora Android Development Supervisor")
    parser.add_argument("--build-only", action="store_true", help="Compile debug APK without launching")
    parser.add_argument("--no-watch", action="store_true", help="Build and launch once without file watching")
    parser.add_argument("--device", type=str, default=None, help="Target specific ADB device ID")
    parser.add_argument("--avd", type=str, default=DEFAULT_AVD_NAME, help="AVD name to launch if none online")
    parser.add_argument("--port", type=str, default=DEFAULT_PORT, help="Dedicated emulator port")
    parser.add_argument("--logs", "-l", action="store_true", help="Stream logcat filtered by package")
    args = parser.parse_args()

    env, resolved_sdk, resolved_java = resolve_environment()
    print("--- Civora Android Environment ---")
    print(f"  Android SDK : {resolved_sdk or 'System Default'}")
    print(f"  Java JDK    : {resolved_java or 'System Default'}")
    print("----------------------------------")

    adb_path = get_adb_path(resolved_sdk)

    # Build stage
    if not run_gradle_build(env, "assembleDebug"):
        sys.exit(1)

    apk = find_built_apk()
    if not apk:
        print("[!] APK file not found after build.")
        sys.exit(1)
    print(f"[+] Output APK: {apk}")

    if args.build_only:
        print("[+] Build complete. Exiting (--build-only).")
        return

    # Device detection & launch
    devices = get_responsive_devices(adb_path, env)
    target_device = args.device

    if not target_device:
        if devices:
            target_device = devices[0]
            print(f"[*] Found active responsive device: {target_device}")
        else:
            emulator_path = get_emulator_path(resolved_sdk)
            launch_emulator(emulator_path, args.avd, args.port, env)
            target_device = wait_for_device(adb_path, env, args.port)

    # Install & Launch
    install_and_launch(adb_path, target_device, apk, env)

    if args.logs:
        print(f"[*] Streaming Logcat for package: {APP_PACKAGE}...")
        subprocess.run([adb_path, "-s", target_device, "logcat", f"{APP_PACKAGE}:V", "*:S"], env=env)
        return

    if not args.no_watch:
        start_watch_loop(adb_path, target_device, env)


if __name__ == "__main__":
    main()
