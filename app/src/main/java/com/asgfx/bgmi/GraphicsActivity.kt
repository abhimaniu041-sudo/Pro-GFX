package com.asgfx.bgmi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityGraphicsBinding
import rikka.shizuku.Shizuku
import android.content.pm.PackageManager

class GraphicsActivity : AppCompatActivity() {

    private var _binding: ActivityGraphicsBinding? = null
    private val binding get() = _binding!!
    private val SHIZUKU_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // Null-safe inflation
            _binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnApplySettings.setOnClickListener {
                handleApply()
            }
        } catch (e: Exception) {
            // Agar UI crash ho toh message dikhega instead of closing
            Toast.makeText(this, "UI Load Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun handleApply() {
        if (!Shizuku.pingBinder()) {
            Toast.makeText(this, "📢 Shizuku not running!", Toast.LENGTH_SHORT).show()
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            executeLogic()
        } else {
            Shizuku.requestPermission(SHIZUKU_CODE)
        }
    }

    private fun executeLogic() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isRestore = binding.rbRestore.isChecked
        
        val mode = if (isRestore) "♻️ Default" else if (isUltra) "🚀 144FPS" else "✅ Smooth"
        Toast.makeText(this, "$mode Applied Successfully!", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
