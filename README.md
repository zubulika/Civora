# Civora (Android App)

Civora is an original concept public-service mobile application inspired by modern government service platforms such as **Absher**, the official digital services platform of the Saudi Ministry of Interior.

This project explores clean, authoritative public-service mobile UI patterns, digital document wallets, service directories, and citizen workflow trackers.

> **Disclaimer**: Civora is a concept project created for educational and portfolio exploration. It is **not affiliated with, endorsed by, or connected to** Absher, the Saudi Ministry of Interior, or any government entity.

---

## 🏛️ Key Features

- **Dashboard & Citizen Identity**: Welcome banner with verified citizen status, National ID quick preview card, and active service request summary.
- **Unified Services Directory**: Comprehensive directory across Civil Affairs, Traffic & Vehicles, Passports & Travel, and Security Clearances, complete with search, category filtering, prerequisites, and instant request submission.
- **Digital Document Wallet**: Authenticated digital documents (Digital National ID, Driver License, Vehicle Istimara, Citizen E-Passport) with expandable official details and encrypted QR verification tokens.
- **Service Request Tracker**: Real-time multi-step timeline milestone tracker for citizen requests with status badges.
- **Profile & Security Settings**: Identity details, Biometric Passkey toggle, SMS transaction alerts, and legal disclaimers.
- **Notifications & Alerts**: Official critical, warning, and informational system alerts with read status tracking.

---

## 📱 Tech Stack & Architecture

- **Platform**: Native Android (Min SDK 26, Target/Compile SDK 35)
- **Language**: Kotlin 2.1.0
- **UI Framework**: Jetpack Compose with Material 3 Design System
- **Architecture**: Clean Architecture (Feature-based: Presentation, Domain, Data, Core)
- **Backend & Cloud**: Firebase (Cloud Firestore Native mode, Firebase Auth, Firebase Storage, Firebase Rules)
- **Tooling**: Gradle 8.11.1, Android Gradle Plugin 8.8.0, Kotlin Compose Compiler Plugin
- **Dev Supervisor**: `android.py` (zero-config toolchain resolution, emulator lifecycle, and live file-watch reload)

### Project Layout

```
Civora/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/civora/app/
│   │   │   ├── CivoraApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── core/
│   │   │   │   ├── designsystem/   # Colors, Typography, Shapes, Theme
│   │   │   │   ├── components/     # TopBar, BottomBar, Cards, Buttons, StatusBadges
│   │   │   │   ├── model/          # UserProfile, DigitalDocument, GovernmentService, etc.
│   │   │   │   └── di/             # AppContainer dependency provider
│   │   │   ├── data/
│   │   │   │   ├── mock/           # Realistic seed data
│   │   │   │   └── repository/     # User, Document, Service, Request repositories
│   │   │   ├── domain/
│   │   │   │   └── usecase/        # Dashboard, Services, Documents, Requests use cases
│   │   │   ├── navigation/         # Screen definitions, NavHost, BottomBar routing
│   │   │   └── presentation/       # Dashboard, Services, Wallet, Requests, Profile, Notifications
│   │   └── res/                    # Drawables, mipmaps, strings, colors, themes
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml          # Gradle Version Catalog
│   └── wrapper/                    # Gradle 8.11.1 wrapper
├── android.py                      # Development supervisor & auto-reloader
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── local.properties
```

---

## 🚀 Getting Started

### 1. Zero-Config Development (Recommended)

Run the automated dev supervisor script:

```bash
# Build, launch emulator (Pixel_9_Pro_XL), install APK, and start live file watching:
python android.py

# Build APK only (without launching emulator):
python android.py --build-only

# Build and launch once without file watching:
python android.py --no-watch

# Stream filtered logcat logs:
python android.py --logs
```

### 2. Standard Gradle Build

```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

Compiled APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License

MIT License. Designed and built for portfolio and educational purposes.
