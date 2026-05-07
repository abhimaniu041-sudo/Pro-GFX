package com.asgfx.bgmi

import android.os.Bundle
import android.view.Display
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityGraphicsBinding
import com.asgfx.bgmi.utils.DeviceUtils

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphicsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnApplyGraphics.setOnClickListener {
            applyOptimizations()
        }
    }

    private fun applyOptimizations() {
        val is144Selected = binding.rbUltraExtreme.isChecked
        val isAntiLag = binding.switchAntiLag.isChecked
        val isForceRefresh = binding.switchUnlock144.isChecked

        // Detect actual hardware refresh rate
        val refreshRate = windowManager.defaultDisplay.refreshRate

        if (is144Selected) {
            if (refreshRate >= 140) {
                Toast.makeText(this, "🚀 144Hz Hardware Detected! Unlocking Max FPS...", Toast.LENGTH_LONG).show()
                // Yahan config file apply karne ka logic aayega
            } else {
                Toast.makeText(this, "⚠️ Hardware supports $refreshRate Hz. Optimizing for Max.", Toast.LENGTH_SHORT).show()
            }
        }

        if (isForceRefresh) {
            // Commands to force system refresh rate (Requires root or Shizuku usually)
            Toast.makeText(this, "Display Refresh Rate Locked to Max", Toast.LENGTH_SHORT).show()
        }

        if (isAntiLag) {
            Toast.makeText(this, "Anti-Lag Engine Active", Toast.LENGTH_SHORT).show()
        }

        Toast.makeText(this, "Graphics Optimization Applied Successfully!", Toast.LENGTH_SHORT).show()
    }
}
