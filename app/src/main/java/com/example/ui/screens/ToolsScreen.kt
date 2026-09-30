package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HealthStatus
import com.example.model.ThermalState
import com.example.ui.components.BrutalistCard
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.NeonSectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiState

data class ToolDefinition(
    val id: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun ToolsScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry

    val toolsList = listOf(
        ToolDefinition(0, "Device Info", "Manufacturer, model, board, ABI architecture & screen resolution", Icons.Default.PhoneAndroid, NeonCyan),
        ToolDefinition(1, "Battery Info", "Battery health, percentage, voltage, temperature & charger state", Icons.Default.BatteryChargingFull, NeonGreen),
        ToolDefinition(2, "Storage Info", "Internal storage capacity, used space & block metrics via StatFs", Icons.Default.SdStorage, ElectricBlue),
        ToolDefinition(3, "Network Info", "Connectivity type, metered state & network capability flags", Icons.Default.Wifi, NeonCyan),
        ToolDefinition(4, "Thermal Info", "Android 10+ hardware thermal status & throttling state", Icons.Default.Thermostat, NeonOrange),
        ToolDefinition(5, "App Info", "Booster runtime details, JVM heap memory & target SDK level", Icons.Default.AppShortcut, NeonViolet),
        ToolDefinition(6, "Permission Info", "Security audit of declared permissions and runtime status", Icons.Default.Security, NeonCyan),
        ToolDefinition(7, "Diagnostics Scanner", "Subsystem verification with HEALTHY, WARNING or UNAVAILABLE status", Icons.Default.HealthAndSafety, NeonGreen),
        ToolDefinition(8, "Export Report", "Generate JSON hardware diagnostic report via Android Sharesheet", Icons.Default.Share, NeonViolet)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tools_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            NeonSectionHeader(title = "Hardware & Utility Tools")
        }

        items(toolsList, key = { it.id }) { tool ->
            BrutalistCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openTool(tool.id) }
                    .testTag("tool_item_${tool.id}"),
                borderColor = tool.accentColor.copy(alpha = 0.35f),
                backgroundColor = DarkCardElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(tool.accentColor.copy(alpha = 0.15f))
                            .border(1.dp, tool.accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = null,
                            tint = tool.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tool.title.uppercase(),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tool.description,
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Open tool",
                        tint = tool.accentColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Modal dialog for the selected tool
    if (uiState.selectedToolIndex != null && telemetry != null) {
        ToolDetailModal(
            toolIndex = uiState.selectedToolIndex,
            telemetry = telemetry,
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.closeTool() }
        )
    }
}

@Composable
private fun ToolDetailModal(
    toolIndex: Int,
    telemetry: com.example.repository.FullDeviceTelemetry,
    uiState: UiState,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val title = when (toolIndex) {
        0 -> "DEVICE INFORMATION"
        1 -> "BATTERY TELEMETRY"
        2 -> "STORAGE TELEMETRY"
        3 -> "NETWORK TELEMETRY"
        4 -> "THERMAL STATUS"
        5 -> "APP & RUNTIME INFO"
        6 -> "PERMISSIONS AUDIT"
        7 -> "DIAGNOSTICS SCANNER"
        8 -> "EXPORT JSON REPORT"
        else -> "TOOL DETAILS"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = NeonCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (toolIndex) {
                    0 -> { // Device Info
                        item { InfoRow("Manufacturer", telemetry.system.manufacturer) }
                        item { InfoRow("Model", telemetry.system.model) }
                        item { InfoRow("Brand", telemetry.system.brand) }
                        item { InfoRow("Device Code", telemetry.system.device) }
                        item { InfoRow("Board", telemetry.system.board) }
                        item { InfoRow("Hardware", telemetry.system.hardware) }
                        item { InfoRow("Android Version", telemetry.system.androidVersion) }
                        item { InfoRow("API Level", telemetry.system.sdkInt.toString()) }
                        item { InfoRow("Security Patch", telemetry.system.securityPatch) }
                        item { InfoRow("Supported ABIs", telemetry.system.supportedAbis.joinToString(", ")) }
                        item { InfoRow("Screen Resolution", "${telemetry.display.widthPx} x ${telemetry.display.heightPx} px") }
                        item { InfoRow("Screen Density", "${telemetry.display.densityDpi} DPI") }
                        item { InfoRow("Display Refresh Rate", "${telemetry.display.refreshRate} Hz") }
                    }
                    1 -> { // Battery Info
                        item { InfoRow("Level", "${telemetry.battery.percentage}%") }
                        item { InfoRow("Charging Status", if (telemetry.battery.isCharging) "Charging" else "Discharging") }
                        item { InfoRow("Connection", telemetry.battery.chargePlug) }
                        item { InfoRow("Health", telemetry.battery.health) }
                        item { InfoRow("Temperature", telemetry.battery.temperatureCelsius?.let { "$it°C" } ?: "N/A") }
                        item { InfoRow("Voltage", telemetry.battery.voltageMv?.let { "$it mV" } ?: "N/A") }
                        item { InfoRow("Battery Technology", telemetry.battery.technology) }
                        item { InfoRow("Power Saver Active", if (telemetry.battery.isPowerSaveMode) "YES" else "NO") }
                    }
                    2 -> { // Storage Info
                        val totalGb = String.format("%.2f GB", telemetry.storage.totalBytes.toDouble() / (1024 * 1024 * 1024))
                        val usedGb = String.format("%.2f GB", telemetry.storage.usedBytes.toDouble() / (1024 * 1024 * 1024))
                        val availGb = String.format("%.2f GB", telemetry.storage.availableBytes.toDouble() / (1024 * 1024 * 1024))
                        item { InfoRow("Total Space", totalGb) }
                        item { InfoRow("Used Space", "$usedGb (${telemetry.storage.usedPercent}%)") }
                        item { InfoRow("Available Space", availGb) }
                        item { InfoRow("Raw Total Bytes", "${telemetry.storage.totalBytes} B") }
                        item { InfoRow("Raw Available Bytes", "${telemetry.storage.availableBytes} B") }
                        item { InfoRow("Method", "StatFs(Environment.getDataDirectory().path)") }
                    }
                    3 -> { // Network Info
                        item { InfoRow("Connected", if (telemetry.network.isConnected) "YES" else "NO") }
                        item { InfoRow("Transport Type", telemetry.network.typeName) }
                        item { InfoRow("Metered Connection", if (telemetry.network.isMetered) "YES (Limited Data)" else "NO (Unmetered)") }
                        item { InfoRow("Internet Capability", if (telemetry.network.hasInternetCapability) "VALIDATED" else "NO") }
                        item { InfoRow("Downstream Bandwidth", telemetry.network.downstreamBandwidthKbps?.let { "$it Kbps" } ?: "Not reported") }
                        item { InfoRow("Upstream Bandwidth", telemetry.network.upstreamBandwidthKbps?.let { "$it Kbps" } ?: "Not reported") }
                        item {
                            Text(
                                text = "Notice: Automatic network speed tests are disabled to prevent consuming your data quota without consent.",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                    4 -> { // Thermal Info
                        item { InfoRow("Thermal State", telemetry.thermal.status.name) }
                        item { InfoRow("API Supported", if (telemetry.thermal.isSupported) "YES (API 29+)" else "NO (Fallback Active)") }
                        item { InfoRow("Raw Value", telemetry.thermal.rawStatusValue?.toString() ?: "N/A") }
                        item {
                            Text(
                                text = telemetry.thermal.description,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        item {
                            Text(
                                text = "Standard Android Policy: Thermal status is read directly from PowerManager. Android does not provide synthetic or guessed temperatures.",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                    5 -> { // App Info
                        val runtime = Runtime.getRuntime()
                        val heapUsedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                        val heapMaxMb = runtime.maxMemory() / (1024 * 1024)
                        item { InfoRow("App Name", "ZX GAME BOOSTER TOOLS") }
                        item { InfoRow("Version", "1.0.0 (Code 1)") }
                        item { InfoRow("Developer", "ZUCCHERO XANN (DEVELOPER)") }
                        item { InfoRow("Target SDK", "API 36") }
                        item { InfoRow("Min SDK", "API 24") }
                        item { InfoRow("JVM Heap Used", "$heapUsedMb MB / $heapMaxMb MB") }
                        item { InfoRow("Root / Shizuku", "NOT USED (100% Official Android APIs)") }
                    }
                    6 -> { // Permission Audit
                        item { InfoRow("ACCESS_NETWORK_STATE", "GRANTED (Normal Permission)") }
                        item { InfoRow("INTERNET", "GRANTED (Normal Permission)") }
                        item { InfoRow("QUERY_ALL_PACKAGES", "NOT REQUESTED (Uses privacy-friendly <queries> launcher filter)") }
                        item { InfoRow("ROOT / SYSTEM", "NONE REQUIRED") }
                        item { InfoRow("ACCESSIBILITY", "NONE REQUIRED") }
                        item {
                            Text(
                                text = "All features are designed strictly with minimum privilege to comply with Google Play policies.",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                    7 -> { // Diagnostics Scanner
                        if (uiState.isRunningDiagnostics) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = NeonCyan)
                                }
                            }
                        } else {
                            items(uiState.diagnostics) { item ->
                                BrutalistCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    borderColor = DarkCardBorder,
                                    backgroundColor = DarkCard
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.category.uppercase(),
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            StatusBadge(status = item.status)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = item.metric, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = item.detail, color = TextSecondary, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = item.recommendation, color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                    8 -> { // Export JSON Report
                        item {
                            Text(
                                text = "Generate full JSON report of real system telemetry to share or save via Android Sharesheet.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        if (uiState.exportJsonPreview != null) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkCard)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = uiState.exportJsonPreview.take(800) + "\n... [Full JSON will be shared]",
                                        color = NeonCyan,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                        item {
                            CyberButton(
                                text = "SHARE REPORT VIA SHARESHEET",
                                onClick = { viewModel.exportReportNow() },
                                icon = Icons.Default.Share,
                                color = NeonViolet,
                                textColor = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (toolIndex == 7) {
                TextButton(onClick = { viewModel.runDiagnostics() }) {
                    Text(text = "RE-SCAN", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            }
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkCardElevated
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(DarkCard)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
