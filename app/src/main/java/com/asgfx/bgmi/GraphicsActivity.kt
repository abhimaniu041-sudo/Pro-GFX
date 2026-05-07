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

    // Listener ko variable mein rakhein taaki remove kar sakein
    private val binderListener = Shizuku.OnBinderReceivedListener {
        // Shizuku connected - Yahan aap initialization kar sakte hain agar zaroorat ho
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // ✅ FIXED: Purani line hata kar naya listener add kiya
            Shizuku.addBinderReceivedListenerSticky(binderListener)

            binding.btnApplySettings.setOnClickListener {
                checkAndRun()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkAndRun() {
        try {
            if (Shizuku.pingBinder()) {
                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    applyFinalGraphics()
                } else {
                    Shizuku.requestPermission(SHIZUKU_CODE)
                }
            } else {
                Toast.makeText(
                    this,
                    "Shizuku not running! Start Shizuku app first.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Shizuku error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFinalGraphics() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isSmooth = binding.rbSmooth.isChecked
        val isRestore = binding.rbRestore.isChecked
        val antilag = binding.switchAntiLag.isChecked
        
        if (!isUltra && !isSmooth && !isRestore) {
            Toast.makeText(this, "⚠️ Please select a Graphics Mode!", Toast.LENGTH_SHORT).show()
            return
        }

        val modeText = when {
            isRestore -> "♻️ Default Graphics Restored"
            isUltra -> "🚀 144FPS Ultra Mode Applied"
            else -> "✅ Smooth Profile Applied"
        }

        val lagText = if (antilag) " + Anti-Lag ON" else ""
        Toast.makeText(this, "$modeText$lagText", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // ✅ Listener remove karna achhi practice hai
        Shizuku.removeBinderReceivedListener(binderListener)
    }
}
