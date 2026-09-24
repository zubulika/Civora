# Strict Workspace Rules

## 1. Zero Terminal Commands Policy
- **DO NOT run adb commands** (`adb shell`, `adb exec-out screencap`, `input tap`, `input keyevent`, `am start`, etc.).
- **DO NOT run build or run commands** unless explicitly instructed by the user.
- **Rely on existing watchers**: Background development scripts (`android.py`, `website.py`, dev servers) are already running in the terminal and automatically detect file modifications, compile, and reload UI changes instantly on file save.

## 2. Direct Source Modifications Only
- Focus exclusively on making direct code modifications using file editing tools.
- Never run redundant shell commands or verification scripts.
- Keep implementation immediate and minimal.
