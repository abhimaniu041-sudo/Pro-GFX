package com.asgfx.bgmi.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Environment
import android.os.StatFs
import com.asgfx.bgmi.models.GameModel

object DeviceUtils {

    // 1. Check specific BGMI installation
    fun isBGMIInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.pubg.imobile", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // 2. NEW: Get full details of all installed games for the Launcher
    fun getInstalledGamesInfo(context: Context): List<GameModel> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val gameList = mutableListOf<GameModel>()

        for (appInfo in packages) {
            // Filter logic for games
            val isGame = (appInfo.flags and ApplicationInfo.FLAG_IS_GAME != 0) || 
                         appInfo.packageName.contains("pubg") || 
                         appInfo.packageName.contains("freefire") || 
                         appInfo.packageName.contains("cod") ||
                         appInfo.packageName.contains("mobile")

            if (isGame && pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                gameList.add(
                    GameModel(
                        name = pm.getApplicationLabel(appInfo).toString(),
                        packageName = appInfo.packageName,
                        icon = pm.getApplicationIcon(appInfo)
                    )
                )
            }
        }
        return gameList
    }

    // 3. Simple list of package names (for Smart Launch button)
    fun getAllInstalledGames(context: Context): List<String> {
        return getInstalledGamesInfo(context).map { it.packageName }
    }

    // 4. RAM Information
    fun getTotalRAM(context: Context): String {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        val totalGB = memInfo.totalMem.toDouble() / (1024 * 1024 * 1024)
        return String.format("%.1f GB", totalGB)
    }

    // 5. Performance Tier
    fun getPerformanceTier(context: Context): String {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        val ramGB = memInfo.totalMem / (1024 * 1024 * 1024)
        return when {
            ramGB >= 6 -> "High-end"
            ramGB >= 4 -> "Mid-range"
            else -> "Low-end"
        }
    }

    // 6. Storage Status
    fun getStoragePercent(): Int {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val available = stat.availableBlocksLong * stat.blockSizeLong
            val total = stat.blockCountLong * stat.blockSizeLong
            if (total > 0) (100 - (available * 100 / total)).toInt() else 0
        } catch (e: Exception) { 0 }
    }
}
