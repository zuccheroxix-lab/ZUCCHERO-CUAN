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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.GameEntity
import com.example.model.GameProfile
import com.example.services.InstalledAppItem
import com.example.ui.components.BrutalistCard
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberOutlineButton
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardElevated
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
fun GamesScreen(
    viewModel: MainViewModel,
    uiState: UiState,
    gamesList: List<GameEntity>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGameForDetails by remember { mutableStateOf<GameEntity?>(null) }

    val filteredGames = gamesList.filter {
        searchQuery.isEmpty() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("games_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Add Game Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GAME MANAGER",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "${gamesList.size} Applications In Library",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                CyberOutlineButton(
                    text = "+ Add Game",
                    onClick = { viewModel.openAddGameDialog() },
                    icon = Icons.Default.Add,
                    accentColor = NeonCyan,
                    testTag = "add_game_button"
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("game_search_input"),
                placeholder = {
                    Text(
                        text = "Search game or package...",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NeonCyan
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = TextMuted
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkCardElevated,
                    unfocusedContainerColor = DarkCardElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Empty State
        if (filteredGames.isEmpty()) {
            item {
                BrutalistCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DarkCardBorder
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (searchQuery.isEmpty()) "NO GAMES IN LIBRARY" else "NO RESULTS FOUND",
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isEmpty())
                                "Scan your device to add installed games with custom telemetry profiles."
                            else
                                "No games match \"$searchQuery\". Try another keyword.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        CyberButton(
                            text = "SCAN INSTALLED APPS",
                            onClick = { viewModel.openAddGameDialog() },
                            icon = Icons.Default.Add,
                            color = NeonCyan,
                            textColor = Color(0xFF0A0D14)
                        )
                    }
                }
            }
        } else {
            items(filteredGames, key = { it.packageName }) { game ->
                GameCardItem(
                    game = game,
                    onLaunch = { viewModel.launchGame(game) },
                    onToggleFavorite = { viewModel.toggleFavorite(game) },
                    onProfileChange = { profile -> viewModel.setGameProfile(game, profile) },
                    onDetails = { selectedGameForDetails = game },
                    onDelete = { viewModel.removeGame(game) }
                )
            }
        }
    }

    // Add Game Dialog / Sheet
    if (uiState.showAddGameDialog) {
        AddGameModalDialog(
            viewModel = viewModel,
            installedApps = uiState.installedAppsToPick,
            isLoading = uiState.isScanningApps,
            existingPackages = gamesList.map { it.packageName }.toSet(),
            onDismiss = { viewModel.closeAddGameDialog() }
        )
    }

    // Game Details Dialog
    if (selectedGameForDetails != null) {
        val g = selectedGameForDetails!!
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val addedDate = dateFormat.format(Date(g.addedTimestamp))
        val lastPlayed = if (g.lastPlayedTimestamp > 0) dateFormat.format(Date(g.lastPlayedTimestamp)) else "Never"

        AlertDialog(
            onDismissRequest = { selectedGameForDetails = null },
            title = {
                Text(
                    text = g.name.uppercase(),
                    color = NeonCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Package: ${g.packageName}", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "Category: ${g.category}", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "Version: ${g.versionName}", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "Target SDK: ${g.targetSdk}", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "Added On: $addedDate", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "Last Launched: $lastPlayed", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "Active Profile: ${g.profile}", color = NeonViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkCard)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Standard Android API Notice: Game booster profile sets telemetry frequency and display settings. Standard Android APIs do not support tampering with external game private memory or CPU frequencies.",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedGameForDetails = null }) {
                    Text(text = "CLOSE", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkCardElevated
        )
    }
}

@Composable
private fun GameCardItem(
    game: GameEntity,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onProfileChange: (GameProfile) -> Unit,
    onDetails: () -> Unit,
    onDelete: () -> Unit
) {
    var profileMenuExpanded by remember { mutableStateOf(false) }

    BrutalistCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (game.isFavorite) NeonCyan.copy(alpha = 0.5f) else DarkCardBorder,
        backgroundColor = DarkCardElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game Initials Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkCard)
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = game.name.take(2).uppercase(),
                        color = NeonCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Package
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = game.packageName,
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                // Favorite Toggle
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (game.isFavorite) NeonRed else TextMuted
                    )
                }

                // Details Button
                IconButton(onClick = onDetails) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Game info",
                        tint = TextSecondary
                    )
                }

                // Delete Button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove game",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Profile & Launch Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Selector Dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCard)
                            .border(1.dp, NeonViolet.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable { profileMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = NeonViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = game.profile,
                            color = NeonViolet,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(
                        expanded = profileMenuExpanded,
                        onDismissRequest = { profileMenuExpanded = false }
                    ) {
                        GameProfile.entries.forEach { profile ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = profile.label, fontWeight = FontWeight.Bold)
                                        Text(text = profile.description, fontSize = 10.sp, color = TextMuted)
                                    }
                                },
                                onClick = {
                                    profileMenuExpanded = false
                                    onProfileChange(profile)
                                }
                            )
                        }
                    }
                }

                // Launch PLAY Button
                CyberButton(
                    text = "PLAY",
                    onClick = onLaunch,
                    icon = Icons.Default.PlayArrow,
                    color = NeonCyan,
                    textColor = Color(0xFF0A0D14),
                    modifier = Modifier.height(38.dp),
                    testTag = "play_${game.packageName}"
                )
            }
        }
    }
}

@Composable
private fun AddGameModalDialog(
    viewModel: MainViewModel,
    installedApps: List<InstalledAppItem>,
    isLoading: Boolean,
    existingPackages: Set<String>,
    onDismiss: () -> Unit
) {
    var searchInstalled by remember { mutableStateOf("") }

    val filteredApps = installedApps.filter {
        searchInstalled.isEmpty() ||
                it.name.contains(searchInstalled, ignoreCase = true) ||
                it.packageName.contains(searchInstalled, ignoreCase = true)
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
                    text = "ADD INSTALLED APP",
                    color = NeonCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                OutlinedTextField(
                    value = searchInstalled,
                    onValueChange = { searchInstalled = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Filter apps...", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonCyan)
                    }
                } else if (filteredApps.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No apps found.", color = TextMuted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            val isAdded = existingPackages.contains(app.packageName)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkCard)
                                    .clickable(enabled = !isAdded) {
                                        viewModel.addGame(app)
                                    }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = app.name,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (app.isGameCategory) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(NeonGreen.copy(alpha = 0.2f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "GAME",
                                                    color = NeonGreen,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = app.packageName,
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }

                                if (isAdded) {
                                    Text(
                                        text = "ADDED",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "+ ADD",
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
        containerColor = DarkCardElevated
    )
}
