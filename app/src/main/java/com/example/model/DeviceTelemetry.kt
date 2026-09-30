package com.example.model

/**
 * Models representing real Android system telemetry gathered directly
 * from official Android APIs (StatFs, BatteryManager, ActivityManager, PowerManager, ConnectivityManager).
 */

enum class HealthStatus {
    HEALTHY,
    WARNING,
    UNAVAILABLE
}

enum class ThermalState {
    COOL,
    NORMAL,
    LIGHT,
    MODERATE,
    SEVERE,
    CRITICAL,
    EMERGENCY,
    SHUTDOWN,
    UNAVAILABLE
}

enum class GameProfile(val label: String, val description: String) {
    PERFORMANCE("Performance", "Focuses telemetry on high-frequency monitoring and prioritizes app foreground responsiveness."),
    BALANCED("Balanced", "Standard telemetry monitoring interval (3-5s) with balanced resource usage."),
    BATTERY_SAVER("Battery Saver", "Throttles background telemetry refresh (10s+) to minimize energy impact.")
}

data class BatteryData(
    val percentage: Int,
    val isCharging: Boolean,
    val chargePlug: String, // AC, USB, Wireless, None
    val temperatureCelsius: Float?, // e.g. 32.5 C
    val voltageMv: Int?, // e.g. 4120 mV
    val health: String, // Good, Overheat, Dead, etc.
    val technology: String, // Li-ion, etc.
    val isPowerSaveMode: Boolean
)

data class StorageData(
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long,
    val availableBytes: Long,
    val usedPercent: Int
)

data class MemoryData(
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val usedRamBytes: Long,
    val isLowMemory: Boolean,
    val thresholdBytes: Long,
    val usedPercent: Int
)

data class ThermalData(
    val status: ThermalState,
    val rawStatusValue: Int?,
    val description: String,
    val isSupported: Boolean
)

data class NetworkData(
    val isConnected: Boolean,
    val typeName: String, // Wi-Fi, Cellular, Ethernet, None
    val isMetered: Boolean,
    val hasInternetCapability: Boolean,
    val isValidated: Boolean,
    val downstreamBandwidthKbps: Int?,
    val upstreamBandwidthKbps: Int?
)

data class DisplayData(
    val refreshRate: Float,
    val supportedRefreshRates: List<Float>,
    val widthPx: Int,
    val heightPx: Int,
    val densityDpi: Int
)

data class SystemInfo(
    val manufacturer: String,
    val model: String,
    val brand: String,
    val device: String,
    val board: String,
    val hardware: String,
    val androidVersion: String,
    val sdkInt: Int,
    val securityPatch: String,
    val supportedAbis: List<String>,
    val buildId: String
)

data class DiagnosticItem(
    val category: String,
    val status: HealthStatus,
    val metric: String,
    val detail: String,
    val recommendation: String
)

data class OptimizationResult(
    val timestamp: Long = System.currentTimeMillis(),
    val memoryFreedEstimateBytes: Long,
    val appCacheClearedBytes: Long,
    val thermalState: ThermalState,
    val batteryPercentage: Int,
    val storageAvailableBytes: Long,
    val networkStatus: String,
    val message: String
)
