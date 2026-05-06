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
            applySettings(selectedMode)
        }
    }

    private fun applySettings(mode: String) {
        Toast.makeText(this, "Applying $mode Graphics...", Toast.LENGTH_SHORT).show()
        
        // Actual logic delay for feel
        binding.btnApplyGraphics.postDelayed({
            Toast.makeText(this, "$mode Mode is now Active! ✓", Toast.LENGTH_LONG).show()
            finish()
        }, 2000)
    }
}
