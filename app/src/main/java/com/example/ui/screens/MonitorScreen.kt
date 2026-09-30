package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserSettings
import com.example.model.ThermalState
import com.example.ui.components.BrutalistCard
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.NeonSectionHeader
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MonitorScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    settings: UserSettings,
    modifier: Modifier = Modifier
) {
    val telemetry = uiState.telemetry
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val lastUpdated = if (telemetry != null) timeFormat.format(Date(telemetry.timestamp)) else "N/A"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("monitor_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Ticker Status & Pause / Resume Control
        item {
            BrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (uiState.isMonitoringActive) NeonCyan.copy(alpha = 0.5f) else DarkCardBorder,
                backgroundColor = DarkCardElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isMonitoringActive) NeonGreen else NeonOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isMonitoringActive) "LIVE TELEMETRY TICKER" else "MONITOR PAUSED",
                                color = if (uiState.isMonitoringActive) NeonGreen else NeonOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "Last: $lastUpdated",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Interval Selector (1s, 3s, 5s, 10s)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interval:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        listOf(1, 3, 5, 10).forEach { sec ->
                            val isSelected = settings.monitorIntervalSeconds == sec
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) NeonCyan else DarkCard)
                                    .border(1.dp, if (isSelected) NeonCyan else DarkCardBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setMonitorInterval(sec) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${sec}s",
                                    color = if (isSelected) Color(0xFF0A0D14) else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Pause/Resume Button
                        IconButton(
                            onClick = {
                                if (uiState.isMonitoringActive) viewModel.pauseMonitoring()
                                else viewModel.resumeMonitoring()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkCard)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = if (uiState.isMonitoringActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle monitoring",
                                tint = if (uiState.isMonitoringActive) NeonOrange else NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // FPS HONEST DISCLOSURE PANEL (User requirement: Zero fake FPS)
        item {
            BrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonViolet.copy(alpha = 0.5f),
                backgroundColor = DarkCard
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = NeonViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "HONEST FPS DISCLOSURE",
                            color = NeonViolet,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Game in-app FPS monitoring is not available through standard Android APIs without root, Shizuku, or adb frame capture permissions.\n\nZX Game Booster strictly avoids displaying simulated or fake FPS counters. Instead, we report official screen refresh rates and hardware thermal throttle states.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCardElevated)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Display Hardware Max Refresh:",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${telemetry?.display?.refreshRate?.toInt() ?: 60} Hz",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // LIVE HARDWARE GAUGES
        if (telemetry != null) {
            item {
                NeonSectionHeader(title = "Real-Time Telemetry Gauges")
            }

            // RAM Gauge
            item {
                MonitorGaugeCard(
                    title = "System RAM Utilization",
                    value = "${telemetry.memory.usedPercent}%",
                    subtitle = "${String.format("%.2f", telemetry.memory.availableRamBytes.toDouble() / (1024*1024*1024))} GB Free of ${String.format("%.2f", telemetry.memory.totalRamBytes.toDouble() / (1024*1024*1024))} GB",
                    progress = telemetry.memory.usedPercent / 100f,
                    accentColor = NeonCyan
                )
            }

            // Battery Gauge
            item {
                MonitorGaugeCard(
                    title = "Battery Capacity",
                    value = "${telemetry.battery.percentage}%",
                    subtitle = "${telemetry.battery.chargePlug} • ${telemetry.battery.temperatureCelsius?.let { "$it°C" } ?: "Normal Temp"} • ${telemetry.battery.health}",
                    progress = telemetry.battery.percentage / 100f,
                    accentColor = if (telemetry.battery.percentage > 20) NeonGreen else NeonOrange
                )
            }

            // Storage Gauge
            item {
                MonitorGaugeCard(
                    title = "Internal Storage",
                    value = "${telemetry.storage.usedPercent}%",
                    subtitle = "${String.format("%.2f", telemetry.storage.availableBytes.toDouble() / (1024*1024*1024))} GB Available of ${String.format("%.2f", telemetry.storage.totalBytes.toDouble() / (1024*1024*1024))} GB",
                    progress = telemetry.storage.usedPercent / 100f,
                    accentColor = ElectricBlue
                )
            }

            // Thermal State
            item {
                BrutalistCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DarkCardBorder,
                    backgroundColor = DarkCardElevated
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "THERMAL STATUS",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            val thermalColor = when (telemetry.thermal.status) {
                                ThermalState.NORMAL, ThermalState.LIGHT, ThermalState.COOL -> NeonGreen
                                ThermalState.MODERATE -> NeonOrange
                                ThermalState.SEVERE, ThermalState.CRITICAL, ThermalState.EMERGENCY, ThermalState.SHUTDOWN -> NeonRed
                                else -> TextMuted
                            }
                            Text(
                                text = telemetry.thermal.status.name,
                                color = thermalColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = telemetry.thermal.description,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonitorGaugeCard(
    title: String,
    value: String,
    subtitle: String,
    progress: Float,
    accentColor: Color
) {
    BrutalistCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accentColor.copy(alpha = 0.4f),
        backgroundColor = DarkCardElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val animatedProgress by animateFloatAsState(
                targetValue = progress.coerceIn(0f, 1f),
                animationSpec = tween(400),
                label = "gauge_progress"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = DarkCardBorder
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
