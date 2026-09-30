package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameEntity
import com.example.model.ThermalState
import com.example.ui.components.BrutalistCard
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.NeonSectionHeader
import com.example.ui.components.StatTile
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

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    favoriteGames: List<GameEntity>,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Cyber Branding & Device Banner
        item {
            BrutalistCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("banner_card"),
                borderColor = NeonCyan.copy(alpha = 0.5f),
                backgroundColor = DarkCardElevated
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ZX GAME BOOSTER",
                                color = NeonCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "DEV: ZUCCHERO XANN",
                                color = NeonViolet,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.refreshTelemetry() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkCard)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                        ) {
                            if (uiState.isLoadingTelemetry) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = NeonCyan,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh telemetry",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (telemetry != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Device Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkCard)
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${telemetry.system.manufacturer} ${telemetry.system.model}",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Android Version Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkCard)
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Android ${telemetry.system.androidVersion} (API ${telemetry.system.sdkInt})",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. QUICK OPTIMIZE BUTTON
        item {
            BrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonViolet.copy(alpha = 0.4f),
                backgroundColor = DarkCard
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SYSTEM INTEGRITY CHECK",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Official Safe Device Optimizer",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        val thermalColor = when (telemetry?.thermal?.status) {
                            ThermalState.NORMAL, ThermalState.LIGHT, ThermalState.COOL -> NeonGreen
                            ThermalState.MODERATE -> NeonOrange
                            ThermalState.SEVERE, ThermalState.CRITICAL, ThermalState.EMERGENCY, ThermalState.SHUTDOWN -> NeonRed
                            else -> TextMuted
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(thermalColor.copy(alpha = 0.15f))
                                .border(1.dp, thermalColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = telemetry?.thermal?.status?.name ?: "THERMAL OK",
                                color = thermalColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberButton(
                        text = if (uiState.isOptimizing) "OPTIMIZING TELEMETRY..." else "QUICK OPTIMIZE",
                        onClick = { viewModel.runQuickOptimize() },
                        enabled = !uiState.isOptimizing,
                        icon = if (uiState.isOptimizing) null else Icons.Default.Bolt,
                        color = NeonCyan,
                        textColor = Color(0xFF0A0D14),
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "quick_optimize_button"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cleans app internal cache, frees JVM heap, and verifies live battery & thermal boundaries.",
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // 3. HARDWARE TELEMETRY GRID
        item {
            NeonSectionHeader(title = "Live Hardware Telemetry")
        }

        if (telemetry != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Battery Tile
                    val batteryColor = if (telemetry.battery.percentage > 20) NeonGreen else NeonOrange
                    StatTile(
                        title = "Battery",
                        value = "${telemetry.battery.percentage}%",
                        subtitle = "${telemetry.battery.chargePlug} • ${telemetry.battery.temperatureCelsius?.let { "$it°C" } ?: "Normal"}",
                        icon = Icons.Default.BatteryChargingFull,
                        accentColor = batteryColor,
                        progress = telemetry.battery.percentage / 100f,
                        modifier = Modifier.weight(1f)
                    )

                    // RAM Tile
                    val ramAvailGb = String.format("%.1f", telemetry.memory.availableRamBytes.toDouble() / (1024 * 1024 * 1024))
                    val ramTotalGb = String.format("%.1f", telemetry.memory.totalRamBytes.toDouble() / (1024 * 1024 * 1024))
                    StatTile(
                        title = "System RAM",
                        value = "${ramAvailGb}G Free",
                        subtitle = "$ramTotalGb GB Total RAM",
                        icon = Icons.Default.Memory,
                        accentColor = NeonCyan,
                        progress = telemetry.memory.usedPercent / 100f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Storage Tile
                    val freeGb = String.format("%.1f", telemetry.storage.availableBytes.toDouble() / (1024 * 1024 * 1024))
                    val totalGb = String.format("%.1f", telemetry.storage.totalBytes.toDouble() / (1024 * 1024 * 1024))
                    StatTile(
                        title = "Storage",
                        value = "${freeGb}G Free",
                        subtitle = "$totalGb GB Total",
                        icon = Icons.Default.SdStorage,
                        accentColor = ElectricBlue,
                        progress = telemetry.storage.usedPercent / 100f,
                        modifier = Modifier.weight(1f)
                    )

                    // Refresh Rate Tile
                    StatTile(
                        title = "Display",
                        value = "${telemetry.display.refreshRate.toInt()} Hz",
                        subtitle = "${telemetry.display.widthPx}x${telemetry.display.heightPx} • ${telemetry.display.densityDpi} DPI",
                        icon = Icons.Default.Speed,
                        accentColor = NeonViolet,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Thermal Tile
                    StatTile(
                        title = "Thermal",
                        value = telemetry.thermal.status.name,
                        subtitle = if (telemetry.thermal.isSupported) "Hardware throttling nominal" else "Not supported on this device",
                        icon = Icons.Default.Thermostat,
                        accentColor = if (telemetry.thermal.status == ThermalState.NORMAL) NeonGreen else NeonOrange,
                        modifier = Modifier.weight(1f)
                    )

                    // Network Tile
                    StatTile(
                        title = "Network",
                        value = telemetry.network.typeName,
                        subtitle = if (telemetry.network.isConnected) "Internet active" else "Disconnected",
                        icon = Icons.Default.Wifi,
                        accentColor = if (telemetry.network.isConnected) NeonCyan else TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. FAVORITE GAMES QUICK-SHELF
        item {
            NeonSectionHeader(
                title = "Game Launcher",
                actionText = "+ Add Game",
                onActionClick = { viewModel.openAddGameDialog() }
            )
        }

        if (favoriteGames.isEmpty()) {
            item {
                BrutalistCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DarkCardBorder
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NO FAVORITE GAMES ADDED",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add installed games to quickly launch them with profile telemetry.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CyberOutlineButton(
                            text = "Browse Installed Apps",
                            onClick = { viewModel.openAddGameDialog() },
                            icon = Icons.Default.Add,
                            accentColor = NeonCyan
                        )
                    }
                }
            }
        } else {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoriteGames, key = { it.packageName }) { game ->
                        BrutalistCard(
                            modifier = Modifier
                                .width(200.dp)
                                .clickable { viewModel.launchGame(game) },
                            borderColor = NeonCyan.copy(alpha = 0.4f),
                            backgroundColor = DarkCardElevated
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonViolet.copy(alpha = 0.2f))
                                            .border(1.dp, NeonViolet, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = game.name.take(1).uppercase(),
                                            color = NeonViolet,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DarkCard)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = game.profile,
                                            color = NeonCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = game.name,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )

                                Text(
                                    text = game.packageName,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                CyberButton(
                                    text = "PLAY",
                                    onClick = { viewModel.launchGame(game) },
                                    icon = Icons.Default.PlayArrow,
                                    color = NeonCyan,
                                    textColor = Color(0xFF0A0D14),
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "play_game_${game.packageName}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Optimization Result Dialog
    if (uiState.showOptimizationDialog && uiState.optimizationResult != null) {
        val res = uiState.optimizationResult
        AlertDialog(
            onDismissRequest = { viewModel.dismissOptimizationDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OPTIMIZATION REPORT",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• Refreshed Live Telemetry: OK",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• App Memory Heap Trimmed: ${(res.memoryFreedEstimateBytes / 1024)} KB",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• App Internal Cache Cleared: ${(res.appCacheClearedBytes / 1024)} KB",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Thermal State: ${res.thermalState.name}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Battery: ${res.batteryPercentage}%",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCard)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "HONEST DISCLOSURE: Modern Android security sandbox prohibits external application killing without root. No third-party processes were forcefully terminated.",
                            color = TextMuted,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.dismissOptimizationDialog() }
                ) {
                    Text(
                        text = "CONFIRM",
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = DarkCardElevated
        )
    }
}
