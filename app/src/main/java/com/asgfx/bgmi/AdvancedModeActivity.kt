package com.asgfx.bgmi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityAdvancedBinding
import rikka.shizuku.Shizuku

class AdvancedModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdvancedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvancedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkStatus()

        binding.btnRequestPermissions.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            }
            
            try {
                if (Shizuku.pingBinder()) {
                    Shizuku.requestPermission(0)
                }
            } catch (e: Exception) {
                // Shizuku not running
            }
        }
    }

    private fun checkStatus() {
        val hasOverlay = Settings.canDrawOverlays(this)
        binding.tvOverlayStatus.text = if (hasOverlay) "Overlay: ✓ Granted" else "Overlay: ✗ Not Granted"
        binding.tvOverlayStatus.setTextColor(if (hasOverlay) getColor(R.color.colorSuccess) else getColor(R.color.colorDanger))

        try {
            val shizukuRunning = Shizuku.pingBinder()
            binding.tvShizukuStatus.text = if (shizukuRunning) "Shizuku: ✓ Running" else "Shizuku: ✗ Not Running"
            binding.tvShizukuStatus.setTextColor(if (shizukuRunning) getColor(R.color.colorSuccess) else getColor(R.color.colorDanger))
        } catch (e: Exception) {
            binding.tvShizukuStatus.text = "Shizuku: ✗ Not Found"
        }
    }

    override fun onResume() {
        super.onResume()
        checkStatus()
    }
}
