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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserSettings
import com.example.ui.components.BrutalistCard
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.NeonSectionHeader
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    settings: UserSettings,
    modifier: Modifier = Modifier
) {
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Developer & App Branding
        item {
            BrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonCyan.copy(alpha = 0.5f),
                backgroundColor = DarkCardElevated
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ZX GAME BOOSTER TOOLS",
                        color = NeonCyan,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "DEVELOPER: ZUCCHERO XANN",
                        color = NeonViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Version 1.0.0 • Architecture: Clean MVVM • Zero Root • 100% Android Official APIs",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Monitoring Configuration
        item {
            NeonSectionHeader(title = "Telemetry Preferences")
        }

        item {
            BrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = DarkCardBorder,
                backgroundColor = DarkCard
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MONITOR REFRESH INTERVAL",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Frequency for updating hardware gauges in the Monitor tab.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 3, 5, 10).forEach { sec ->
                            val isSelected = settings.monitorIntervalSeconds == sec
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) NeonCyan else DarkCardElevated)
                                    .border(1.dp, if (isSelected) NeonCyan else DarkCardBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setMonitorInterval(sec) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "${sec}s",
                                    color = if (isSelected) Color(0xFF0A0D14) else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            BrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = DarkCardBorder,
                backgroundColor = DarkCard
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Auto Refresh Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BACKGROUND TICKER",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Periodically update hardware telemetry when the app is active.",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = settings.autoRefreshEnabled,
                            onCheckedChange = { viewModel.setAutoRefresh(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF0A0D14),
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkCardElevated
                            )
                        )
                    }

                    // Animations Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "INTERFACE ANIMATIONS",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Display animated progress bars and transitions.",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = settings.animationsEnabled,
                            onCheckedChange = { viewModel.setAnimationsEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF0A0D14),
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkCardElevated
                            )
                        )
                    }
                }
            }
        }

        // Data & Privacy Actions
        item {
            NeonSectionHeader(title = "Privacy & Diagnostics Data")
        }

        item {
            BrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = DarkCardBorder,
                backgroundColor = DarkCard
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberOutlineButton(
                        text = "EXPORT DIAGNOSTIC REPORT (JSON)",
                        onClick = { viewModel.exportReportNow() },
                        icon = Icons.Default.Share,
                        accentColor = NeonCyan,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "export_report_button"
                    )

                    CyberOutlineButton(
                        text = "VIEW PRIVACY & COMPLIANCE",
                        onClick = { showPrivacyDialog = true },
                        icon = Icons.Default.PrivacyTip,
                        accentColor = NeonViolet,
                        modifier = Modifier.fillMaxWidth()
                    )

                    CyberOutlineButton(
                        text = "RESET APP DATA & LIBRARY",
                        onClick = { showResetConfirmDialog = true },
                        icon = Icons.Default.Delete,
                        accentColor = NeonRed,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "reset_app_data_button"
                    )
                }
            }
        }

        // DevOps & GitHub CI/CD Info
        item {
            BrutalistCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = DarkCardBorder,
                backgroundColor = DarkCardElevated
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEVOPS & CI/CD WORKFLOWS",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Automated with GitHub Actions:\n• .github/workflows/build-apk.yml (PR & Master builds)\n• .github/workflows/release-apk.yml (Release tags & GitHub Release uploads)",
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }

    // Reset Confirm Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RESET APPLICATION DATA?",
                        color = NeonRed,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            },
            text = {
                Text(
                    text = "This will clear your added games library and restore all telemetry settings to default values. System settings on your phone will not be affected.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAppData()
                        showResetConfirmDialog = false
                    }
                ) {
                    Text(text = "RESET ALL", color = NeonRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text(text = "CANCEL", color = TextSecondary)
                }
            },
            containerColor = DarkCardElevated
        )
    }

    // Privacy & Compliance Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = "PRIVACY & SECURITY STATEMENT",
                    color = NeonCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. ZERO DATA COLLECTION",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ZX Game Booster Tools operates 100% locally on your device. We do not upload telemetry, hardware logs, or game lists to any cloud server.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "2. OFFICIAL ANDROID APIS ONLY",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "No root access, Shizuku bridges, or system tampering are used. All metrics are gathered through standard Android BatteryManager, StatFs, ConnectivityManager, and ActivityManager APIs.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "3. GOOGLE PLAY POLICY COMPLIANCE",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "We never claim to boost hardware specs beyond device manufacturer limits. No fake RAM clearing, no fake FPS overlays.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(text = "UNDERSTOOD", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkCardElevated
        )
    }
}
