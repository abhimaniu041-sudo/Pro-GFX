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

        // 1. Stable Foreground Start
        startServiceInForeground()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        try {
            floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_control, null)

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

            setupUI()
            loadMods()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Overlay Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun startServiceInForeground() {
        val channelId = "pro_gfx_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Pro GFX Service", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Pro GFX Menu Active")
            .setSmallIcon(R.drawable.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(101, notification)
    }

    private fun setupUI() {
        floatingView?.let { v ->
            val featurePage = v.findViewById<ScrollView>(R.id.featureContainer)
            
            v.findViewById<ImageView>(R.id.btnHide).setOnClickListener {
                isExpanded = !isExpanded
                featurePage.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            v.findViewById<ImageView>(R.id.btnRunBgmi).setOnClickListener {
                packageManager.getLaunchIntentForPackage("com.pubg.imobile")?.let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(it)
                } ?: Toast.makeText(this, "Game not found", Toast.LENGTH_SHORT).show()
            }

            v.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                stopSelf()
            }
        }
    }

    private fun loadMods() {
        floatingView?.let { v ->
            val container = v.findViewById<LinearLayout>(R.id.modListLayout)
            val prefs = getSharedPreferences("ModSettings", Context.MODE_PRIVATE)
            val files = File(getExternalFilesDir(null), "Configs").listFiles { f -> f.extension == "zip" }

            container.removeAllViews()
            files?.forEach { file ->
                if (prefs.getBoolean(file.name, false)) {
                    val modRow = LayoutInflater.from(this).inflate(R.layout.item_mod_switch, null)
                    modRow.findViewById<TextView>(R.id.tvModName).text = file.name
                    container.addView(modRow)
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
