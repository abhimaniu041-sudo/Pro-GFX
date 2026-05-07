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
        binding = ActivityGraphicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Shizuku permission result listener
        Shizuku.addRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == SHIZUKU_CODE && grantResult == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "✅ Shizuku Permission Granted!", Toast.LENGTH_SHORT).show()
                applyOptimizations() // Permission milte hi apply kar do
            }
        }

        binding.btnApplySettings.setOnClickListener {
            checkAndRun()
        }
    }

    private fun checkAndRun() {
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                applyOptimizations()
            } else {
                // 🔥 Ye line zaroori hai popup dikhane ke liye
                Shizuku.requestPermission(SHIZUKU_CODE)
            }
        } else {
            Toast.makeText(this, "📢 Shizuku Service not running! Open Shizuku app first.", Toast.LENGTH_LONG).show()
        }
    }

    private fun applyOptimizations() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isSmooth = binding.rbSmooth.isChecked
        val isRestore = binding.rbRestore.isChecked

        if (!isUltra && !isSmooth && !isRestore) {
            Toast.makeText(this, "⚠️ Please select a Graphics Mode!", Toast.LENGTH_SHORT).show()
            return
        }

        when {
            isRestore -> {
                binding.rgGraphics.clearCheck()
                binding.switchAntiLag.isChecked = false
                binding.switchUnlock144.isChecked = false
                Toast.makeText(this, "♻️ Settings Restored to Default", Toast.LENGTH_LONG).show()
            }
            else -> {
                val mode = if (isUltra) "144FPS Ultra" else "Smooth"
                Toast.makeText(this, "🚀 Applying $mode Optimization via Shizuku...", Toast.LENGTH_LONG).show()
                // Yahan actual command execution logic aayega
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Listener remove karna mat bhulna
        Shizuku.removeRequestPermissionResultListener { _, _ -> }
    }
}
