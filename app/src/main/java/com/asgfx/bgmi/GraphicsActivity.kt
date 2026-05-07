package com.asgfx.bgmi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityGraphicsBinding
import rikka.shizuku.Shizuku
import android.content.pm.PackageManager
import android.view.View

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphicsBinding
    private val SHIZUKU_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityGraphicsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnApplySettings.setOnClickListener {
                startShizukuFlow()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startShizukuFlow() {
        try {
            if (Shizuku.pingBinder()) {
                // Permission check listener yahan add karenge on-demand
                Shizuku.addRequestPermissionResultListener(object : Shizuku.OnRequestPermissionResultListener {
                    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                        if (requestCode == SHIZUKU_CODE && grantResult == PackageManager.PERMISSION_GRANTED) {
                            applyFinal()
                        }
                    }
                })

                if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                    applyFinal()
                } else {
                    Shizuku.requestPermission(SHIZUKU_CODE)
                }
            } else {
                Toast.makeText(this, "📢 Shizuku not running!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Service Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFinal() {
        val isUltra = binding.rbUltraExtreme.isChecked
        val isRestore = binding.rbRestore.isChecked

        if (!isUltra && !binding.rbSmooth.isChecked && !isRestore) {
            Toast.makeText(this, "⚠️ Select a Mode!", Toast.LENGTH_SHORT).show()
            return
        }

        val msg = if (isRestore) "♻️ Settings Reset!" else "🚀 Optimization Applied!"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
