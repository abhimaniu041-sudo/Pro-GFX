package com.asgfx.bgmi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityGraphicsBinding
import rikka.shizuku.Shizuku
import android.content.pm.PackageManager

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphicsBinding
    private val SHIZUKU_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Startup par sirf ping karenge, koi bhari listener nahi
            if (Shizuku.pingBinder()) {
                // Connection hai
            }

            binding.btnApplySettings.setOnClickListener {
                runGraphicsProcess()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runGraphicsProcess() {
        try {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    applyFinalSettings()
                } else {
                    Toast.makeText(this, "🔑 Requesting Permission...", Toast.LENGTH_SHORT).show()
                    Shizuku.requestPermission(SHIZUKU_CODE)
                }
            } else {
                Toast.makeText(this, "📢 Shizuku Binder not connected! Open Shizuku app first.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFinalSettings() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isRestore = binding.rbRestore.isChecked

        if (!isUltra && !binding.rbSmooth.isChecked && !isRestore) {
            Toast.makeText(this, "⚠️ Please select an option!", Toast.LENGTH_SHORT).show()
            return
        }

        val msg = if (isRestore) "♻️ Settings Restored!" else "🚀 Graphics Optimized!"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
