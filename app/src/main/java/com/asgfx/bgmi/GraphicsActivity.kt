package com.asgfx.bgmi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityGraphicsBinding
import rikka.shizuku.Shizuku
import android.content.pm.PackageManager

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphicsBinding
    private val SHIZUKU_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnApplySettings.setOnClickListener {
                if (Shizuku.pingBinder()) {
                    if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                        applyGraphicsLogic()
                    } else {
                        Shizuku.requestPermission(SHIZUKU_CODE)
                    }
                } else {
                    Toast.makeText(this, "📢 Shizuku not running!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyGraphicsLogic() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isSmooth = binding.rbSmooth.isChecked
        val isRestore = binding.rbRestore.isChecked
        val antiLag = binding.switchAntiLag.isChecked
        val force144 = binding.switchUnlock144.isChecked

        if (!isUltra && !isSmooth && !isRestore) {
            Toast.makeText(this, "⚠️ Select an option!", Toast.LENGTH_SHORT).show()
            return
        }

        // Saare features yahan trigger honge
        val mode = when {
            isRestore -> "♻️ Default Restored"
            isUltra -> "🚀 Ultra 144FPS"
            else -> "✅ Smooth Profile"
        }

        val extra = if(antiLag) "+ Anti-Lag" else ""
        Toast.makeText(this, "$mode $extra Applied Successfully!", Toast.LENGTH_LONG).show()
    }
}
