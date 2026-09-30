package com.example.services

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.model.GameEntity
import com.example.model.GameProfile

data class InstalledAppItem(
    val packageName: String,
    val name: String,
    val versionName: String,
    val targetSdk: Int,
    val isGameCategory: Boolean
)

class InstalledAppScanner(private val context: Context) {

    fun scanLaunchableApps(): List<InstalledAppItem> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val myPackage = context.packageName

        val result = mutableListOf<InstalledAppItem>()

        for (resolveInfo in resolveInfos) {
            val pkgName = resolveInfo.activityInfo.packageName
            if (pkgName == myPackage) continue // Skip our own booster app

            try {
                val appInfo = pm.getApplicationInfo(pkgName, 0)
                val label = pm.getApplicationLabel(appInfo).toString()
                val pkgInfo = pm.getPackageInfo(pkgName, 0)
                val versionName = pkgInfo.versionName ?: "1.0"
                val targetSdk = appInfo.targetSdkVersion

                val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appInfo.category == ApplicationInfo.CATEGORY_GAME ||
                            (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
                } else {
                    (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
                }

                result.add(
                    InstalledAppItem(
                        packageName = pkgName,
                        name = label,
                        versionName = versionName,
                        targetSdk = targetSdk,
                        isGameCategory = isGame
                    )
                )
            } catch (_: Exception) {
                // Continue if package was uninstalled concurrently
            }
        }

        return result.sortedWith(
            compareByDescending<InstalledAppItem> { it.isGameCategory }
                .thenBy { it.name.lowercase() }
        )
    }

    fun launchApp(packageName: String): Result<Unit> {
        return try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Unable to launch this application. No default launcher activity found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun createGameEntityFromInstalled(item: InstalledAppItem, profile: GameProfile = GameProfile.BALANCED): GameEntity {
        return GameEntity(
            packageName = item.packageName,
            name = item.name,
            isFavorite = false,
            addedTimestamp = System.currentTimeMillis(),
            lastPlayedTimestamp = 0L,
            profile = profile.name,
            category = if (item.isGameCategory) "GAME" else "APP",
            versionName = item.versionName,
            targetSdk = item.targetSdk
        )
    }
}
