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

        // 2 Seconds ka wait
        Handler(Looper.getMainLooper()).postDelayed({
            
            // 🔥 SESSION CHECK: Kya user pehle login kar chuka hai?
            val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                // Seedha Dashboard par jao aur naam pass karo
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_NAME", sharedPref.getString("USER_NAME", "Pro User"))
                startActivity(intent)
            } else {
                // Agar login nahi hai toh Login page dikhao
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
