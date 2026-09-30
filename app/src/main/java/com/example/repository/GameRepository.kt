package com.example.repository

import com.example.data.GameDao
import com.example.model.GameEntity
import com.example.model.GameProfile
import com.example.services.InstalledAppItem
import com.example.services.InstalledAppScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(
    private val gameDao: GameDao,
    private val scanner: InstalledAppScanner
) {
    val games: Flow<List<GameEntity>> = gameDao.getAllGames()
    val favoriteGames: Flow<List<GameEntity>> = gameDao.getFavoriteGames()
    val gamesCount: Flow<Int> = gameDao.getGamesCount()

    suspend fun addGameFromInstalled(item: InstalledAppItem, profile: GameProfile = GameProfile.BALANCED) {
        withContext(Dispatchers.IO) {
            val entity = scanner.createGameEntityFromInstalled(item, profile)
            gameDao.insertGame(entity)
        }
    }

    suspend fun toggleFavorite(packageName: String, currentFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            gameDao.setFavorite(packageName, !currentFavorite)
        }
    }

    suspend fun updateProfile(packageName: String, profile: GameProfile) {
        withContext(Dispatchers.IO) {
            gameDao.updateProfile(packageName, profile.name)
        }
    }

    suspend fun removeGame(game: GameEntity) {
        withContext(Dispatchers.IO) {
            gameDao.deleteGame(game)
        }
    }

    suspend fun recordGameLaunch(packageName: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val result = scanner.launchApp(packageName)
            if (result.isSuccess) {
                gameDao.updateLastPlayed(packageName, System.currentTimeMillis())
            }
            result
        }
    }

    suspend fun scanInstalledApps(): List<InstalledAppItem> {
        return withContext(Dispatchers.IO) {
            scanner.scanLaunchableApps()
        }
    }

    suspend fun clearAllGames() {
        withContext(Dispatchers.IO) {
            gameDao.clearAllGames()
        }
    }
}
