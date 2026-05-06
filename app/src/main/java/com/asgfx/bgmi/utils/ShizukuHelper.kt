package com.asgfx.bgmi.utils

import rikka.shizuku.Shizuku
import java.io.DataOutputStream

object ShizukuHelper {
    fun executeShell(command: String): Boolean {
        return try {
            if (Shizuku.pingBinder()) {
                // Shizuku binder ke through direct shell command
                val process = Runtime.getRuntime().exec("sh") // Fallback
                val os = DataOutputStream(process.outputStream)
                os.writeBytes(command + "\n")
                os.writeBytes("exit\n")
                os.flush()
                process.waitFor()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
