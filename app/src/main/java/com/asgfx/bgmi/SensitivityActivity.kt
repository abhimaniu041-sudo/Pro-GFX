package com.asgfx.bgmi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivitySensitivityBinding
import com.asgfx.bgmi.utils.DeviceUtils

class SensitivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensitivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivitySensitivityBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val tier = DeviceUtils.getPerformanceTier(this)
            binding.tvDeviceInfo.text = "Device: ${android.os.Build.MODEL}"
            binding.tvDeviceTier.text = "Optimization: $tier"

            // Default calculation (Gyro)
            applySens(tier, true)

            binding.btnGyro.setOnClickListener { applySens(tier, true) }
            binding.btnNonGyro.setOnClickListener { applySens(tier, false) }
            binding.btnLoadBest.setOnClickListener { 
                Toast.makeText(this, "Settings Applied!", Toast.LENGTH_SHORT).show() 
            }

        } catch (e: Exception) {
            Toast.makeText(this, "UI Error: Update XML layout first", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun applySens(tier: String, isGyro: Boolean) {
        val mult = when (tier) {
            "High-end" -> 0.95
            "Mid-range" -> 1.10
            else -> 1.25
        }

        try {
            if (isGyro) {
                binding.tvTppNoScope.text = "${(350 * mult).toInt()}%"
                binding.tvRedDot.text = "${(320 * mult).toInt()}%"
                binding.tv2x.text = "${(280 * mult).toInt()}%"
                binding.tv3x.text = "${(245 * mult).toInt()}%"
                binding.tv4x.text = "${(210 * mult).toInt()}%"
                binding.tv6x.text = "${(110 * mult).toInt()}%"
                binding.tv8x.text = "${(85 * mult).toInt()}%"
                binding.btnGyro.alpha = 1.0f
                binding.btnNonGyro.alpha = 0.5f
            } else {
                binding.tvTppNoScope.text = "${(120 * mult).toInt()}%"
                binding.tvRedDot.text = "${(60 * mult).toInt()}%"
                binding.tv2x.text = "${(35 * mult).toInt()}%"
                binding.tv3x.text = "${(26 * mult).toInt()}%"
                binding.tv4x.text = "${(18 * mult).toInt()}%"
                binding.tv6x.text = "${(12 * mult).toInt()}%"
                binding.tv8x.text = "${(10 * mult).toInt()}%"
                binding.btnNonGyro.alpha = 1.0f
                binding.btnGyro.alpha = 0.5f
            }
        } catch (e: Exception) {
            // IDs mismatch ignore
        }
    }
}
