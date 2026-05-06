package com.asgfx.bgmi.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.os.StatFs

object DeviceUtils {

    fun isBGMIInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.pubg.imobile", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
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
        } catch (e: Exception) {
            0
        }
    }
}
