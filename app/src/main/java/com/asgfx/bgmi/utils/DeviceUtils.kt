package com.asgfx.bgmi.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Environment
import android.os.StatFs
import com.asgfx.bgmi.models.GameModel

object DeviceUtils {

    fun isBGMIInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.pubg.imobile", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getInstalledGamesInfo(context: Context): List<GameModel> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val gameList = mutableListOf<GameModel>()

        for (appInfo in packages) {
            // Android System ka game flag check karein
            val isSystemGame = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                appInfo.category == ApplicationInfo.CATEGORY_GAME
            } else {
                (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }

            // Keyword based filter (Backup ke liye)
            val isGameByPackage = appInfo.packageName.contains("pubg") || 
                                 appInfo.packageName.contains("freefire") || 
                                 appInfo.packageName.contains("cod") ||
                                 appInfo.packageName.contains("mobile") ||
                                 appInfo.packageName.contains("tencent")

            // Agar game hai aur uska icon/launcher hai
            if ((isSystemGame || isGameByPackage) && pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                // Duplicate check (apne hi app ko list mein na dikhayein)
                if (appInfo.packageName != context.packageName) {
                    gameList.add(
                        GameModel(
                            name = pm.getApplicationLabel(appInfo).toString(),
                            packageName = appInfo.packageName,
                            icon = pm.getApplicationIcon(appInfo)
                        )
                    )
                }
            }
        }
        return gameList.distinctBy { it.packageName }
    }

    fun getAllInstalledGames(context: Context): List<String> {
        return getInstalledGamesInfo(context).map { it.packageName }
    }

    fun getTotalRAM(context: Context): String {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        val totalGB = memInfo.totalMem.toDouble() / (1024 * 1024 * 1024)
        return String.format("%.1f GB", totalGB)
    }

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

    fun getStoragePercent(): Int {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val available = stat.availableBlocksLong * stat.blockSizeLong
            val total = stat.blockCountLong * stat.blockSizeLong
            if (total > 0) (100 - (available * 100 / total)).toInt() else 0
        } catch (e: Exception) { 0 }
    }
}
