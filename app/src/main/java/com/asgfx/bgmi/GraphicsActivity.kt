package com.asgfx.bgmi

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityGraphicsBinding
import rikka.shizuku.Shizuku
import android.content.pm.PackageManager

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphicsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Title and Back button setup (if needed)
        binding.btnApplySettings.setOnClickListener {
            checkAndApply()
        }
    }

    private fun checkAndApply() {
        // 1. Pehle check karein ki user ne kuch select kiya hai ya nahi
        val isUltra = binding.rbUltraExtreme.isChecked
        val isSmooth = binding.rbSmooth.isChecked
        val isRestore = binding.rbRestore.isChecked

        if (!isUltra && !isSmooth && !isRestore) {
            Toast.makeText(this, "⚠️ Please select a Graphics Mode first!", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Shizuku Permission Check (Android 11+ optimization ke liye zaroori)
        if (!isShizukuAvailable()) {
            Toast.makeText(this, "📢 Shizuku Service not found! Please install and start Shizuku.", Toast.LENGTH_LONG).show()
            // Note: Hum bina Shizuku ke bhi file method se try kar sakte hain, par performance optimized nahi hogi
        }

        // 3. Logic Execution
        when {
            isRestore -> restoreDefaultSettings()
            isUltra -> applyGraphicsConfig("ULTRA_144FPS")
            isSmooth -> applyGraphicsConfig("SMOOTH_PERFORMANCE")
        }
    }

    private fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder() // Check if service is alive
        } catch (e: Exception) {
            false
        }
    }

    private fun applyGraphicsConfig(mode: String) {
        val antiLag = binding.switchAntiLag.isChecked
        val force144 = binding.switchUnlock144.isChecked

        // Display Hardware Check
        val currentRefreshRate = windowManager.defaultDisplay.refreshRate
        
        var statusMessage = "Applying $mode..."
        if (force144 && currentRefreshRate < 140) {
            statusMessage += "\n(Forcing 144Hz via System)"
        }

        // Yahan aapka actual Shizuku command execution logic aayega
        // Example: Shizuku.newBinder()...
        
        Toast.makeText(this, "🚀 $statusMessage\nAnti-Lag: ${if(antiLag) "ON" else "OFF"}", Toast.LENGTH_LONG).show()
    }

    private fun restoreDefaultSettings() {
        binding.rgGraphics.clearCheck()
        binding.switchAntiLag.isChecked = false
        binding.switchUnlock144.isChecked = false
        
        Toast.makeText(this, "♻️ All Graphics Settings Restored to Default", Toast.LENGTH_LONG).show()
    }

    // Shizuku permission request handle karne ke liye (Optional but recommended)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Shizuku Permission Granted!", Toast.LENGTH_SHORT).show()
        }
    }
}
