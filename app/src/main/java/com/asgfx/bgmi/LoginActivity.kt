package com.asgfx.bgmi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    // ViewBinding use kar rahe hain layouts access karne ke liye
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Login Button Click Listener
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Basic Validation
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show()
            } else {
                // Filhal hum koi bhi login accept kar rahe hain
                // Baad mein yahan Firebase ya Database check add kar sakte hain
                performLogin(username)
            }
        }
    }

    private fun performLogin(user: String) {
        Toast.makeText(this, "Welcome, $user!", Toast.LENGTH_SHORT).show()
        
        // Login successful hone par MainActivity par bhej dein
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        
        // finish() zaroori hai taaki user back button daba kar wapas login page par na aa sake
        finish()
    }
}
