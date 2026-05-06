package com.asgfx.bgmi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asgfx.bgmi.adapter.GameAdapter
import com.asgfx.bgmi.databinding.ActivityMainBinding
import com.asgfx.bgmi.models.GameModel
import com.asgfx.bgmi.utils.DeviceUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatus()
        setupClickListeners()
        initGameLauncher() // Naya Launcher call
    }

    private fun setupStatus() {
        val isInstalled = DeviceUtils.isBGMIInstalled(this)
        binding.tvBgmiStatus.apply {
            text = if (isInstalled) "✓ BGMI Ready" else "✗ BGMI Not Found"
            setTextColor(if (isInstalled) getColor(R.color.colorSuccess) else getColor(R.color.colorDanger))
        }
    }

    private fun initGameLauncher() {
        val pm = packageManager
        val packages = pm.getInstalledApplications(android.content.pm.PackageManager.GET_META_DATA)
        val gameList = mutableListOf<GameModel>()

        for (app in packages) {
            // Games ya Battle Royale apps detect karein
            val isGame = (app.flags and android.content.pm.ApplicationInfo.FLAG_IS_GAME != 0) || 
                         app.packageName.contains("pubg") || 
                         app.packageName.contains("freefire") ||
                         app.packageName.contains("cod")
            
            if (isGame && pm.getLaunchIntentForPackage(app.packageName) != null) {
                gameList.add(GameModel(
                    pm.getApplicationLabel(app).toString(),
                    app.packageName,
                    pm.getApplicationIcon(app)
                ))
            }
        }

        binding.rvGameList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvGameList.adapter = GameAdapter(gameList) { pkg ->
            val intent = pm.getLaunchIntentForPackage(pkg)
            if (intent != null) startActivity(intent)
        }
    }

    private fun setupClickListeners() {
        binding.btnSensitivity.setOnClickListener {
            startActivity(Intent(this, SensitivityActivity::class.java))
        }

        binding.btnGraphics.setOnClickListener {
            startActivity(Intent(this, GraphicsActivity::class.java))
        }

        binding.btnRunGame.setOnClickListener {
            val games = DeviceUtils.getAllInstalledGames(this)
            if (games.isNotEmpty()) {
                val intent = packageManager.getLaunchIntentForPackage(games[0])
                if (intent != null) startActivity(intent)
            } else {
                Toast.makeText(this, "No games found!", Toast.LENGTH_SHORT).show()
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
}
