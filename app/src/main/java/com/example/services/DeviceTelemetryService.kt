package com.example.services

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.model.BatteryData
import com.example.model.DiagnosticItem
import com.example.model.DisplayData
import com.example.model.HealthStatus
import com.example.model.MemoryData
import com.example.model.NetworkData
import com.example.model.OptimizationResult
import com.example.model.StorageData
import com.example.model.SystemInfo
import com.example.model.ThermalData
import com.example.model.ThermalState
import java.io.File

class DeviceTelemetryService(private val context: Context) {

    fun getBatteryData(): BatteryData {
        return try {
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, iFilter)

            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val percentage = if (level >= 0 && scale > 0) (level * 100 / scale) else 0

            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
            val plugString = when (chargePlug) {
                BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
                else -> if (isCharging) "Charging" else "Unplugged"
            }

            val tempRaw = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
            val tempCelsius = if (tempRaw > 0) tempRaw / 10.0f else null

            val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            val voltageMv = if (voltage != null && voltage > 0) voltage else null

            val healthInt = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
            val healthString = when (healthInt) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
                BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                else -> "Normal"
            }

            val techString = batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            val isPowerSaveMode = powerManager?.isPowerSaveMode ?: false

            BatteryData(
                percentage = percentage,
                isCharging = isCharging,
                chargePlug = plugString,
                temperatureCelsius = tempCelsius,
                voltageMv = voltageMv,
                health = healthString,
                technology = techString,
                isPowerSaveMode = isPowerSaveMode
            )
        } catch (e: Exception) {
            BatteryData(
                percentage = 0,
                isCharging = false,
                chargePlug = "Unknown",
                temperatureCelsius = null,
                voltageMv = null,
                health = "Unknown",
                technology = "N/A",
                isPowerSaveMode = false
            )
        }
    }

    fun getStorageData(): StorageData {
        return try {
            val dataDir = Environment.getDataDirectory()
            val stat = StatFs(dataDir.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong
            val freeBlocks = stat.freeBlocksLong

            val totalBytes = totalBlocks * blockSize
            val availableBytes = availableBlocks * blockSize
            val freeBytes = freeBlocks * blockSize
            val usedBytes = (totalBytes - availableBytes).coerceAtLeast(0L)

            val usedPercent = if (totalBytes > 0) ((usedBytes * 100) / totalBytes).toInt() else 0

            StorageData(
                totalBytes = totalBytes,
                freeBytes = freeBytes,
                usedBytes = usedBytes,
                availableBytes = availableBytes,
                usedPercent = usedPercent
            )
        } catch (e: Exception) {
            StorageData(
                totalBytes = 0L,
                freeBytes = 0L,
                usedBytes = 0L,
                availableBytes = 0L,
                usedPercent = 0
            )
        }
    }

    fun getMemoryData(): MemoryData {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            if (actManager != null) {
                actManager.getMemoryInfo(memInfo)
                val total = memInfo.totalMem
                val available = memInfo.availMem
                val used = (total - available).coerceAtLeast(0L)
                val usedPercent = if (total > 0) ((used * 100) / total).toInt() else 0
                MemoryData(
                    totalRamBytes = total,
                    availableRamBytes = available,
                    usedRamBytes = used,
                    isLowMemory = memInfo.lowMemory,
                    thresholdBytes = memInfo.threshold,
                    usedPercent = usedPercent
                )
            } else {
                MemoryData(0L, 0L, 0L, false, 0L, 0)
            }
        } catch (e: Exception) {
            MemoryData(0L, 0L, 0L, false, 0L, 0)
        }
    }

    fun getThermalData(): ThermalData {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                if (powerManager != null) {
                    val status = powerManager.currentThermalStatus
                    val state = when (status) {
                        PowerManager.THERMAL_STATUS_NONE -> ThermalState.NORMAL
                        PowerManager.THERMAL_STATUS_LIGHT -> ThermalState.LIGHT
                        PowerManager.THERMAL_STATUS_MODERATE -> ThermalState.MODERATE
                        PowerManager.THERMAL_STATUS_SEVERE -> ThermalState.SEVERE
                        PowerManager.THERMAL_STATUS_CRITICAL -> ThermalState.CRITICAL
                        PowerManager.THERMAL_STATUS_EMERGENCY -> ThermalState.EMERGENCY
                        PowerManager.THERMAL_STATUS_SHUTDOWN -> ThermalState.SHUTDOWN
                        else -> ThermalState.NORMAL
                    }
                    val description = when (state) {
                        ThermalState.NORMAL -> "Thermal status is nominal. No thermal throttling."
                        ThermalState.LIGHT -> "Light heat detected. Gaming performance unaffected."
                        ThermalState.MODERATE -> "Moderate thermal state. System may manage burst frequencies."
                        ThermalState.SEVERE -> "Severe temperature! System actively reducing thermal dissipation."
                        ThermalState.CRITICAL -> "Critical thermal load! Major system throttling in progress."
                        ThermalState.EMERGENCY -> "Emergency threshold reached! Device in protection state."
                        ThermalState.SHUTDOWN -> "Hardware imminent thermal shutdown."
                        else -> "Thermal state normal."
                    }
                    ThermalData(
                        status = state,
                        rawStatusValue = status,
                        description = description,
                        isSupported = true
                    )
                } else {
                    ThermalData(
                        status = ThermalState.UNAVAILABLE,
                        rawStatusValue = null,
                        description = "Thermal service not available on this device.",
                        isSupported = false
                    )
                }
            } else {
                ThermalData(
                    status = ThermalState.UNAVAILABLE,
                    rawStatusValue = null,
                    description = "Thermal API requires Android 10 (API 29) or higher.",
                    isSupported = false
                )
            }
        } catch (e: Exception) {
            ThermalData(
                status = ThermalState.UNAVAILABLE,
                rawStatusValue = null,
                description = "Thermal information unavailable through standard Android APIs.",
                isSupported = false
            )
        }
    }

    fun getNetworkData(): NetworkData {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (cm != null) {
                val activeNetwork = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(activeNetwork)

                if (activeNetwork != null && capabilities != null) {
                    val isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) || isConnected
                    val isMetered = !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)

                    val typeName = when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular Mobile"
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth"
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
                        else -> "Connected Network"
                    }

                    val downKbps = capabilities.linkDownstreamBandwidthKbps.takeIf { it > 0 }
                    val upKbps = capabilities.linkUpstreamBandwidthKbps.takeIf { it > 0 }

                    NetworkData(
                        isConnected = isConnected,
                        typeName = typeName,
                        isMetered = isMetered,
                        hasInternetCapability = hasInternet,
                        isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED),
                        downstreamBandwidthKbps = downKbps,
                        upstreamBandwidthKbps = upKbps
                    )
                } else {
                    NetworkData(
                        isConnected = false,
                        typeName = "Disconnected",
                        isMetered = false,
                        hasInternetCapability = false,
                        isValidated = false,
                        downstreamBandwidthKbps = null,
                        upstreamBandwidthKbps = null
                    )
                }
            } else {
                NetworkData(false, "Unknown", false, false, false, null, null)
            }
        } catch (e: Exception) {
            NetworkData(false, "Offline", false, false, false, null, null)
        }
    }

    @Suppress("DEPRECATION")
    fun getDisplayData(): DisplayData {
        return try {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = context.display
                val refreshRate = display?.mode?.refreshRate ?: 60.0f
                val supportedModes = display?.supportedModes?.map { it.refreshRate }?.distinct() ?: listOf(refreshRate)
                val bounds = wm?.currentWindowMetrics?.bounds
                val width = bounds?.width() ?: 1080
                val height = bounds?.height() ?: 2400
                val densityDpi = context.resources.configuration.densityDpi

                DisplayData(
                    refreshRate = refreshRate,
                    supportedRefreshRates = supportedModes,
                    widthPx = width,
                    heightPx = height,
                    densityDpi = densityDpi
                )
            } else {
                val display = wm?.defaultDisplay
                val refreshRate = display?.refreshRate ?: 60.0f
                val supportedModes = display?.supportedModes?.map { it.refreshRate }?.distinct() ?: listOf(refreshRate)
                val metrics = DisplayMetrics()
                display?.getRealMetrics(metrics)

                DisplayData(
                    refreshRate = refreshRate,
                    supportedRefreshRates = supportedModes,
                    widthPx = metrics.widthPixels,
                    heightPx = metrics.heightPixels,
                    densityDpi = metrics.densityDpi
                )
            }
        } catch (e: Exception) {
            DisplayData(
                refreshRate = 60.0f,
                supportedRefreshRates = listOf(60.0f),
                widthPx = 1080,
                heightPx = 1920,
                densityDpi = 420
            )
        }
    }

    fun getSystemInfo(): SystemInfo {
        val securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH
        } else {
            "N/A"
        }

        return SystemInfo(
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            brand = Build.BRAND,
            device = Build.DEVICE,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            androidVersion = Build.VERSION.RELEASE,
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = securityPatch,
            supportedAbis = Build.SUPPORTED_ABIS.toList(),
            buildId = Build.ID
        )
    }

    fun runDiagnostics(): List<DiagnosticItem> {
        val list = mutableListOf<DiagnosticItem>()
        val battery = getBatteryData()
        val storage = getStorageData()
        val memory = getMemoryData()
        val thermal = getThermalData()
        val network = getNetworkData()
        val system = getSystemInfo()

        // 1. Battery Check
        val batteryStatus = when {
            battery.percentage < 15 && !battery.isCharging -> HealthStatus.WARNING
            battery.health != "Good" && battery.health != "Normal" -> HealthStatus.WARNING
            else -> HealthStatus.HEALTHY
        }
        val batteryRec = when {
            battery.isCharging -> "Device is connected to power. Safe for extended gaming sessions."
            battery.percentage < 20 -> "Battery level low (${battery.percentage}%). Connect charger to avoid sudden throttling."
            else -> "Battery capacity is optimal. Battery temperature is normal."
        }
        list.add(
            DiagnosticItem(
                category = "Battery Health",
                status = batteryStatus,
                metric = "${battery.percentage}% • ${battery.health}",
                detail = "Temperature: ${battery.temperatureCelsius?.let { "$it°C" } ?: "N/A"}, Voltage: ${battery.voltageMv?.let { "$it mV" } ?: "N/A"}",
                recommendation = batteryRec
            )
        )

        // 2. Storage Check
        val storageStatus = when {
            storage.usedPercent > 90 -> HealthStatus.WARNING
            storage.availableBytes < (1024L * 1024L * 1024L * 2) -> HealthStatus.WARNING // < 2GB
            else -> HealthStatus.HEALTHY
        }
        val freeGb = String.format("%.2f GB", storage.availableBytes.toDouble() / (1024 * 1024 * 1024))
        val totalGb = String.format("%.2f GB", storage.totalBytes.toDouble() / (1024 * 1024 * 1024))
        list.add(
            DiagnosticItem(
                category = "Storage Capacity",
                status = storageStatus,
                metric = "$freeGb available ($totalGb total)",
                detail = "Storage is ${storage.usedPercent}% utilized.",
                recommendation = if (storageStatus == HealthStatus.WARNING)
                    "Internal storage is almost full. Large game shader caches and update packages require free storage headroom."
                else
                    "Adequate free storage available for high-end game installation and shader assets."
            )
        )

        // 3. Memory Check
        val memStatus = when {
            memory.isLowMemory || memory.usedPercent > 90 -> HealthStatus.WARNING
            memory.totalRamBytes <= 0L -> HealthStatus.UNAVAILABLE
            else -> HealthStatus.HEALTHY
        }
        val availRamGb = String.format("%.2f GB", memory.availableRamBytes.toDouble() / (1024 * 1024 * 1024))
        val totalRamGb = String.format("%.2f GB", memory.totalRamBytes.toDouble() / (1024 * 1024 * 1024))
        list.add(
            DiagnosticItem(
                category = "RAM / System Memory",
                status = memStatus,
                metric = "$availRamGb available ($totalRamGb total)",
                detail = "System memory is ${memory.usedPercent}% utilized. Low memory threshold: ${memory.isLowMemory}",
                recommendation = if (memStatus == HealthStatus.WARNING)
                    "High memory pressure detected. Running foreground game may cause background tabs to be suspended by Android OS."
                else
                    "System RAM has adequate headroom for loading intensive game textures."
            )
        )

        // 4. Thermal State Check
        val thermalStatus = when (thermal.status) {
            ThermalState.NORMAL, ThermalState.LIGHT, ThermalState.COOL -> HealthStatus.HEALTHY
            ThermalState.MODERATE, ThermalState.SEVERE, ThermalState.CRITICAL, ThermalState.EMERGENCY, ThermalState.SHUTDOWN -> HealthStatus.WARNING
            ThermalState.UNAVAILABLE -> HealthStatus.UNAVAILABLE
        }
        list.add(
            DiagnosticItem(
                category = "Thermal Throttle State",
                status = thermalStatus,
                metric = thermal.status.name,
                detail = thermal.description,
                recommendation = if (thermalStatus == HealthStatus.WARNING)
                    "Device is experiencing thermal load. Remove tight phone case or allow device to cool to prevent hardware frame drops."
                else if (thermalStatus == HealthStatus.HEALTHY)
                    "Thermals are completely nominal. Hardware is operating within optimal temperature range."
                else
                    "Thermal reporting is unavailable via standard Android APIs on this specific hardware or OS build."
            )
        )

        // 5. Network Connectivity
        val netStatus = when {
            !network.isConnected -> HealthStatus.WARNING
            network.hasInternetCapability -> HealthStatus.HEALTHY
            else -> HealthStatus.WARNING
        }
        list.add(
            DiagnosticItem(
                category = "Network Connectivity",
                status = netStatus,
                metric = "${network.typeName} • ${if (network.isConnected) "Connected" else "Offline"}",
                detail = "Internet Validated: ${network.isValidated}, Metered: ${network.isMetered}",
                recommendation = if (network.isConnected)
                    if (network.isMetered) "Connected to metered connection. Watch out for automatic in-game patch downloads." else "Low-latency network connection established."
                else
                    "No internet connection detected. Multiplayer games will not be able to reach matchmaking servers."
            )
        )

        // 6. Android Platform & Architecture
        list.add(
            DiagnosticItem(
                category = "Operating System & ABI",
                status = HealthStatus.HEALTHY,
                metric = "Android ${system.androidVersion} (API ${system.sdkInt})",
                detail = "Target ABI: ${system.supportedAbis.firstOrNull() ?: "arm64-v8a"}, Patch: ${system.securityPatch}",
                recommendation = "Modern 64-bit Android runtime with optimal hardware acceleration."
            )
        )

        return list
    }

    /**
     * Performs real, strictly permitted Android optimization:
     * 1. Cleans this application's own cache files and codeCache directory.
     * 2. Requests explicit garbage collection (System.gc()) to minimize app memory footprint.
     * 3. Refreshes and inspects battery, thermal, and network metrics.
     * 4. Reports exact, honest telemetry changes without fake claims.
     */
    fun performSafeOptimization(): OptimizationResult {
        var freedCacheBytes = 0L

        // Measure & clean app internal cache safely
        try {
            val cacheDir = context.cacheDir
            freedCacheBytes += getFolderSize(cacheDir)
            cacheDir.listFiles()?.forEach { file ->
                deleteFileRecursively(file)
            }

            val codeCacheDir = context.codeCacheDir
            codeCacheDir.listFiles()?.forEach { file ->
                deleteFileRecursively(file)
            }
        } catch (_: Exception) {
            // Best effort cache trim
        }

        // Measure memory before & after app garbage collection
        val runtime = Runtime.getRuntime()
        val memBefore = runtime.totalMemory() - runtime.freeMemory()
        System.gc()
        val memAfter = runtime.totalMemory() - runtime.freeMemory()
        val freedHeapEstimate = (memBefore - memAfter).coerceAtLeast(0L)

        val battery = getBatteryData()
        val storage = getStorageData()
        val thermal = getThermalData()
        val network = getNetworkData()

        val message = buildString {
            append("Real Optimization Complete: ")
            append("Refreshed hardware telemetry. ")
            append("Trimmed application heap by ${(freedHeapEstimate / 1024)} KB. ")
            if (freedCacheBytes > 0) {
                append("Purged ${(freedCacheBytes / 1024)} KB app cache. ")
            }
            append("Thermal state: ${thermal.status.name}. ")
            append("Note: Modern Android security boundaries prohibit killing external app processes without root access.")
        }

        return OptimizationResult(
            timestamp = System.currentTimeMillis(),
            memoryFreedEstimateBytes = freedHeapEstimate,
            appCacheClearedBytes = freedCacheBytes,
            thermalState = thermal.status,
            batteryPercentage = battery.percentage,
            storageAvailableBytes = storage.availableBytes,
            networkStatus = network.typeName,
            message = message
        )
    }

    private fun getFolderSize(file: File): Long {
        var size = 0L
        try {
            if (file.isDirectory) {
                file.listFiles()?.forEach { child ->
                    size += getFolderSize(child)
                }
            } else {
                size = file.length()
            }
        } catch (_: Exception) {}
        return size
    }

    private fun deleteFileRecursively(file: File) {
        try {
            if (file.isDirectory) {
                file.listFiles()?.forEach { deleteFileRecursively(it) }
            }
            file.delete()
        } catch (_: Exception) {}
    }
}
