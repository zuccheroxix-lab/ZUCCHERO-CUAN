package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.SettingsDataStore
import com.example.model.GameEntity
import com.example.model.GameProfile
import com.example.model.HealthStatus
import com.example.model.ThermalState
import com.example.repository.GameRepository
import com.example.repository.SettingsRepository
import com.example.services.DeviceTelemetryService
import com.example.services.InstalledAppItem
import com.example.services.InstalledAppScanner
import com.example.services.ReportExporter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ZXGameBoosterUnitTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var telemetryService: DeviceTelemetryService
    private lateinit var reportExporter: ReportExporter
    private lateinit var scanner: InstalledAppScanner
    private lateinit var gameRepository: GameRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        telemetryService = DeviceTelemetryService(context)
        reportExporter = ReportExporter(context)
        scanner = InstalledAppScanner(context)
        gameRepository = GameRepository(db.gameDao(), scanner)
    }

    @After
    fun tearDown() {
        db.close()
    }

    // 1. Device Info Test
    @Test
    fun testDeviceInfoRetrieval() {
        val systemInfo = telemetryService.getSystemInfo()
        assertNotNull(systemInfo)
        assertNotNull(systemInfo.manufacturer)
        assertNotNull(systemInfo.model)
        assertNotNull(systemInfo.androidVersion)
        assertTrue(systemInfo.sdkInt > 0)
        assertNotNull(systemInfo.supportedAbis)
    }

    // 2. Battery Telemetry Test
    @Test
    fun testBatteryTelemetry() {
        val battery = telemetryService.getBatteryData()
        assertNotNull(battery)
        assertTrue(battery.percentage in 0..100)
        assertNotNull(battery.chargePlug)
        assertNotNull(battery.health)
        assertNotNull(battery.technology)
    }

    // 3. Storage Telemetry Test
    @Test
    fun testStorageTelemetry() {
        val storage = telemetryService.getStorageData()
        assertNotNull(storage)
        assertTrue("Storage total bytes should be non-negative", storage.totalBytes >= 0L)
        assertTrue("Storage available bytes should be non-negative", storage.availableBytes >= 0L)
        assertTrue(storage.usedPercent in 0..100)
    }

    // 4. Memory Telemetry Test
    @Test
    fun testMemoryTelemetry() {
        val memory = telemetryService.getMemoryData()
        assertNotNull(memory)
        assertTrue(memory.totalRamBytes >= 0L)
        assertTrue(memory.availableRamBytes >= 0L)
        assertTrue(memory.usedPercent in 0..100)
    }

    // 5. Diagnostics Subsystems Test
    @Test
    fun testDiagnosticsEvaluation() {
        val diagnostics = telemetryService.runDiagnostics()
        assertNotNull(diagnostics)
        assertTrue(diagnostics.isNotEmpty())

        // Verify categories exist
        val categories = diagnostics.map { it.category }
        assertTrue(categories.any { it.contains("Battery", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Storage", ignoreCase = true) })
        assertTrue(categories.any { it.contains("RAM", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Thermal", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Network", ignoreCase = true) })

        // Check each diagnostic item has valid status
        for (item in diagnostics) {
            assertTrue(
                item.status == HealthStatus.HEALTHY ||
                        item.status == HealthStatus.WARNING ||
                        item.status == HealthStatus.UNAVAILABLE
            )
            assertTrue(item.metric.isNotEmpty())
            assertTrue(item.recommendation.isNotEmpty())
        }
    }

    // 6. Safe Optimization Logic Test
    @Test
    fun testSafeOptimizationExecution() {
        val result = telemetryService.performSafeOptimization()
        assertNotNull(result)
        assertTrue(result.timestamp > 0)
        assertTrue(result.message.contains("Real Optimization Complete"))
        assertTrue(result.batteryPercentage in 0..100)
        assertNotNull(result.thermalState)
    }

    // 7. Game Repository Room Database Operations Test
    @Test
    fun testGameRepositoryOperations() = runBlocking {
        // Initial state
        val initialCount = gameRepository.gamesCount.first()
        assertEquals(0, initialCount)

        // Insert game
        val installedItem = InstalledAppItem(
            packageName = "com.supercell.brawlstars",
            name = "Brawl Stars",
            versionName = "50.1.2",
            targetSdk = 34,
            isGameCategory = true
        )
        gameRepository.addGameFromInstalled(installedItem, GameProfile.PERFORMANCE)

        // Verify inserted
        val games = gameRepository.games.first()
        assertEquals(1, games.size)
        val game = games[0]
        assertEquals("com.supercell.brawlstars", game.packageName)
        assertEquals("Brawl Stars", game.name)
        assertEquals("PERFORMANCE", game.profile)
        assertFalse(game.isFavorite)

        // Toggle Favorite
        gameRepository.toggleFavorite(game.packageName, game.isFavorite)
        val favGames = gameRepository.favoriteGames.first()
        assertEquals(1, favGames.size)
        assertTrue(favGames[0].isFavorite)

        // Update Profile
        gameRepository.updateProfile(game.packageName, GameProfile.BATTERY_SAVER)
        val updatedGame = gameRepository.games.first()[0]
        assertEquals("BATTERY_SAVER", updatedGame.profile)

        // Delete Game
        gameRepository.removeGame(updatedGame)
        val remainingGames = gameRepository.games.first()
        assertEquals(0, remainingGames.size)
    }

    // 8. Settings DataStore Test
    @Test
    fun testSettingsRepository() = runBlocking {
        val dataStore = SettingsDataStore(context)
        val settingsRepo = SettingsRepository(dataStore)

        settingsRepo.setMonitorInterval(5)
        settingsRepo.setAnimationsEnabled(false)
        settingsRepo.setAutoRefresh(false)

        val settings = settingsRepo.settings.first()
        assertEquals(5, settings.monitorIntervalSeconds)
        assertFalse(settings.animationsEnabled)
        assertFalse(settings.autoRefreshEnabled)
    }

    // 9. JSON Report Exporter Format Test
    @Test
    fun testJsonReportGeneration() {
        val system = telemetryService.getSystemInfo()
        val battery = telemetryService.getBatteryData()
        val storage = telemetryService.getStorageData()
        val memory = telemetryService.getMemoryData()
        val thermal = telemetryService.getThermalData()
        val network = telemetryService.getNetworkData()
        val display = telemetryService.getDisplayData()

        val jsonStr = reportExporter.generateJsonReport(
            system, battery, storage, memory, thermal, network, display
        )

        assertNotNull(jsonStr)
        val json = JSONObject(jsonStr)
        assertEquals("ZX GAME BOOSTER TOOLS - HARDWARE DIAGNOSTICS REPORT", json.getString("report_title"))
        assertEquals("ZUCCHERO XANN (DEVELOPER)", json.getString("developer"))
        assertTrue(json.has("system_info"))
        assertTrue(json.has("battery"))
        assertTrue(json.has("storage"))
        assertTrue(json.has("memory"))
        assertTrue(json.has("thermal"))
        assertTrue(json.has("network"))
        assertTrue(json.has("display"))
    }
}
