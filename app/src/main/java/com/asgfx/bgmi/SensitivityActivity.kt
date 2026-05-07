package com.asgfx.bgmi

import android.os.Bundle
import android.util.Log
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

            val model = android.os.Build.MODEL
            val tier = DeviceUtils.getPerformanceTier(this)

            binding.tvDeviceInfo.text = "Device: $model"
            binding.tvDeviceTier.text = "Tier: $tier"

            // Default calculation
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
                Toast.makeText(this, "Optimized Profile Applied!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("GFX_ERROR", "Crash in SensitivityActivity: ${e.message}")
            Toast.makeText(this, "Something went wrong. Checking resources...", Toast.LENGTH_LONG).show()
            finish() // Crash hone se pehle gracefully close kar dega
        }
    }

    private fun calculateSensitivity(tier: String, gyro: Boolean) {
        val mult = when (tier) {
            "High-end" -> 0.95
            "Mid-range" -> 1.10
            else -> 1.25
        }

        try {
            if (gyro) {
                binding.tvTppNoScope.text = "${(350 * mult).toInt()}%"
                binding.tvRedDot.text = "${(320 * mult).toInt()}%"
                binding.tv2x.text = "${(280 * mult).toInt()}%"
                binding.tv3x.text = "${(245 * mult).toInt()}%"
                binding.tv4x.text = "${(210 * mult).toInt()}%"
                binding.tv6x.text = "${(115 * mult).toInt()}%"
                binding.tv8x.text = "${(90 * mult).toInt()}%"
            } else {
                binding.tvTppNoScope.text = "${(120 * mult).toInt()}%"
                binding.tvRedDot.text = "${(60 * mult).toInt()}%"
                binding.tv2x.text = "${(36 * mult).toInt()}%"
                binding.tv3x.text = "${(26 * mult).toInt()}%"
                binding.tv4x.text = "${(20 * mult).toInt()}%"
                binding.tv6x.text = "${(12 * mult).toInt()}%"
                binding.tv8x.text = "${(10 * mult).toInt()}%"
            }
        } catch (e: Exception) {
            Log.e("GFX_ERROR", "UI Binding missing: ${e.message}")
        }
    }

    private fun updateTabUI() {
        if (isGyroSelected) {
            binding.btnGyro.alpha = 1.0f
            binding.btnNonGyro.alpha = 0.6f
        } else {
            binding.btnNonGyro.alpha = 1.0f
            binding.btnGyro.alpha = 0.6f
        }
    }
}
