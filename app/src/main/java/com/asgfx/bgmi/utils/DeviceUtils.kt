package com.asgfx.bgmi.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.StatFs
import android.view.WindowManager
import java.io.File

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
        val totalMemory = memInfo.totalMem / (1024 * 1024 * 1024.0)
        return String.format("%.1f GB", totalMemory)
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
        val stat = StatFs(System.getProperty("user.dir"))
        val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
        val bytesTotal = stat.blockSizeLong * stat.blockCountLong
        return if (bytesTotal > 0) (100 - (bytesAvailable * 100 / bytesTotal)).toInt() else 0
    }
}
