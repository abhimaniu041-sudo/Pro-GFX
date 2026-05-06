package com.asgfx.bgmi.utils

import rikka.shizuku.Shizuku
import java.io.OutputStream

object ShizukuHelper {
    fun executeShell(command: String): Boolean {
        return try {
            if (Shizuku.pingBinder()) {
                // Shizuku ke through process chalane ka sahi tareeka
                val process = Shizuku.newProcess(arrayOf("sh"), null, null)
                val os: OutputStream = process.outputStream
                os.write((command + "\n").toByteArray())
                os.write("exit\n".toByteArray())
                os.flush()
                process.waitFor()
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
