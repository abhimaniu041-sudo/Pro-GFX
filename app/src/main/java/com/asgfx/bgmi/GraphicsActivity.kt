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

    // Listener jo connection aate hi binder ko "Catch" kar lega
    private val binderListener = Shizuku.OnBinderReceivedListener {
        checkShizukuStatus(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // ✅ Connection detect karne ke liye sticky listener
            Shizuku.addBinderReceivedListenerSticky(binderListener)

            binding.btnApplySettings.setOnClickListener {
                checkShizukuStatus(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkShizukuStatus(isManualClick: Boolean) {
        try {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    if (isManualClick) applyFinalGraphics()
                } else {
                    // Agar authorized hai par session expired hai
                    Shizuku.requestPermission(SHIZUKU_CODE)
                }
            } else {
                if (isManualClick) {
                    Toast.makeText(this, "📢 Shizuku Binder not connected! Restart Shizuku app.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            if (isManualClick) Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFinalGraphics() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isSmooth = binding.rbSmooth.isChecked
        val isRestore = binding.rbRestore.isChecked

        if (!isUltra && !isSmooth && !isRestore) {
            Toast.makeText(this, "⚠️ Select Graphics Mode First!", Toast.LENGTH_SHORT).show()
            return
        }

        val modeText = when {
            isRestore -> "♻️ Original Graphics Restored"
            isUltra -> "🚀 144FPS Mode Applied Successfully!"
            else -> "✅ Smooth Profile Applied!"
        }

        Toast.makeText(this, modeText, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderListener)
    }
}
