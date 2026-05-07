package com.asgfx.bgmi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.asgfx.bgmi.adapter.GameAdapter
import com.asgfx.bgmi.databinding.ActivityMainBinding
import com.asgfx.bgmi.services.FloatingControlService
import com.asgfx.bgmi.utils.DeviceUtils
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.File

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
        setupModWarehouse() // Dynamic scan function
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

    // 🔥 UPDATED: Dynamic File Scanner for MODs
    private fun setupModWarehouse() {
        val sharedPref = getSharedPreferences("ModSettings", Context.MODE_PRIVATE)
        val container = findViewById<LinearLayout>(R.id.modListContainer)
        
        // Configs folder path: Android/data/com.asgfx.bgmi/files/Configs
        val configFolder = File(getExternalFilesDir(null), "Configs")
        if (!configFolder.exists()) configFolder.mkdirs()

        // Scan for .zip files only
        val zipFiles = configFolder.listFiles { file -> file.extension == "zip" }
        
        container.removeAllViews() // Purani list clear karein

        if (zipFiles.isNullOrEmpty()) {
            val emptyMsg = TextView(this).apply {
                text = "No ZIP files found in Configs folder"
                setTextColor(getColor(R.color.white))
                setPadding(20, 20, 20, 20)
            }
            container.addView(emptyMsg)
        } else {
            // Maximum 10 files dikhayenge jaisa aapne kaha tha
            zipFiles.take(10).forEach { file ->
                val fileName = file.name
                val modView = LayoutInflater.from(this).inflate(R.layout.item_mod_list, null)
                val tvName = modView.findViewById<TextView>(R.id.tvModItemName)
                val swMod = modView.findViewById<SwitchMaterial>(R.id.swModItem)

                tvName.text = fileName // File ka asli naam (e.g. MOD1.zip)
                swMod.isChecked = sharedPref.getBoolean(fileName, false)

                swMod.setOnCheckedChangeListener { _, isChecked ->
                    sharedPref.edit().putBoolean(fileName, isChecked).apply()
                    val status = if (isChecked) "Added to Floating Panel" else "Removed"
                    Toast.makeText(this, "$fileName $status", Toast.LENGTH_SHORT).show()
                }
                container.addView(modView)
            }
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
                .setNegativeButton("Cancel", null)
                .show()
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
                startService(Intent(this, FloatingControlService::class.java))
                Toast.makeText(this, "Floating Panel Active", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
