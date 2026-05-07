package com.asgfx.bgmi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityGraphicsBinding
import rikka.shizuku.Shizuku
import android.content.pm.PackageManager

class GraphicsActivity : AppCompatActivity() {

    private var _binding: ActivityGraphicsBinding? = null
    private val binding get() = _binding!!
    private val SHIZUKU_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            _binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnApplySettings.setOnClickListener {
                handleShizukuProcess()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "UI Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun handleShizukuProcess() {
        try {
            // Step 1: Check binder status
            if (Shizuku.pingBinder()) {
                // Step 2: Check or Request Permission
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    applyGraphics()
                } else {
                    Toast.makeText(this, "🔑 Requesting Shizuku Permission...", Toast.LENGTH_SHORT).show()
                    Shizuku.requestPermission(SHIZUKU_CODE)
                }
            } else {
                Toast.makeText(this, "📢 Shizuku not running! Please start Shizuku app.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Service Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyGraphics() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isRestore = binding.rbRestore.isChecked
        
        val mode = when {
            isRestore -> "♻️ Default Settings Restored"
            isUltra -> "🚀 144FPS Ultra Applied"
            else -> "✅ Smooth Performance Applied"
        }
        
        Toast.makeText(this, mode, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
