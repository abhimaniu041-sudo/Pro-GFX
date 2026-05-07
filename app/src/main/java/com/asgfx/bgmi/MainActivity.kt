package com.asgfx.bgmi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.asgfx.bgmi.adapter.GameAdapter
import com.asgfx.bgmi.databinding.ActivityMainBinding
import com.asgfx.bgmi.services.FloatingControlService
import com.asgfx.bgmi.utils.DeviceUtils
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("USER_NAME") ?: "Pro User"
        binding.tvProfileName.text = username

        setupStatus()
        setupClickListeners()
        initGameLauncher()
        setupModWarehouse() // Initialize MOD list
    }

    private fun setupStatus() {
        val isInstalled = DeviceUtils.isBGMIInstalled(this)
        binding.tvBgmiStatus.apply {
            if (isInstalled) {
                text = "✓ System Optimized"
                setTextColor(getColor(R.color.colorSuccess))
            } else {
                text = "× BGMI Not Found"
                setTextColor(getColor(R.color.colorDanger))
            }
        }
    }

    private fun initGameLauncher() {
        val games = DeviceUtils.getInstalledGamesInfo(this)
        if (games.isNotEmpty()) {
            binding.rvGameList.visibility = View.VISIBLE
            binding.rvGameList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = GameAdapter(games) { pkgName ->
                    val launchIntent = packageManager.getLaunchIntentForPackage(pkgName)
                    if (launchIntent != null) startActivity(launchIntent)
                }
            }
        } else {
            binding.rvGameList.visibility = View.GONE
        }
    }

    // 🔥 NEW: Function to manage MOD 1-10 Switches
    private fun setupModWarehouse() {
        val sharedPref = getSharedPreferences("ModSettings", Context.MODE_PRIVATE)
        val container = findViewById<LinearLayout>(R.id.modListContainer)

        for (i in 1..10) {
            val modView = LayoutInflater.from(this).inflate(R.layout.item_mod_list, null)
            val tvName = modView.findViewById<TextView>(R.id.tvModItemName)
            val swMod = modView.findViewById<SwitchMaterial>(R.id.swModItem)

            tvName.text = "MOD $i (Zip Config)"
            swMod.isChecked = sharedPref.getBoolean("ENABLE_MOD_$i", false)

            swMod.setOnCheckedChangeListener { _, isChecked ->
                sharedPref.edit().putBoolean("ENABLE_MOD_$i", isChecked).apply()
                val status = if (isChecked) "Enabled for Floating Panel" else "Disabled"
                Toast.makeText(this, "MOD $i $status", Toast.LENGTH_SHORT).show()
            }
            container.addView(modView)
        }
    }

    private fun setupClickListeners() {
        binding.ivSettings.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Kya aap logout karna chahte hain?")
                .setPositiveButton("Logout") { _, _ ->
                    val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                    sharedPref.edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton("Cancel", null).show()
        }

        binding.btnSensitivity.setOnClickListener {
            startActivity(Intent(this, SensitivityActivity::class.java))
        }

        binding.btnGraphics.setOnClickListener {
            startActivity(Intent(this, GraphicsActivity::class.java))
        }

        binding.btnRunGame.setOnClickListener {
            val installedGames = DeviceUtils.getAllInstalledGames(this)
            if (installedGames.isNotEmpty()) {
                val target = if (installedGames.contains("com.pubg.imobile")) "com.pubg.imobile" else installedGames[0]
                val intent = packageManager.getLaunchIntentForPackage(target)
                if (intent != null) startActivity(intent)
            }
        }

        binding.btnStartOverlay.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            } else {
                // 🔥 Start the Floating Control Service
                startService(Intent(this, FloatingControlService::class.java))
                Toast.makeText(this, "Floating Panel Active", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
