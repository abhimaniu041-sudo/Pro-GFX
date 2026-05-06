package com.asgfx.bgmi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fade-in animation for logo
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1500
        binding.logoContainer.startAnimation(fadeIn)

        // Navigate to MainActivity after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    // Disable back button during splash
    override fun onBackPressed() {
        // Do nothing
    }
}
