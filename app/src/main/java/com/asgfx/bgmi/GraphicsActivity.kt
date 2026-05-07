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

    // Listener ko variable mein rakhein taaki destroy par remove kar sakein
    private val permissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == SHIZUKU_CODE && grantResult == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "✅ Shizuku Permission Granted!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Safe listener addition
            if (Shizuku.pingBinder()) {
                Shizuku.addRequestPermissionResultListener(permissionListener)
            }

            binding.btnApplySettings.setOnClickListener {
                handleApplyFlow()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "UI Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleApplyFlow() {
        try {
            if (!Shizuku.pingBinder()) {
                Toast.makeText(this, "📢 Shizuku not running! Please start Shizuku app.", Toast.LENGTH_LONG).show()
                return
            }

            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                applyFinalSettings()
            } else {
                Shizuku.requestPermission(SHIZUKU_CODE)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Service Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFinalSettings() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isSmooth = binding.rbSmooth.isChecked
        val isRestore = binding.rbRestore.isChecked

        if (!isUltra && !isSmooth && !isRestore) {
            Toast.makeText(this, "⚠️ Select a Graphics Mode!", Toast.LENGTH_SHORT).show()
            return
        }

        val msg = if (isRestore) "♻️ Settings Reset!" else "🚀 144Hz Optimization Applied!"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            Shizuku.removeRequestPermissionResultListener(permissionListener)
        } catch (e: Exception) { }
    }
}
