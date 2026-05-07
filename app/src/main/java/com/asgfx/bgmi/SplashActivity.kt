package com.asgfx.bgmi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2.5 Seconds delay for a smoother feel
        Handler(Looper.getMainLooper()).postDelayed({
            
            // 🔥 SESSION CHECK
            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                // User already logged in, go to Dashboard
                val intent = Intent(this, MainActivity::class.java)
                // Passing saved username or default name
                val savedName = sharedPref.getString("USER_NAME", "ABHIMANIU SHARMA")
                intent.putExtra("USER_NAME", savedName)
                startActivity(intent)
            } else {
                // No session found, go to Login
                startActivity(Intent(this, LoginActivity::class.java))
            }
            
            finish()
            // Adding a smooth fade transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            
        }, 2500)
    }
}
