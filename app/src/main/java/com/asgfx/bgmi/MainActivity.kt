package com.asgfx.bgmi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityMainBinding
import com.asgfx.bgmi.utils.DeviceUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatus()
        setupClickListeners()
    }

    private fun setupStatus() {
        val isInstalled = DeviceUtils.isBGMIInstalled(this)
        binding.tvBgmiStatus.apply {
            text = if (isInstalled) "✓ BGMI Detected" else "✗ BGMI Not Found"
            setTextColor(if (isInstalled) getColor(R.color.colorSuccess) else getColor(R.color.colorDanger))
        }
    }

    private fun setupClickListeners() {
        binding.btnSensitivity.setOnClickListener {
            startActivity(Intent(this, SensitivityActivity::class.java))
        }

        binding.btnGraphics.setOnClickListener {
            startActivity(Intent(this, GraphicsActivity::class.java))
        }

        binding.btnRunGame.setOnClickListener {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.pubg.imobile")
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                Toast.makeText(this, "BGMI is not installed!", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnStartOverlay.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            } else {
                startService(Intent(this, FloatingOverlayService::class.java))
                Toast.makeText(this, "Floating Panel Active", Toast.LENGTH_SHORT).show()
            }
        }

        // Is line ko check karein, agar XML mein button hai tabhi ye chalega
        binding.btnRunGame.setOnLongClickListener {
            startActivity(Intent(this, CoreUtilityActivity::class.java))
            true
        }
    }
}
