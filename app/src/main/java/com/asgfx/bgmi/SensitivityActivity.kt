package com.asgfx.bgmi

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivitySensitivityBinding
import com.asgfx.bgmi.utils.DeviceUtils

class SensitivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensitivityBinding
    private var isGyroSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivitySensitivityBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val tier = DeviceUtils.getPerformanceTier(this)
            binding.tvDeviceInfo.text = "Device: ${android.os.Build.MODEL}"
            binding.tvDeviceTier.text = "Optimization: $tier"

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
                Toast.makeText(this, "Sensitivity Applied!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Layout ID Mismatch! Please check XML.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun calculateSensitivity(tier: String, gyro: Boolean) {
        val mult = when (tier) {
            "High-end" -> 0.95
            "Mid-range" -> 1.10
            else -> 1.25
        }

        // Checking each view safely to prevent crash
        try {
            if (gyro) {
                binding.tvTppNoScope.text = "${(350 * mult).toInt()}%"
                binding.tvRedDot.text = "${(320 * mult).toInt()}%"
                binding.tv2x.text = "${(280 * mult).toInt()}%"
                binding.tv3x.text = "${(245 * mult).toInt()}%"
                binding.tv4x.text = "${(210 * mult).toInt()}%"
                binding.tv6x.text = "${(110 * mult).toInt()}%"
                binding.tv8x.text = "${(85 * mult).toInt()}%"
            } else {
                binding.tvTppNoScope.text = "${(120 * mult).toInt()}%"
                binding.tvRedDot.text = "${(60 * mult).toInt()}%"
                binding.tv2x.text = "${(35 * mult).toInt()}%"
                binding.tv3x.text = "${(26 * mult).toInt()}%"
                binding.tv4x.text = "${(18 * mult).toInt()}%"
                binding.tv6x.text = "${(12 * mult).toInt()}%"
                binding.tv8x.text = "${(10 * mult).toInt()}%"
            }
        } catch (e: Exception) { }
    }

    private fun updateTabUI() {
        binding.btnGyro.alpha = if (isGyroSelected) 1.0f else 0.5f
        binding.btnNonGyro.alpha = if (isGyroSelected) 0.5f else 1.0f
    }
}
