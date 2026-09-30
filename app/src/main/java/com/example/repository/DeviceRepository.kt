package com.example.repository

import com.example.model.BatteryData
import com.example.model.DiagnosticItem
import com.example.model.DisplayData
import com.example.model.MemoryData
import com.example.model.NetworkData
import com.example.model.OptimizationResult
import com.example.model.StorageData
import com.example.model.SystemInfo
import com.example.model.ThermalData
import com.example.services.DeviceTelemetryService
import com.example.services.ReportExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class FullDeviceTelemetry(
    val system: SystemInfo,
    val battery: BatteryData,
    val storage: StorageData,
    val memory: MemoryData,
    val thermal: ThermalData,
    val network: NetworkData,
    val display: DisplayData,
    val timestamp: Long = System.currentTimeMillis()
)

class DeviceRepository(
    private val telemetryService: DeviceTelemetryService,
    private val reportExporter: ReportExporter
) {
    suspend fun getFullTelemetry(): FullDeviceTelemetry {
        return withContext(Dispatchers.IO) {
            FullDeviceTelemetry(
                system = telemetryService.getSystemInfo(),
                battery = telemetryService.getBatteryData(),
                storage = telemetryService.getStorageData(),
                memory = telemetryService.getMemoryData(),
                thermal = telemetryService.getThermalData(),
                network = telemetryService.getNetworkData(),
                display = telemetryService.getDisplayData(),
                timestamp = System.currentTimeMillis()
            )
        }
    }

    suspend fun runDiagnostics(): List<DiagnosticItem> {
        return withContext(Dispatchers.IO) {
            telemetryService.runDiagnostics()
        }
    }

    suspend fun performSafeOptimization(): OptimizationResult {
        return withContext(Dispatchers.IO) {
            telemetryService.performSafeOptimization()
        }
    }

    suspend fun generateAndExportReport(): String {
        return withContext(Dispatchers.IO) {
            val t = getFullTelemetry()
            val json = reportExporter.generateJsonReport(
                system = t.system,
                battery = t.battery,
                storage = t.storage,
                memory = t.memory,
                thermal = t.thermal,
                network = t.network,
                display = t.display
            )
            reportExporter.shareReport(json)
            json
        }
    }

    suspend fun generateReportJson(): String {
        return withContext(Dispatchers.IO) {
            val t = getFullTelemetry()
            reportExporter.generateJsonReport(
                system = t.system,
                battery = t.battery,
                storage = t.storage,
                memory = t.memory,
                thermal = t.thermal,
                network = t.network,
                display = t.display
            )
        }
    }
}
