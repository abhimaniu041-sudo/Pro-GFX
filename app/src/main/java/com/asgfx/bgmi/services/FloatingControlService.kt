package com.asgfx.bgmi.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import com.asgfx.bgmi.R
import java.io.File

class FloatingControlService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var isExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // 1. Foreground Notification (Oreo+ support)
        val channelId = "floating_panel_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "GFX Service", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Pro GFX Active")
            .setContentText("Floating control is running")
            .setSmallIcon(R.drawable.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(101, notification)

        // 2. Window Manager Initialization
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(this)

        try {
            floatingView = inflater.inflate(R.layout.layout_floating_control, null)

            val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )

            params.gravity = Gravity.TOP or Gravity.START
            params.x = 100
            params.y = 300

            if (floatingView?.parent == null) {
                windowManager?.addView(floatingView, params)
            }

            setupUIControls()
            loadUserMods()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Floating Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUIControls() {
        floatingView?.let { view ->
            val featurePage = view.findViewById<ScrollView>(R.id.featureContainer)
            
            view.findViewById<ImageView>(R.id.btnHide).setOnClickListener {
                isExpanded = !isExpanded
                featurePage.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            view.findViewById<ImageView>(R.id.btnRunBgmi).setOnClickListener {
                val launchIntent = packageManager.getLaunchIntentForPackage("com.pubg.imobile")
                launchIntent?.let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(it)
                } ?: Toast.makeText(this, "Game not found", Toast.LENGTH_SHORT).show()
            }

            view.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                stopSelf()
            }
        }
    }

    private fun loadUserMods() {
        floatingView?.let { view ->
            val container = view.findViewById<LinearLayout>(R.id.modListLayout)
            val prefs = getSharedPreferences("ModSettings", Context.MODE_PRIVATE)
            val configFolder = File(getExternalFilesDir(null), "Configs")
            val files = configFolder.listFiles { f -> f.extension == "zip" }

            container.removeAllViews()
            files?.forEach { file ->
                if (prefs.getBoolean(file.name, false)) {
                    val modView = LayoutInflater.from(this).inflate(R.layout.item_mod_switch, null)
                    modView.findViewById<TextView>(R.id.tvModName).text = file.name
                    container.addView(modView)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { 
            try { windowManager?.removeView(it) } catch (e: Exception) { }
        }
    }
}
