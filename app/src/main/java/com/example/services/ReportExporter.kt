package com.example.services

import android.content.Context
import android.content.Intent
import com.example.model.BatteryData
import com.example.model.DisplayData
import com.example.model.MemoryData
import com.example.model.NetworkData
import com.example.model.StorageData
import com.example.model.SystemInfo
import com.example.model.ThermalData
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportExporter(private val context: Context) {

    fun generateJsonReport(
        system: SystemInfo,
        battery: BatteryData,
        storage: StorageData,
        memory: MemoryData,
        thermal: ThermalData,
        network: NetworkData,
        display: DisplayData
    ): String {
        val root = JSONObject()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
        root.put("report_title", "ZX GAME BOOSTER TOOLS - HARDWARE DIAGNOSTICS REPORT")
        root.put("app_name", "ZX GAME BOOSTER TOOLS")
        root.put("version", "1.0.0")
        root.put("developer", "ZUCCHERO XANN (DEVELOPER)")
        root.put("timestamp", isoFormat.format(Date()))
        root.put("epoch_ms", System.currentTimeMillis())

        val sysObj = JSONObject().apply {
            put("manufacturer", system.manufacturer)
            put("model", system.model)
            put("brand", system.brand)
            put("device", system.device)
            put("hardware", system.hardware)
            put("board", system.board)
            put("android_version", system.androidVersion)
            put("sdk_int", system.sdkInt)
            put("security_patch", system.securityPatch)
            put("build_id", system.buildId)
            val abisArray = JSONArray()
            system.supportedAbis.forEach { abisArray.put(it) }
            put("supported_abis", abisArray)
        }
        root.put("system_info", sysObj)

        val batObj = JSONObject().apply {
            put("percentage", battery.percentage)
            put("is_charging", battery.isCharging)
            put("plug_type", battery.chargePlug)
            put("temperature_celsius", battery.temperatureCelsius ?: JSONObject.NULL)
            put("voltage_mv", battery.voltageMv ?: JSONObject.NULL)
            put("health", battery.health)
            put("technology", battery.technology)
            put("power_save_mode", battery.isPowerSaveMode)
        }
        root.put("battery", batObj)

        val storObj = JSONObject().apply {
            put("total_bytes", storage.totalBytes)
            put("available_bytes", storage.availableBytes)
            put("used_bytes", storage.usedBytes)
            put("used_percent", storage.usedPercent)
        }
        root.put("storage", storObj)

        val memObj = JSONObject().apply {
            put("total_ram_bytes", memory.totalRamBytes)
            put("available_ram_bytes", memory.availableRamBytes)
            put("used_ram_bytes", memory.usedRamBytes)
            put("is_low_memory", memory.isLowMemory)
            put("threshold_bytes", memory.thresholdBytes)
            put("used_percent", memory.usedPercent)
        }
        root.put("memory", memObj)

        val thermObj = JSONObject().apply {
            put("status", thermal.status.name)
            put("raw_status_value", thermal.rawStatusValue ?: JSONObject.NULL)
            put("description", thermal.description)
            put("is_supported", thermal.isSupported)
        }
        root.put("thermal", thermObj)

        val netObj = JSONObject().apply {
            put("is_connected", network.isConnected)
            put("type_name", network.typeName)
            put("is_metered", network.isMetered)
            put("has_internet_capability", network.hasInternetCapability)
            put("is_validated", network.isValidated)
            put("downstream_kbps", network.downstreamBandwidthKbps ?: JSONObject.NULL)
            put("upstream_kbps", network.upstreamBandwidthKbps ?: JSONObject.NULL)
        }
        root.put("network", netObj)

        val dispObj = JSONObject().apply {
            put("refresh_rate_hz", display.refreshRate)
            val modesArray = JSONArray()
            display.supportedRefreshRates.forEach { modesArray.put(it) }
            put("supported_refresh_rates", modesArray)
            put("resolution_width_px", display.widthPx)
            put("resolution_height_px", display.heightPx)
            put("density_dpi", display.densityDpi)
        }
        root.put("display", dispObj)

        root.put("compliance_notice", "All telemetry retrieved strictly via official Android public APIs. Zero root, Shizuku, or process injection.")

        return root.toString(2)
    }

    fun shareReport(jsonString: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, jsonString)
            putExtra(Intent.EXTRA_SUBJECT, "ZX Game Booster - Device Report")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Device Diagnostic Report").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }
}
