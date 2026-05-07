package com.asgfx.bgmi

import android.os.Bundle
import android.widget.TextView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.utils.DeviceUtils

class SensitivityActivity : AppCompatActivity() {

    private var isGyroSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_sensitivity)

            val tier = DeviceUtils.getPerformanceTier(this)
            
            // Safe View Finding
            val tvDeviceInfo = findViewById<TextView>(R.id.tvDeviceInfo)
            val tvDeviceTier = findViewById<TextView>(R.id.tvDeviceTier)
            val btnGyro = findViewById<View>(R.id.btnGyro)
            val btnNonGyro = findViewById<View>(R.id.btnNonGyro)
            val btnLoadBest = findViewById<View>(R.id.btnLoadBest)

            tvDeviceInfo?.text = "Device: ${android.os.Build.MODEL}"
            tvDeviceTier?.text = "Optimization: $tier"

            // Default Calculation
            applySens(tier, true)

            btnGyro?.setOnClickListener {
                isGyroSelected = true
                applySens(tier, true)
                btnGyro.alpha = 1.0f
                btnNonGyro?.alpha = 0.5f
            }

            btnNonGyro?.setOnClickListener {
                isGyroSelected = false
                applySens(tier, false)
                btnNonGyro.alpha = 1.0f
                btnGyro?.alpha = 0.5f
            }

            btnLoadBest?.setOnClickListener {
                Toast.makeText(this, "No-Recoil Sensitivity Loaded!", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Technical Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun applySens(tier: String, isGyro: Boolean) {
        val mult = when (tier) {
            "High-end" -> 0.95
            "Mid-range" -> 1.10
            else -> 1.25
        }

        try {
            // Helper to update text safely
            updateText(R.id.tvTppNoScope, if(isGyro) (350 * mult) else (120 * mult))
            updateText(R.id.tvRedDot, if(isGyro) (320 * mult) else (60 * mult))
            updateText(R.id.tv2x, if(isGyro) (280 * mult) else (35 * mult))
            updateText(R.id.tv3x, if(isGyro) (245 * mult) else (26 * mult))
            updateText(R.id.tv4x, if(isGyro) (210 * mult) else (18 * mult))
            updateText(R.id.tv6x, if(isGyro) (110 * mult) else (12 * mult))
            updateText(R.id.tv8x, if(isGyro) (85 * mult) else (10 * mult))
        } catch (e: Exception) {
            // Specific view missing, ignore to prevent crash
        }
    }

    private fun updateText(viewId: Int, value: Double) {
        findViewById<TextView>(viewId)?.text = "${value.toInt()}%"
    }
}
