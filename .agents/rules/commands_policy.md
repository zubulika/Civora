# Terminal Commands Policy

- **NEVER execute adb commands** (`adb shell`, `adb exec-out screencap`, `input tap`, `input keyevent`, `am start`, etc.).
- **NEVER execute build or run commands** unless explicitly instructed by the user.
- The user has background supervisor scripts (e.g., `android.py`, `website.py`) running in their terminal that automatically hot-reload and update on every file save.
- All tasks must be done strictly through direct file edits.
