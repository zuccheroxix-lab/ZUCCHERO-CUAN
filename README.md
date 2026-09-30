# ZX GAME BOOSTER TOOLS

**Production-Ready Android Game Utility & Hardware Monitor**  
**Lead Developer:** ZUCCHERO XANN (DEVELOPER)  
**Package:** `com.zuccheroxann.zxgamebooster`  
**License:** MIT  

---

## Overview

**ZX GAME BOOSTER TOOLS** is a modern Android application engineered to help gamers, power users, and developers monitor device hardware condition and perform safe, legitimate optimizations officially permitted by the Android operating system.

### Core Engineering Principles
1. **100% Real Hardware Telemetry**: All metrics are read directly from Android official APIs (`StatFs`, `BatteryManager`, `ActivityManager`, `PowerManager`, `ConnectivityManager`, and `WindowManager`).
2. **Zero Gimmicks / Zero Fake Data**: No simulated RAM boosting, no fake FPS meters, and no deceptive animation counters. If a capability requires root or is not supported by standard APIs, the application states `"Not supported on this device"` or `"Not available through standard Android APIs"`.
3. **Strict Android Security Compliance**: Operates completely within Android application sandbox boundaries. Never requires Root, Shizuku, or invasive accessibility services.
4. **Neon Brutalism UI / UX**: High-contrast cyberpunk gaming dashboard styling utilizing neon cyan, violet, electric blue, and deep black surfaces with smooth 60/120Hz responsiveness.

---

## Features & Modules

### 1. Direct-to-Home Dashboard
- **Instant Launch**: Starts immediately on the home screen without splash screen delays.
- **Cyber Device Banner**: Displays live manufacturer, model, build ID, and Android release.
- **Official Safe Quick Optimize**:
  - Trims internal application heap memory using `System.gc()`.
  - Clears application internal cache and codeCache directories safely.
  - Refreshes all hardware telemetry metrics.
  - Displays transparent, honest optimization results detailing the exact bytes freed without claiming to kill external apps.
- **Quick Hardware Telemetry Cards**: Real-time snapshots of Battery, RAM, Storage, Thermal status, Network, and Display refresh rate.
- **Quick-Launch Games Shelf**: One-tap access to your favorite installed games.

### 2. Game Manager (`GAMES` Tab)
- **Automatic Installed App Scanner**: Discovers installed games and launcher applications using privacy-friendly `<queries>` category matching.
- **Add / Remove Games**: Manage your personal game library persisted locally with Android Jetpack Room.
- **Search & Filter**: Real-time search query filtering by title or package name.
- **Telemetry Profiles**:
  - **Performance**: Prioritizes high-frequency monitoring loop during gaming sessions.
  - **Balanced**: Standard monitoring cadence (3-5s).
  - **Battery Saver**: Low-cadence monitoring (10s+) to conserve energy.
- **Safe Launcher**: Direct launch via `PackageManager.getLaunchIntentForPackage()` with graceful error handling if an app was uninstalled or disabled.

### 3. Hardware & Utility Tools (`TOOLS` Tab)
Nine utility tools:
1. **Device Info**: Manufacturer, model, brand, board, hardware SoC, Android version, SDK level, security patch, ABIs, and resolution.
2. **Battery Info**: Battery level, charging plug status (AC, USB, Wireless), live temperature, voltage (mV), battery health, and power-saver status.
3. **Storage Info**: Exact internal partition metrics calculated via `StatFs` (Total, Used, Available bytes and usage percentage).
4. **Network Info**: Active transport type (Wi-Fi, Cellular, Ethernet), metered connection status, validated internet capability, and downstream/upstream link estimates.
5. **Thermal Info**: Hardware thermal throttle status via Android 10+ (`PowerManager.getCurrentThermalStatus()`) with clear explanation of states (COOL, NORMAL, LIGHT, MODERATE, SEVERE, CRITICAL).
6. **App & Runtime Info**: App version name, target SDK, JVM heap memory usage, and runtime environment.
7. **Permission Info**: Transparent security audit of permissions declared by the app.
8. **Diagnostics Scanner**: Multi-system diagnostic scan evaluating Battery, Storage, Memory, Thermal, and Network health with `HEALTHY`, `WARNING`, and `UNAVAILABLE` badges.
9. **Export Diagnostic Report**: Compiles full system telemetry into structured JSON and triggers the native Android Sharesheet for export.

### 4. Real-Time Hardware Monitor (`MONITOR` Tab)
- **Live Hardware Ticker**: Configurable ticker interval (1s, 3s, 5s, 10s) updating RAM usage, battery state, storage headroom, and thermals in real time.
- **Pause & Resume Controls**: Freeze or resume the telemetry ticker at will.
- **Honest FPS Disclosure Notice**: Prominent brutalist notice explaining that in-game FPS telemetry requires root/adb frame capture and that ZX Game Booster does not fabricate simulated frame rates.

### 5. Settings & Privacy (`SETTINGS` Tab)
- **Telemetry Preferences**: Configure monitor intervals, auto-refresh, and UI animations.
- **Zero Cloud Uploads**: 100% offline-first architecture. No personal telemetry or hardware profiles are uploaded to remote servers.
- **Reset Application Data**: Clear added game entries and restore preferences.
- **Developer Credit**: `ZUCCHERO XANN (DEVELOPER)`.

---

## Technical Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose & Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Local Persistence**: Jetpack Room (`AppDatabase`, `GameDao`)
- **Key-Value Persistence**: Jetpack DataStore Preferences
- **Asynchronous Flow**: Kotlin Coroutines & `StateFlow`
- **Testing**: JUnit4, Robolectric, Roborazzi
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 15+)

---

## DevOps & GitHub Actions CI/CD

This repository includes pre-configured GitHub Actions workflows:

1. **`build-apk.yml`**:
   - Runs automatically on pull requests and pushes to `main` / `master`.
   - Executes JVM unit tests (`testDebugUnitTest`).
   - Builds debug APK and uploads artifacts for download.
2. **`release-apk.yml`**:
   - Triggers when a git tag matching `v*` (e.g. `v1.0.0`) is pushed.
   - Signs release APK using GitHub repository secrets if configured (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`).
   - Generates release APK and Android App Bundle (`.aab`).
   - Creates a GitHub Release with downloadable release binaries.

---

## Author & Developer

**ZUCCHERO XANN (DEVELOPER)**  
ZX Game Booster Tools - 2026
