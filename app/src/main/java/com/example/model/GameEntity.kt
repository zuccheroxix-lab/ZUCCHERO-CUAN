package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val packageName: String,
    val name: String,
    val isFavorite: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis(),
    val lastPlayedTimestamp: Long = 0L,
    val profile: String = GameProfile.BALANCED.name,
    val category: String = "GAME",
    val versionName: String = "1.0",
    val targetSdk: Int = 34
)
