# Strict Workspace Rules

## 1. Zero Terminal Commands Policy
- **DO NOT run adb commands** (e.g. `adb shell`, `adb exec-out screencap`, `input tap`, `input keyevent`, `am start`, etc.).
- **DO NOT run build or run commands** unless explicitly instructed by the user.
- **Rely on existing watchers**: Background development scripts (such as `android.py`, `website.py`, dev servers) are already running in the terminal and automatically detect file modifications, compile, and reload UI changes instantly on file save.

## 2. Fast Direct Code Modifications
- Focus strictly on modifying source files directly and cleanly using file editing tools.
- Do not over-investigate or run speculative test scripts.
- Make the requested UI/logic changes swiftly and confirm to the user without unnecessary steps.
