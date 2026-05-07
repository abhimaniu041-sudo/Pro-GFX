package com.asgfx.bgmi

import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.utils.DeviceUtils

class SensitivityActivity : AppCompatActivity() {

    private var isGyroSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensitivity) // Direct layout use kar rahe hain

        val tier = DeviceUtils.getPerformanceTier(this)
        
        // IDs ko direct find kar rahe hain (Safe Method)
        val tvDeviceInfo = findViewById<TextView>(R.id.tvDeviceInfo)
        val tvDeviceTier = findViewById<TextView>(R.id.tvDeviceTier)
        val btnGyro = findViewById<Button>(R.id.btnGyro)
        val btnNonGyro = findViewById<Button>(R.id.btnNonGyro)
        val btnLoadBest = findViewById<Button>(R.id.btnLoadBest)

        tvDeviceInfo.text = "Device: ${android.os.Build.MODEL}"
        tvDeviceTier.text = "Optimization: $tier"

        // Default Load
        applySens(tier, true)

        btnGyro.setOnClickListener {
            isGyroSelected = true
            applySens(tier, true)
            btnGyro.alpha = 1.0f
            btnNonGyro.alpha = 0.5f
        }

        btnNonGyro.setOnClickListener {
            isGyroSelected = false
            applySens(tier, false)
            btnNonGyro.alpha = 1.0f
            btnGyro.alpha = 0.5f
        }

        btnLoadBest.setOnClickListener {
            Toast.makeText(this, "Best Profile Applied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applySens(tier: String, isGyro: Boolean) {
        val mult = when (tier) {
            "High-end" -> 0.95
            "Mid-range" -> 1.10
            else -> 1.25
        }

        // Saare TextViews ko update kar rahe hain
        findViewById<TextView>(R.id.tvTppNoScope).text = "${(350 * mult).toInt()}%"
        findViewById<TextView>(R.id.tvRedDot).text = "${(320 * mult).toInt()}%"
        findViewById<TextView>(R.id.tv2x).text = "${(280 * mult).toInt()}%"
        findViewById<TextView>(R.id.tv3x).text = "${(245 * mult).toInt()}%"
        findViewById<TextView>(R.id.tv4x).text = "${(210 * mult).toInt()}%"
        findViewById<TextView>(R.id.tv6x).text = "${(110 * mult).toInt()}%"
        findViewById<TextView>(R.id.tv8x).text = "${(85 * mult).toInt()}%"

        if (!isGyro) {
            findViewById<TextView>(R.id.tvTppNoScope).text = "${(120 * mult).toInt()}%"
            findViewById<TextView>(R.id.tvRedDot).text = "${(60 * mult).toInt()}%"
            findViewById<TextView>(R.id.tv2x).text = "${(35 * mult).toInt()}%"
            findViewById<TextView>(R.id.tv3x).text = "${(26 * mult).toInt()}%"
            findViewById<TextView>(R.id.tv4x).text = "${(18 * mult).toInt()}%"
            findViewById<TextView>(R.id.tv6x).text = "${(12 * mult).toInt()}%"
            findViewById<TextView>(R.id.tv8x).text = "${(10 * mult).toInt()}%"
        }
    }
}
