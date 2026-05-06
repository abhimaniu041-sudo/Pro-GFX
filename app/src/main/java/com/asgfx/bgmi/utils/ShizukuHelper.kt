package com.asgfx.bgmi.utils

import rikka.shizuku.Shizuku

object ShizukuHelper {
    fun executeShell(command: String): Boolean {
        return try {
            if (Shizuku.pingBinder()) {
                val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
                process.waitFor()
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
