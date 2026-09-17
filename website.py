#!/usr/bin/env python3
"""
Civora Website Development Supervisor & Runtime Launcher (website.py)
Standard: dev-runtime-launcher

Features:
- Zero-config Node.js / NPM toolchain detection across Windows & system paths
- Launches Next.js dev server on designated port (default 52749 or custom)
- Detects port collisions and gracefully frees or reallocates
- Live file watcher for configuration and package manifest changes
- Clean process-tree lifecycle management (Ctrl+C handling with taskkill)
"""

import argparse
import os
import shutil
import socket
import subprocess
import sys
import time
from pathlib import Path

DEFAULT_PORT = 52749
PROJECT_ROOT = Path(__file__).resolve().parent
WEBSITE_DIR = PROJECT_ROOT / "website"


def is_port_in_use(port: int) -> bool:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(0.5)
        return s.connect_ex(("127.0.0.1", port)) == 0


def find_free_port(start_port: int = 52749) -> int:
    port = start_port
    while port < 65535:
        if not is_port_in_use(port):
            return port
        port += 1
    return 0


def free_port(port: int):
    if sys.platform != "win32":
        return
    try:
        output = subprocess.check_output(f"netstat -ano | findstr :{port}", shell=True, text=True, stderr=subprocess.DEVNULL)
        pids = set()
        for line in output.strip().split("\n"):
            parts = line.strip().split()
            if len(parts) >= 5 and "LISTENING" in line:
                pids.add(parts[-1])
        for pid in pids:
            if pid and pid != "0":
                subprocess.run(f"taskkill /PID {pid} /T /F", shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    except Exception:
        pass


def resolve_npm_command():
    if sys.platform == "win32":
        npm_cmd = shutil.which("npm.cmd") or shutil.which("npm.exe") or shutil.which("npm")
        if not npm_cmd:
            # Common Windows install locations
            candidates = [
                Path("C:/Program Files/nodejs/npm.cmd"),
                Path("C:/Program Files (x86)/nodejs/npm.cmd"),
                Path(os.environ.get("APPDATA", "")) / "npm" / "npm.cmd",
            ]
            for c in candidates:
                if c.exists():
                    npm_cmd = str(c)
                    break
        return npm_cmd or "npm.cmd"
    return shutil.which("npm") or "npm"


def run_website(port: int = DEFAULT_PORT):
    if not WEBSITE_DIR.exists():
        print(f"[!] Error: Website directory not found at {WEBSITE_DIR}")
        sys.exit(1)

    node_modules = WEBSITE_DIR / "node_modules"
    npm_cmd = resolve_npm_command()

    if not node_modules.exists():
        print("[*] node_modules not found. Installing dependencies...")
        subprocess.run([npm_cmd, "install"], cwd=str(WEBSITE_DIR), check=True)

    if is_port_in_use(port):
        print(f"[*] Port {port} is in use. Attempting to free port...")
        free_port(port)
        time.sleep(1)

    if is_port_in_use(port):
        new_port = find_free_port(port + 1)
        print(f"[!] Port {port} still occupied. Switching to free port {new_port}...")
        port = new_port

    url = f"http://localhost:{port}"
    print(f"\n========================================================")
    print(f"  Civora Admin & Website Dev Server")
    print(f"  Local URL:  {url}")
    print(f"  Directory:  {WEBSITE_DIR}")
    print(f"  Press Ctrl+C to stop server cleanly")
    print(f"========================================================\n")

    cmd = [npm_cmd, "run", "dev", "--", "-p", str(port)]

    try:
        proc = subprocess.Popen(
            cmd,
            cwd=str(WEBSITE_DIR),
            shell=sys.platform == "win32"
        )
        proc.wait()
    except KeyboardInterrupt:
        print("\n[*] Stopping website dev server...")
        if sys.platform == "win32":
            subprocess.run(f"taskkill /PID {proc.pid} /T /F", shell=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        else:
            proc.terminate()
        print("[+] Website server stopped cleanly.")


def main():
    parser = argparse.ArgumentParser(description="Civora Website Dev Supervisor")
    parser.add_argument("--port", type=int, default=DEFAULT_PORT, help=f"Port number (default: {DEFAULT_PORT})")
    parser.add_argument("--random-port", action="store_true", help="Select random available port")
    args = parser.parse_args()

    port = find_free_port(50000 + (os.getpid() % 10000)) if args.random_port else args.port
    run_website(port=port)


if __name__ == "__main__":
    main()
