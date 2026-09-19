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
APP_PACKAGE = "com.civora.app.debug"
MAIN_ACTIVITY = "com.civora.app.MainActivity"
DEFAULT_AVD_NAME = "Pixel_9_Pro_XL"
DEFAULT_PORT = "5554"
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


# --- Device Management & Self-Healing ---
def get_responsive_devices(adb_path, env):
    responsive = []
    try:
        output = subprocess.check_output([adb_path, "devices"], text=True, stderr=subprocess.DEVNULL, env=env)
        lines = [line.split()[0] for line in output.strip().split("\n")[1:] if line.strip() and "offline" not in line and "device" in line]
        for dev_id in lines:
            try:
                res = subprocess.run(
                    [adb_path, "-s", dev_id, "shell", "echo", "ready"],
                    stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True, timeout=2, env=env
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
            stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True, timeout=2, env=env
        )
        return res.stdout.strip() == "1"
    except (subprocess.TimeoutExpired, Exception):
        return False


def kill_all_emulator_processes():
    if sys.platform == "win32":
        subprocess.run(
            ["taskkill", "/F", "/IM", "qemu-system-x86_64.exe", "/IM", "emulator.exe"],
            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL
        )
    else:
        subprocess.run(
            ["pkill", "-9", "-f", "qemu-system|emulator"],
            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL
        )


def restart_adb_server(adb_path, env):
    subprocess.run([adb_path, "kill-server"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, env=env)
    time.sleep(1)
    subprocess.run([adb_path, "start-server"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, env=env)
    time.sleep(1)


def clean_stale_avd_locks(avd_name, force=False):
    # If no qemu emulator is running (or force is requested), purge stale .lock files
    is_running = False
    if not force and sys.platform == "win32":
        try:
            out = subprocess.check_output(["tasklist", "/FI", "IMAGENAME eq qemu-system-x86_64.exe"], text=True, stderr=subprocess.DEVNULL)
            if "qemu-system-x86_64.exe" in out:
                is_running = True
        except Exception:
            pass

    if not is_running or force:
        avd_paths = [
            Path.home() / ".android" / "avd" / f"{avd_name}.avd",
            Path(os.environ.get("ANDROID_AVD_HOME", "")) / f"{avd_name}.avd"
        ]
        for avd_dir in avd_paths:
            if avd_dir.exists():
                for lock_file in avd_dir.glob("*.lock"):
                    try:
                        if lock_file.is_dir():
                            shutil.rmtree(lock_file, ignore_errors=True)
                        else:
                            lock_file.unlink(missing_ok=True)
                    except Exception:
                        pass
                # Reset corrupted window scale/position if present
                user_ini = avd_dir / "emulator-user.ini"
                if user_ini.exists():
                    try:
                        content = user_ini.read_text(encoding="utf-8")
                        if "scale = -1" in content or "scale = 0" in content:
                            user_ini.unlink(missing_ok=True)
                    except Exception:
                        pass


def ensure_healthy_emulator_environment(adb_path, avd_name, env):
    """
    Self-healing routine:
    Detects if an emulator process (qemu) is running in the background while ADB has become
    unresponsive or frozen. If detected, automatically terminates the zombie instance, restarts
    the ADB server, and clears stale file locks to allow clean startup.
    """
    is_qemu_running = False
    if sys.platform == "win32":
        try:
            out = subprocess.check_output(["tasklist", "/FI", "IMAGENAME eq qemu-system-x86_64.exe"], text=True, stderr=subprocess.DEVNULL)
            if "qemu-system-x86_64.exe" in out:
                is_qemu_running = True
        except Exception:
            pass

    if is_qemu_running:
        responsive = get_responsive_devices(adb_path, env)
        has_responsive_emu = any(d.startswith("emulator-") for d in responsive)
        if not has_responsive_emu:
            print("[!] Auto-recovery: Detected frozen/unresponsive emulator process in background.")
            print("[*] Terminating zombie emulator, restarting ADB daemon, and clearing locks...")
            kill_all_emulator_processes()
            restart_adb_server(adb_path, env)
            clean_stale_avd_locks(avd_name, force=True)
            return

    clean_stale_avd_locks(avd_name)


def launch_emulator(emulator_path, avd_name, port=DEFAULT_PORT, env=None):
    clean_stale_avd_locks(avd_name)
    print(f"[*] Launching Desktop AVD '{avd_name}'...")
    cmd = [
        emulator_path,
        "-avd", avd_name,
        "-netdelay", "none",
        "-netspeed", "full",
        # Avoid reusing or writing a Quick Boot snapshot after an ADB/emulator
        # crash. This preserves the AVD's data while forcing a clean boot.
        "-no-snapshot-load",
        "-no-snapshot-save",
        "-no-boot-anim",
        # This workstation has a supported NVIDIA GPU; host rendering is both
        # faster and more reliable than the SwiftShader fallback here.
        "-gpu", "host"
    ]
    if port:
        cmd.extend(["-port", str(port)])

    creationflags = 0
    if sys.platform == "win32":
        creationflags = subprocess.CREATE_NO_WINDOW | subprocess.CREATE_NEW_PROCESS_GROUP

    proc = subprocess.Popen(
        cmd,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
        stdin=subprocess.DEVNULL,
        creationflags=creationflags,
        env=env
    )
    return proc


def wait_for_emulator(adb_path, env, expected_port=None, proc=None, timeout=240):
    start_time = time.time()
    print("[*] Waiting for desktop emulator to finish boot...")
    expected_serial = f"emulator-{expected_port}" if expected_port else None

    offline_reconnect_attempts = 0
    last_offline_recovery = 0.0
    while time.time() - start_time < timeout:
        try:
            output = subprocess.check_output([adb_path, "devices"], text=True, stderr=subprocess.DEVNULL, env=env)
            lines = [line.split()[0] for line in output.strip().split("\n")[1:] if line.strip() and "offline" not in line]
            emulator_serials = [s for s in lines if s.startswith("emulator-")]

            # ADB can retain an offline transport after a previous emulator
            # process was killed. Reconnect it once while the new process boots.
            elapsed = time.time() - start_time
            is_offline = any("offline" in line for line in output.splitlines())
            if is_offline and elapsed - last_offline_recovery >= 20 and offline_reconnect_attempts < 3:
                offline_reconnect_attempts += 1
                last_offline_recovery = elapsed
                if offline_reconnect_attempts == 1:
                    print("\n[*] ADB reports the emulator offline; requesting transport reconnect...")
                    subprocess.run([adb_path, "reconnect", "offline"], env=env,
                                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
                else:
                    print(f"\n[*] Restarting ADB after offline boot transport (attempt {offline_reconnect_attempts}/3)...")
                    restart_adb_server(adb_path, env)

            candidates = [expected_serial] if (expected_serial and expected_serial in emulator_serials) else emulator_serials

            for emu in candidates:
                if is_boot_completed(adb_path, emu, env):
                    print(f"\n[+] Desktop emulator '{emu}' boot confirmed and ready!")
                    return emu
        except Exception:
            pass

        elapsed = int(time.time() - start_time)
        print(f"\r[*] Emulator initializing and booting OS... ({elapsed}s)", end="", flush=True)
        time.sleep(2)

    print()
    raise TimeoutError("Timed out waiting for Android emulator to boot.")


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


def install_and_launch_single(adb_path, dev_id, apk_path, env):
    is_emulator = dev_id.startswith("emulator-")
    device_label = f"Emulator ({dev_id})" if is_emulator else f"Physical Device ({dev_id})"
    print(f"[*] Installing APK onto {device_label}...")
    install_res = subprocess.run(
        [adb_path, "-s", dev_id, "install", "-r", str(apk_path)],
        capture_output=True, text=True, env=env
    )
    if install_res.returncode != 0:
        err_msg = install_res.stderr.strip()
        if "INSTALL_FAILED_UPDATE_INCOMPATIBLE" in err_msg or "signatures do not match" in err_msg or "INSTALL_FAILED_VERSION_DOWNGRADE" in err_msg:
            print(f"[*] Package conflict detected ({err_msg}). Cleanly replacing app on {device_label}...")
            subprocess.run([adb_path, "-s", dev_id, "uninstall", APP_PACKAGE], env=env, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            subprocess.run([adb_path, "-s", dev_id, "install", str(apk_path)], env=env, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        else:
            print(f"[!] Streamed install on {dev_id} failed: {err_msg}. Attempting fallback...")
            remote_tmp = f"/data/local/tmp/{apk_path.name}"
            subprocess.run([adb_path, "-s", dev_id, "push", str(apk_path), remote_tmp], env=env, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
            subprocess.run([adb_path, "-s", dev_id, "shell", "pm", "install", "-r", remote_tmp], env=env, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

    print(f"[*] Launching {APP_PACKAGE}/{MAIN_ACTIVITY} on {device_label}...")
    subprocess.run(
        [adb_path, "-s", dev_id, "shell", "am", "start", "-n", f"{APP_PACKAGE}/{MAIN_ACTIVITY}"],
        env=env, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL
    )
    print(f"[+] App running on {device_label}.")


def install_and_launch_all(adb_path, device_list, apk_path, env):
    from concurrent.futures import ThreadPoolExecutor
    if not device_list:
        print("[!] No active devices available for installation.")
        return

    if len(device_list) == 1:
        install_and_launch_single(adb_path, device_list[0], apk_path, env)
        return

    print(f"[*] Deploying simultaneously across {len(device_list)} devices...")
    with ThreadPoolExecutor(max_workers=len(device_list)) as executor:
        futures = [
            executor.submit(install_and_launch_single, adb_path, dev_id, apk_path, env)
            for dev_id in device_list
        ]
        for f in futures:
            f.result()


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


def start_watch_loop(adb_path, target_devices, env, auto_discover=True, target_filter="all"):
    watch_dirs = [
        APP_MODULE_DIR / "src",
        PROJECT_ROOT / "gradle",
        PROJECT_ROOT / "build.gradle.kts",
        PROJECT_ROOT / "settings.gradle.kts",
        PROJECT_ROOT / "gradle.properties",
        PROJECT_ROOT / "version.properties"
    ]
    print("\n" + "=" * 60)
    print(f"  [Civora Multi-Device Live Watcher] Active on {len(target_devices)} device(s)")
    for dev in target_devices:
        dev_type = "Desktop Emulator" if dev.startswith("emulator-") else "Physical Smartphone"
        print(f"    * {dev} ({dev_type})")
    print("  Watching source files for instant reload across all devices...")
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

                # Dynamically refresh active devices if auto-discovery is on
                active_devices = target_devices
                if auto_discover:
                    live_devices = get_responsive_devices(adb_path, env)
                    if target_filter == "emulator":
                        live_devices = [d for d in live_devices if d.startswith("emulator-")]
                    elif target_filter == "phone":
                        live_devices = [d for d in live_devices if not d.startswith("emulator-")]
                    if live_devices:
                        active_devices = live_devices

                # Recompile and relaunch across all active screens
                if run_gradle_build(env, "assembleDebug"):
                    apk = find_built_apk()
                    if apk:
                        install_and_launch_all(adb_path, active_devices, apk, env)
        except KeyboardInterrupt:
            print("\n[*] Stopping Civora supervisor.")
            break


# --- Main CLI ---
def main():
    parser = argparse.ArgumentParser(description="Civora Android Development Supervisor (Dual Screen & Multi-Device)")
    parser.add_argument("--build-only", action="store_true", help="Compile debug APK without launching")
    parser.add_argument("--no-watch", action="store_true", help="Build and launch once without file watching")
    parser.add_argument("--device", type=str, default=None, help="Target specific ADB device ID only")
    parser.add_argument("--target", choices=["all", "emulator", "phone"], default="all", help="Target: 'all', 'emulator', or 'phone' (default: all)")
    parser.add_argument("--emulator", "-e", action="store_true", help="Shortcut: target only desktop emulator")
    parser.add_argument("--phone", "-p", action="store_true", help="Shortcut: target only physical smartphone")
    parser.add_argument("--no-emulator", action="store_true", help="Do not auto-launch desktop emulator if physical device is connected")
    parser.add_argument("--avd", type=str, default=DEFAULT_AVD_NAME, help="AVD name to launch if emulator is needed")
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

    # Device detection & multi-target resolution
    target_devices = []

    if args.device:
        target_devices = [args.device]
        print(f"[*] Targeting explicit device: {args.device}")
    else:
        # Self-healing: verify background emulator health before checking devices
        if not args.no_emulator:
            ensure_healthy_emulator_environment(adb_path, args.avd, env)

        connected = get_responsive_devices(adb_path, env)
        has_emulator = any(d.startswith("emulator-") for d in connected)
        has_physical = any(not d.startswith("emulator-") for d in connected)

        # If no emulator is currently running and user hasn't disabled it, launch the desktop emulator
        if not has_emulator and not args.no_emulator:
            print("[*] No desktop emulator online. Starting emulator to enable dual-screen workflow...")
            emulator_path = get_emulator_path(resolved_sdk)
            emu_proc = launch_emulator(emulator_path, args.avd, args.port, env)
            wait_for_emulator(adb_path, env, expected_port=args.port, proc=emu_proc)

        # Re-query all responsive devices (will include both phone and emulator)
        all_responsive = get_responsive_devices(adb_path, env)
        if args.emulator or args.target == "emulator":
            target_devices = [d for d in all_responsive if d.startswith("emulator-")]
            target_filter = "emulator"
        elif args.phone or args.target == "phone":
            target_devices = [d for d in all_responsive if not d.startswith("emulator-")]
            target_filter = "phone"
        else:
            target_devices = all_responsive
            target_filter = "all"

    if not target_devices:
        print("[!] No responsive devices or emulators found matching criteria.")
        sys.exit(1)

    print(f"\n[+] Active Deployment Target(s) [{len(target_devices)}]:")
    for dev in target_devices:
        label = "Desktop Emulator" if dev.startswith("emulator-") else "Physical Smartphone (USB)"
        print(f"    - {dev} -> {label}")

    # Install & Launch concurrently across all devices
    install_and_launch_all(adb_path, target_devices, apk, env)

    if args.logs:
        log_target = target_devices[0]
        print(f"[*] Streaming Logcat from {log_target} for package: {APP_PACKAGE}...")
        subprocess.run([adb_path, "-s", log_target, "logcat", f"{APP_PACKAGE}:V", "*:S"], env=env)
        return

    if not args.no_watch:
        start_watch_loop(adb_path, target_devices, env, auto_discover=(args.device is None), target_filter=target_filter)


if __name__ == "__main__":
    main()
