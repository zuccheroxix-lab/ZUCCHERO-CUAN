# Changelog

All notable changes to **ZX GAME BOOSTER TOOLS** will be documented in this file.

## [1.0.0] - 2026-09-20

### Initial Production Release
- **Developer**: ZUCCHERO XANN (DEVELOPER)
- **Application ID**: `com.zuccheroxann.zxgamebooster`
- **Architecture**: MVVM with Jetpack Compose and Material Design 3.
- **Direct Home Launch**: Immediate navigation to Dashboard without splash delay.
- **Hardware Telemetry**:
  - Live Battery status, voltage, temperature, technology, and health via `BatteryManager`.
  - Storage space calculation via `StatFs`.
  - Memory analysis using `ActivityManager.MemoryInfo`.
  - Thermal state detection via `PowerManager.getCurrentThermalStatus()` (API 29+).
  - Network transport and bandwidth estimates via `ConnectivityManager`.
  - Display refresh rate, resolution, and density via `Display` / `WindowManager`.
- **Official Safe Quick Optimize**:
  - Application internal cache purging and JVM heap trim via `System.gc()`.
  - Transparent disclosure dialog with honest breakdown.
- **Game Manager**:
  - Room database persistence with favorites, search, and telemetry profiles (Performance, Balanced, Battery Saver).
  - Installed application scanner using privacy-compliant `<queries>` launcher filter.
- **9 Hardware Utility Tools**:
  - Device Info, Battery Info, Storage Info, Network Info, Thermal Info, App Info, Permissions Audit, Diagnostics Scanner, and Export JSON Report.
- **Real-Time Monitor**:
  - Dynamic ticker with configurable 1s/3s/5s/10s intervals and pause/resume capability.
  - Transparent disclosure clarifying the unavailability of in-game FPS counters via standard Android APIs.
- **DevOps**:
  - GitHub Actions `build-apk.yml` and `release-apk.yml` workflows.
  - Robolectric unit tests for all subsystems.
