package com.asgfx.bgmi

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivitySensitivityBinding
import com.asgfx.bgmi.utils.DeviceUtils

class SensitivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensitivityBinding
    private var isGyroMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detectDevice()
        updateValues()

        binding.btnGyro.setOnClickListener {
            isGyroMode = true
            binding.btnGyro.backgroundTintList = getColorStateList(R.color.colorPrimary)
            binding.btnNonGyro.backgroundTintList = getColorStateList(R.color.colorCardBackground)
            updateValues()
        }

        binding.btnNonGyro.setOnClickListener {
            isGyroMode = false
            binding.btnNonGyro.backgroundTintList = getColorStateList(R.color.colorPrimary)
            binding.btnGyro.backgroundTintList = getColorStateList(R.color.colorCardBackground)
            updateValues()
        }

        binding.btnApplySensitivity.setOnClickListener {
            Toast.makeText(this, "Sensitivity Profile Active! ✓", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun detectDevice() {
        val model = Build.MODEL
        val ram = DeviceUtils.getTotalRAM(this)
        val tier = DeviceUtils.getPerformanceTier(this)
        
        binding.tvDeviceInfo.text = "Device: $model | RAM: $ram"
        binding.tvDeviceTier.text = "Tier: $tier"
    }

    private fun updateValues() {
        binding.containerValues.removeAllViews()
        val values = if (isGyroMode) {
            mapOf("TPP No Scope" to "330%", "Red Dot" to "345%", "2x Scope" to "315%", "3x Scope" to "300%", "4x Scope" to "276%")
        } else {
            mapOf("TPP No Scope" to "180%", "Red Dot" to "185%", "2x Scope" to "165%", "3x Scope" to "155%", "4x Scope" to "140%")
        }

        for ((label, value) in values) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                setPadding(0, 8, 0, 8)
            }

            val tvLabel = TextView(this).apply {
                text = label
                setTextColor(getColor(R.color.white))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvValue = TextView(this).apply {
                text = value
                setTextColor(getColor(R.color.colorPrimary))
                textStyle = android.graphics.Typeface.BOLD
                gravity = Gravity.END
            }

            row.addView(tvLabel)
            row.addView(tvValue)
            binding.containerValues.addView(row)
        }
    }
}
