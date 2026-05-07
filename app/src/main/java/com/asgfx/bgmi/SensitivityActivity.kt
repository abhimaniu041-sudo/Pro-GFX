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

        val model = android.os.Build.MODEL
        val ram = DeviceUtils.getTotalRAM(this)
        val tier = DeviceUtils.getPerformanceTier(this)

        binding.tvDeviceInfo.text = "Device: $model | RAM: $ram"
        binding.tvDeviceTier.text = "Tier: $tier"

        // Initial Calculation (Default Gyro)
        calculateSensitivity(tier, true)

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

        binding.btnLoadBest.setOnClickListener {
            calculateSensitivity(tier, isGyroSelected)
            Toast.makeText(this, "No-Recoil Profile Applied for $model", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateSensitivity(tier: String, gyro: Boolean) {
        // Multiplier based on device tier to ensure stability
        val multiplier = when (tier) {
            "High-end" -> 0.95
            "Mid-range" -> 1.10
            else -> 1.25
        }

        if (gyro) {
            // High speed Gyro settings
            binding.tvTppNoScope.text = "${(350 * multiplier).toInt()}%"
            binding.tvRedDot.text = "${(320 * multiplier).toInt()}%"
            binding.tv2x.text = "${(280 * multiplier).toInt()}%"
            binding.tv3x.text = "${(245 * multiplier).toInt()}%"
            binding.tv4x.text = "${(210 * multiplier).toInt()}%"
            binding.tv6x.text = "${(110 * multiplier).toInt()}%"
            binding.tv8x.text = "${(85 * multiplier).toInt()}%"
        } else {
            // Precision ADS (Non-Gyro) settings
            binding.tvTppNoScope.text = "${(125 * multiplier).toInt()}%"
            binding.tvRedDot.text = "${(65 * multiplier).toInt()}%"
            binding.tv2x.text = "${(38 * multiplier).toInt()}%"
            binding.tv3x.text = "${(28 * multiplier).toInt()}%"
            binding.tv4x.text = "${(22 * multiplier).toInt()}%"
            binding.tv6x.text = "${(14 * multiplier).toInt()}%"
            binding.tv8x.text = "${(10 * multiplier).toInt()}%"
        }
    }

    private fun updateTabUI() {
        if (isGyroSelected) {
            binding.btnGyro.setBackgroundResource(R.drawable.premium_primary_bg)
            binding.btnNonGyro.setBackgroundResource(R.drawable.premium_button_bg)
        } else {
            binding.btnNonGyro.setBackgroundResource(R.drawable.premium_primary_bg)
            binding.btnGyro.setBackgroundResource(R.drawable.premium_button_bg)
        }
    }
}
