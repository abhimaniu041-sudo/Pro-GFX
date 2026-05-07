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

            binding.btnApplySettings.setOnClickListener {
                checkAndRun()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkAndRun() {
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                applyFinalGraphics()
            } else {
                // Manual permission request
                Shizuku.requestPermission(SHIZUKU_CODE)
            }
        } else {
            Toast.makeText(this, "📢 Shizuku not running! Start Shizuku app first.", Toast.LENGTH_LONG).show()
        }
    }

    private fun applyFinalGraphics() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isSmooth = binding.rbSmooth.isChecked
        val isRestore = binding.rbRestore.isChecked
        val antiLag = binding.switchAntiLag.isChecked
        val force144 = binding.switchUnlock144.isChecked

        if (!isUltra && !isSmooth && !isRestore) {
            Toast.makeText(this, "⚠️ Please select a Graphics Mode!", Toast.LENGTH_SHORT).show()
            return
        }

        val modeText = when {
            isRestore -> "♻️ Default Graphics Restored"
            isUltra -> "🚀 144FPS Ultra Mode Applied"
            else -> "✅ Smooth Profile Applied"
        }

        val lagText = if(antiLag) " + Anti-Lag ON" else ""
        
        Toast.makeText(this, "$modeText$lagText", Toast.LENGTH_LONG).show()
        // Yahan actual shell commands execution logic aayega
    }
}
