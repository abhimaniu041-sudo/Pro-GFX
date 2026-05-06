package com.asgfx.bgmi.utils

import rikka.shizuku.Shizuku
import java.io.OutputStream

object ShizukuHelper {
    fun executeShell(command: String): Boolean {
        return try {
            if (Shizuku.pingBinder()) {
                val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
                val os: OutputStream = process.outputStream
                os.write(command.toByteArray())
                os.flush()
                os.close()
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
