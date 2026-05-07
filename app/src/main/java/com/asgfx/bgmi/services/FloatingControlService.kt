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
import com.asgfx.bgmi.LoginActivity // Login par wapas bhejne ke liye agar zaroorat ho

class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private var isExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize WindowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // 2. Inflate the Floating Layout
        val inflater = LayoutInflater.from(this)
        floatingView = inflater.inflate(R.layout.layout_floating_control, null)

        // 3. Set Window Parameters (Overlay System)
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

        // 4. Add View to Window
        windowManager.addView(floatingView, params)

        // 5. Setup Business Logic
        setupControlButtons()
        loadDynamicModList()
    }

    private fun setupControlButtons() {
        val featureContainer = floatingView.findViewById<ScrollView>(R.id.featureContainer)
        val btnHide = floatingView.findViewById<ImageView>(R.id.btnHide)
        val btnRunBgmi = floatingView.findViewById<ImageView>(R.id.btnRunBgmi)
        val btnSmartRun = floatingView.findViewById<ImageView>(R.id.btnSmartRun)
        val btnClose = floatingView.findViewById<ImageView>(R.id.btnClose)

        // HIDE/SHOW Toggle
        btnHide.setOnClickListener {
            isExpanded = !isExpanded
            featureContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
            Toast.makeText(this, if (isExpanded) "Menu Opened" else "Menu Hidden", Toast.LENGTH_SHORT).show()
        }

        // BGMI QUICK RUN
        btnRunBgmi.setOnClickListener {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.pubg.imobile")
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                Toast.makeText(this, "BGMI not installed!", Toast.LENGTH_SHORT).show()
            }
        }

        // SMART RUN (Game Selector)
        btnSmartRun.setOnClickListener {
            // Yahan hum future mein mini-list popup dalenge
            Toast.makeText(this, "Smart Run: Select Game", Toast.LENGTH_SHORT).show()
        }

        // CLOSE SERVICE
        btnClose.setOnClickListener {
            stopSelf()
        }
    }

    private fun loadDynamicModList() {
        val modContainer = floatingView.findViewById<LinearLayout>(R.id.modListLayout)
        val sharedPref = getSharedPreferences("ModSettings", Context.MODE_PRIVATE)

        // Clear existing list to avoid duplication
        modContainer.removeAllViews()

        // Loop for MOD 1 to 10
        for (i in 1..10) {
            val isModEnabledInApp = sharedPref.getBoolean("ENABLE_MOD_$i", false)

            if (isModEnabledInApp) {
                // MOD Enabled hai toh floating window mein switch dikhao
                val modItem = LayoutInflater.from(this).inflate(R.layout.item_mod_switch, null)
                val tvModName = modItem.findViewById<TextView>(R.id.tvModName)
                val swMod = modItem.findViewById<SwitchCompat>(R.id.swModApply)

                tvModName.text = "MOD $i (Active ZIP)"
                
                swMod.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        applyModLogic(i)
                    } else {
                        removeModLogic(i)
                    }
                }
                modContainer.addView(modItem)
            }
        }
    }

    private fun applyModLogic(modNumber: Int) {
        // Yahan Shizuku/Shell command aayegi MOD.zip extract karne ke liye
        Toast.makeText(this, "Applying MOD $modNumber...", Toast.LENGTH_SHORT).show()
    }

    private fun removeModLogic(modNumber: Int) {
        // Yahan Backup restore karne ka logic aayega
        Toast.makeText(this, "MOD $modNumber Removed", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}
