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
        try {
            binding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(binding.root)

            Handler(Looper.getMainLooper()).postDelayed({
                
                val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

                if (isLoggedIn) {
                    val intent = Intent(this, MainActivity::class.java)
                    val savedName = sharedPref.getString("USER_NAME", "ABHIMANIU SHARMA")
                    intent.putExtra("USER_NAME", savedName)
                    startActivity(intent)
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                
            }, 2500)
        } catch (e: Exception) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
