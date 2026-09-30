package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SettingsDataStore
import com.example.data.UserSettings
import com.example.model.DiagnosticItem
import com.example.model.GameEntity
import com.example.model.GameProfile
import com.example.model.OptimizationResult
import com.example.repository.DeviceRepository
import com.example.repository.FullDeviceTelemetry
import com.example.repository.GameRepository
import com.example.repository.SettingsRepository
import com.example.services.DeviceTelemetryService
import com.example.services.InstalledAppItem
import com.example.services.InstalledAppScanner
import com.example.services.ReportExporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class NavigationTab {
    HOME,
    GAMES,
    TOOLS,
    MONITOR,
    SETTINGS
}

data class UiState(
    val currentTab: NavigationTab = NavigationTab.HOME,
    val telemetry: FullDeviceTelemetry? = null,
    val isLoadingTelemetry: Boolean = false,
    val isOptimizing: Boolean = false,
    val optimizationResult: OptimizationResult? = null,
    val showOptimizationDialog: Boolean = false,
    val searchQuery: String = "",
    val installedAppsToPick: List<InstalledAppItem> = emptyList(),
    val isScanningApps: Boolean = false,
    val showAddGameDialog: Boolean = false,
    val diagnostics: List<DiagnosticItem> = emptyList(),
    val isRunningDiagnostics: Boolean = false,
    val selectedToolIndex: Int? = null, // 0..8
    val isMonitoringActive: Boolean = true,
    val exportJsonPreview: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val scanner = InstalledAppScanner(application)
    private val telemetryService = DeviceTelemetryService(application)
    private val reportExporter = ReportExporter(application)

    val gameRepository = GameRepository(db.gameDao(), scanner)
    val deviceRepository = DeviceRepository(telemetryService, reportExporter)
    private val settingsDataStore = SettingsDataStore(application)
    val settingsRepository = SettingsRepository(settingsDataStore)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val games: StateFlow<List<GameEntity>> = gameRepository.games
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteGames: StateFlow<List<GameEntity>> = gameRepository.favoriteGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    private val _snackBarEvent = MutableSharedFlow<String>()
    val snackBarEvent: SharedFlow<String> = _snackBarEvent.asSharedFlow()

    private var monitorJob: Job? = null

    init {
        refreshTelemetry()
        startMonitoringLoop()
    }

    fun setTab(tab: NavigationTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
        if (tab == NavigationTab.TOOLS && _uiState.value.diagnostics.isEmpty()) {
            runDiagnostics()
        }
    }

    fun refreshTelemetry() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTelemetry = true)
            try {
                val t = deviceRepository.getFullTelemetry()
                _uiState.value = _uiState.value.copy(telemetry = t, isLoadingTelemetry = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingTelemetry = false)
                _snackBarEvent.emit("Failed to read telemetry: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun runQuickOptimize() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOptimizing = true)
            try {
                delay(600) // Brief animation frame
                val result = deviceRepository.performSafeOptimization()
                val refreshed = deviceRepository.getFullTelemetry()
                settingsRepository.recordOptimized()
                _uiState.value = _uiState.value.copy(
                    isOptimizing = false,
                    telemetry = refreshed,
                    optimizationResult = result,
                    showOptimizationDialog = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isOptimizing = false)
                _snackBarEvent.emit("Optimization encountered an issue: ${e.localizedMessage}")
            }
        }
    }

    fun dismissOptimizationDialog() {
        _uiState.value = _uiState.value.copy(showOptimizationDialog = false)
    }

    fun runDiagnostics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunningDiagnostics = true)
            try {
                val diag = deviceRepository.runDiagnostics()
                _uiState.value = _uiState.value.copy(diagnostics = diag, isRunningDiagnostics = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isRunningDiagnostics = false)
                _snackBarEvent.emit("Diagnostics check failed: ${e.localizedMessage}")
            }
        }
    }

    fun openTool(toolIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedToolIndex = toolIndex)
        if (toolIndex == 7 && _uiState.value.diagnostics.isEmpty()) { // 7 = Diagnostics
            runDiagnostics()
        }
        if (toolIndex == 8) { // 8 = Export Report
            prepareExportJsonPreview()
        }
    }

    fun closeTool() {
        _uiState.value = _uiState.value.copy(selectedToolIndex = null)
    }

    fun exportReportNow() {
        viewModelScope.launch {
            try {
                deviceRepository.generateAndExportReport()
                _snackBarEvent.emit("Report shared via Android Sharesheet")
            } catch (e: Exception) {
                _snackBarEvent.emit("Export failed: ${e.localizedMessage}")
            }
        }
    }

    private fun prepareExportJsonPreview() {
        viewModelScope.launch {
            try {
                val json = deviceRepository.generateReportJson()
                _uiState.value = _uiState.value.copy(exportJsonPreview = json)
            } catch (_: Exception) {}
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun openAddGameDialog() {
        _uiState.value = _uiState.value.copy(showAddGameDialog = true, isScanningApps = true)
        viewModelScope.launch {
            try {
                val apps = gameRepository.scanInstalledApps()
                _uiState.value = _uiState.value.copy(installedAppsToPick = apps, isScanningApps = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isScanningApps = false)
                _snackBarEvent.emit("Could not scan applications: ${e.localizedMessage}")
            }
        }
    }

    fun closeAddGameDialog() {
        _uiState.value = _uiState.value.copy(showAddGameDialog = false)
    }

    fun addGame(item: InstalledAppItem, profile: GameProfile = GameProfile.BALANCED) {
        viewModelScope.launch {
            try {
                gameRepository.addGameFromInstalled(item, profile)
                _snackBarEvent.emit("Added ${item.name} to Game Library")
                closeAddGameDialog()
            } catch (e: Exception) {
                _snackBarEvent.emit("Failed to add game: ${e.localizedMessage}")
            }
        }
    }

    fun toggleFavorite(game: GameEntity) {
        viewModelScope.launch {
            try {
                gameRepository.toggleFavorite(game.packageName, game.isFavorite)
            } catch (_: Exception) {}
        }
    }

    fun setGameProfile(game: GameEntity, profile: GameProfile) {
        viewModelScope.launch {
            try {
                gameRepository.updateProfile(game.packageName, profile)
                _snackBarEvent.emit("Updated profile for ${game.name} to ${profile.label}")
            } catch (_: Exception) {}
        }
    }

    fun removeGame(game: GameEntity) {
        viewModelScope.launch {
            try {
                gameRepository.removeGame(game)
                _snackBarEvent.emit("Removed ${game.name} from library")
            } catch (_: Exception) {}
        }
    }

    fun launchGame(game: GameEntity) {
        viewModelScope.launch {
            val result = gameRepository.recordGameLaunch(game.packageName)
            if (result.isFailure) {
                _snackBarEvent.emit("Unable to launch this application. Please ensure the app is installed.")
            }
        }
    }

    // Monitoring Lifecycle
    fun pauseMonitoring() {
        monitorJob?.cancel()
        monitorJob = null
        _uiState.value = _uiState.value.copy(isMonitoringActive = false)
    }

    fun resumeMonitoring() {
        _uiState.value = _uiState.value.copy(isMonitoringActive = true)
        startMonitoringLoop()
    }

    private fun startMonitoringLoop() {
        monitorJob?.cancel()
        monitorJob = viewModelScope.launch {
            while (isActive) {
                val intervalSec = userSettings.value.monitorIntervalSeconds.coerceIn(1, 60)
                delay(intervalSec * 1000L)
                if (_uiState.value.isMonitoringActive && userSettings.value.autoRefreshEnabled) {
                    try {
                        val t = deviceRepository.getFullTelemetry()
                        _uiState.value = _uiState.value.copy(telemetry = t)
                    } catch (_: Exception) {}
                }
            }
        }
    }

    // Settings actions
    fun setMonitorInterval(seconds: Int) {
        viewModelScope.launch {
            settingsRepository.setMonitorInterval(seconds)
            startMonitoringLoop()
        }
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAnimationsEnabled(enabled)
        }
    }

    fun setAutoRefresh(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoRefresh(enabled)
        }
    }

    fun resetAppData() {
        viewModelScope.launch {
            try {
                gameRepository.clearAllGames()
                settingsRepository.resetAll()
                _snackBarEvent.emit("All application data & settings reset successfully.")
                refreshTelemetry()
            } catch (e: Exception) {
                _snackBarEvent.emit("Reset failed: ${e.localizedMessage}")
            }
        }
    }
}
