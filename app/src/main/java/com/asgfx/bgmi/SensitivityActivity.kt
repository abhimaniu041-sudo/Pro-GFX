package com.asgfx.bgmi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivitySensitivityBinding
import com.asgfx.bgmi.utils.DeviceUtils

class SensitivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensitivityBinding
    private var isGyroSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Device info detect aur display karein
        val model = android.os.Build.MODEL
        val ram = DeviceUtils.getTotalRAM(this)
        val tier = DeviceUtils.getPerformanceTier(this)

        binding.tvDeviceInfo.text = "Device: $model | RAM: $ram"
        binding.tvDeviceTier.text = "Tier: $tier"

        // Default sensitivity load karein
        calculateSensitivity(tier, true)

        // 2. Tab switching logic (Gyro vs Non-Gyro)
        binding.btnGyro.setOnClickListener {
            isGyroSelected = true
            updateTabUI()
            calculateSensitivity(tier, true)
        }

        binding.btnNonGyro.setOnClickListener {
            isGyroSelected = false
            updateTabUI()
            calculateSensitivity(tier, false)
        }

        // 3. Load Best Button logic
        binding.btnLoadBest.setOnClickListener {
            calculateSensitivity(tier, isGyroSelected)
            Toast.makeText(this, "Optimized for $model", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateSensitivity(tier: String, gyro: Boolean) {
        // No-Recoil Algorithm based on Tier
        val multiplier = when (tier) {
            "High-end" -> 0.95  // Stable control for powerful sensors
            "Mid-range" -> 1.1  // Balanced
            else -> 1.25        // Higher sensitivity for low-end touch response
        }

        if (gyro) {
            // Best Gyro No-Recoil values
            binding.tvTppNoScope.text = "${(350 * multiplier).toInt()}%"
            binding.tvRedDot.text = "${(320 * multiplier).toInt()}%"
            binding.tv2x.text = "${(280 * multiplier).toInt()}%"
            binding.tv3x.text = "${(245 * multiplier).toInt()}%"
            binding.tv4x.text = "${(210 * multiplier).toInt()}%"
        } else {
            // Best Non-Gyro (Ads) values
            binding.tvTppNoScope.text = "${(120 * multiplier).toInt()}%"
            binding.tvRedDot.text = "${(60 * multiplier).toInt()}%"
            binding.tv2x.text = "${(35 * multiplier).toInt()}%"
            binding.tv3x.text = "${(27 * multiplier).toInt()}%"
            binding.tv4x.text = "${(18 * multiplier).toInt()}%"
        }
    }

    private fun updateTabUI() {
        // Blue stealth look for active tab
        if (isGyroSelected) {
            binding.btnGyro.setBackgroundResource(R.drawable.premium_primary_bg)
            binding.btnNonGyro.setBackgroundResource(R.drawable.premium_button_bg)
        } else {
            binding.btnNonGyro.setBackgroundResource(R.drawable.premium_primary_bg)
            binding.btnGyro.setBackgroundResource(R.drawable.premium_button_bg)
        }
    }
}
