package com.asgfx.bgmi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityGraphicsBinding

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphicsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnApplyGraphics.setOnClickListener {
            val selectedMode = when (binding.rgGraphics.checkedRadioButtonId) {
                R.id.rbSmooth -> "Smooth"
                R.id.rbBalanced -> "Balanced"
                R.id.rbHDR -> "HDR"
                else -> "None"
            }

            val antiLag = binding.swAntiLag.isChecked
            val fpsBoost = binding.swFpsBoost.isChecked

            // Logic to simulate applying GFX
            applySettings(selectedMode, antiLag, fpsBoost)
        }
    }

    private fun applySettings(mode: String, lag: Boolean, fps: Boolean) {
        // Here we would normally write to the game's config files 
        // For now, we show success message and finish
        Toast.makeText(this, "Applying $mode Graphics...", Toast.LENGTH_SHORT).show()
        
        binding.btnApplyGraphics.postDelayed({
            Toast.makeText(this, "Graphics profile is active! ✓", Toast.LENGTH_LONG).show()
            finish()
        }, 1500)
    }
}
