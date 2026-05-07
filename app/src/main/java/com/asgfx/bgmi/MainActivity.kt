package com.asgfx.bgmi

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
import com.asgfx.bgmi.utils.DeviceUtils

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
    }

    private fun setupStatus() {
        val isInstalled = DeviceUtils.isBGMIInstalled(this)
        binding.tvBgmiStatus.apply {
            if (isInstalled) {
                text = "✓ System Optimized"
                setTextColor(getColor(R.color.colorSuccess))
            } else {
                text = "✗ BGMI Not Found"
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

    private fun setupClickListeners() {
        binding.ivSettings.setOnClickListener { showThemeDialog() }

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
            } else {
                Toast.makeText(this, "No games found", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnStartOverlay.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            } else {
                startService(Intent(this, FloatingOverlayService::class.java))
                Toast.makeText(this, "Floating Panel Active", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Light Mode", "Dark Mode", "System Default")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Theme")
        builder.setItems(themes) { _, which ->
            when (which) {
                0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
        builder.show()
    }
}
