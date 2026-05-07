package com.asgfx.bgmi.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.asgfx.bgmi.R
import java.io.File

class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private var isExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_control, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100

        windowManager.addView(floatingView, params)
        setupControlButtons()
        loadDynamicModList()
    }

    private fun setupControlButtons() {
        val featureContainer = floatingView.findViewById<ScrollView>(R.id.featureContainer)
        
        floatingView.findViewById<ImageView>(R.id.btnHide).setOnClickListener {
            isExpanded = !isExpanded
            featureContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }

        floatingView.findViewById<ImageView>(R.id.btnRunBgmi).setOnClickListener {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.pubg.imobile")
            if (launchIntent != null) startActivity(launchIntent)
            else Toast.makeText(this, "BGMI not installed!", Toast.LENGTH_SHORT).show()
        }

        floatingView.findViewById<ImageView>(R.id.btnSmartRun).setOnClickListener {
            Toast.makeText(this, "Select Game from Smart Launcher", Toast.LENGTH_SHORT).show()
        }

        floatingView.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
            stopSelf()
        }
    }

    private fun loadDynamicModList() {
        val modContainer = floatingView.findViewById<LinearLayout>(R.id.modListLayout)
        val sharedPref = getSharedPreferences("ModSettings", Context.MODE_PRIVATE)
        
        // Scan Configs folder
        val configFolder = File(getExternalFilesDir(null), "Configs")
        val zipFiles = configFolder.listFiles { file -> file.extension == "zip" }

        modContainer.removeAllViews()

        zipFiles?.forEach { file ->
            val fileName = file.name
            // Check if user enabled this file in Main App
            if (sharedPref.getBoolean(fileName, false)) {
                val modItem = LayoutInflater.from(this).inflate(R.layout.item_mod_switch, null)
                val tvName = modItem.findViewById<TextView>(R.id.tvModName)
                val swMod = modItem.findViewById<SwitchCompat>(R.id.swModApply)

                tvName.text = fileName
                swMod.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) applyMod(fileName) else removeMod(fileName)
                }
                modContainer.addView(modItem)
            }
        }
    }

    private fun applyMod(name: String) {
        Toast.makeText(this, "Applying: $name", Toast.LENGTH_SHORT).show()
        // Shizuku logic for unzipping $name will go here
    }

    private fun removeMod(name: String) {
        Toast.makeText(this, "Removed: $name", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) windowManager.removeView(floatingView)
    }
}
