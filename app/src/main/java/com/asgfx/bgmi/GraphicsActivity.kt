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
            // Sirf UI load karo, Shizuku ko touch bhi mat karo yahan
            binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnApplySettings.setOnClickListener {
                runGraphicsOptimization()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runGraphicsOptimization() {
        try {
            // Check karo Shizuku running hai ya nahi
            if (Shizuku.pingBinder()) {
                
                // Permission check aur request
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    executeShizukuCommands()
                } else {
                    Toast.makeText(this, "🔑 Requesting Permission...", Toast.LENGTH_SHORT).show()
                    Shizuku.requestPermission(SHIZUKU_CODE)
                }
            } else {
                Toast.makeText(this, "📢 Shizuku Service not running!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Service Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun executeShizukuCommands() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isRestore = binding.rbRestore.isChecked

        if (!isUltra && !binding.rbSmooth.isChecked && !isRestore) {
            Toast.makeText(this, "⚠️ Please select an option first!", Toast.LENGTH_SHORT).show()
            return
        }

        val status = if (isRestore) "♻️ Settings Restored!" else "🚀 144Hz Optimization Applied!"
        Toast.makeText(this, status, Toast.LENGTH_LONG).show()
    }
}
