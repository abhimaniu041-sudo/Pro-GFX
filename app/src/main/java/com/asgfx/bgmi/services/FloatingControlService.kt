package com.asgfx.bgmi.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
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

        // 1. Start Foreground First (Android 8.0+ ke liye compulsory hai)
        runAsForeground()

        // 2. Initialize Window Manager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(this)

        try {
            floatingView = inflater.inflate(R.layout.layout_floating_control, null)

            val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            // Fixed Dimensions taaki hidden na rahe
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            params.gravity = Gravity.TOP or Gravity.START
            params.x = 100
            params.y = 200

            if (floatingView?.parent == null) {
                windowManager?.addView(floatingView, params)
            }

            setupUI()
            loadMods()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runAsForeground() {
        val channelId = "floating_panel_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "GFX Floating Service", NotificationManager.IMPORTANCE_MIN)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Pro GFX Active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        startForeground(101, notification)
    }

    private fun setupUI() {
        floatingView?.let { view ->
            val featurePage = view.findViewById<ScrollView>(R.id.featureContainer)
            
            view.findViewById<ImageView>(R.id.btnHide).setOnClickListener {
                isExpanded = !isExpanded
                featurePage.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            view.findViewById<ImageView>(R.id.btnRunBgmi).setOnClickListener {
                packageManager.getLaunchIntentForPackage("com.pubg.imobile")?.let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(it)
                }
            }

            view.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                stopSelf()
            }
        }
    }

    private fun loadMods() {
        floatingView?.let { view ->
            val container = view.findViewById<LinearLayout>(R.id.modListLayout)
            val prefs = getSharedPreferences("ModSettings", Context.MODE_PRIVATE)
            val files = File(getExternalFilesDir(null), "Configs").listFiles { f -> f.extension == "zip" }

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
        floatingView?.let { windowManager?.removeView(it) }
    }
}
