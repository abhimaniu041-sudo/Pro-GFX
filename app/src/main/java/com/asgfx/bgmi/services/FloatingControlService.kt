package com.asgfx.bgmi.services

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.PixelFormat
import android.os.*
import android.view.*
import android.widget.*
import androidx.core.app.NotificationCompat
import com.asgfx.bgmi.R
import java.io.File

class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var isExpanded = false
    
    // Position Variables for Moving
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        startForegroundService()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_control, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100; y = 300
        }

        // 🔥 MASTER DRAG/MOVE LOGIC
        floatingView?.findViewById<View>(R.id.dragHandle)?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingView, params)
                    true
                }
                else -> false
            }
        }

        setupUI()
        windowManager.addView(floatingView, params)
    }

    private fun setupUI() {
        floatingView?.let { v ->
            val page = v.findViewById<ScrollView>(R.id.featureContainer)
            
            // Hide/Show Toggle
            v.findViewById<ImageView>(R.id.btnHide).setOnClickListener {
                isExpanded = !isExpanded
                page.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            // Launch BGMI
            v.findViewById<ImageView>(R.id.btnRunBgmi).setOnClickListener {
                launchTargetApp("com.pubg.imobile")
            }

            // 🔥 SMART RUN: Launch Any Game installed
            v.findViewById<ImageView>(R.id.btnSmartRun).setOnClickListener {
                val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                val allApps = packageManager.queryIntentActivities(intent, 0)
                // Filters common game keywords
                val gameApp = allApps.find { 
                    val pkg = it.activityInfo.packageName.lowercase()
                    pkg.contains("game") || pkg.contains("pubg") || pkg.contains("freefire") || pkg.contains("cod") || pkg.contains("mobile")
                }
                
                if (gameApp != null) {
                    launchTargetApp(gameApp.activityInfo.packageName)
                } else {
                    Toast.makeText(this, "No games detected automatically", Toast.LENGTH_SHORT).show()
                }
            }

            v.findViewById<ImageView>(R.id.btnClose).setOnClickListener { stopSelf() }
        }
    }

    private fun launchTargetApp(pkg: String) {
        packageManager.getLaunchIntentForPackage(pkg)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
        } ?: Toast.makeText(this, "Target app not found", Toast.LENGTH_SHORT).show()
    }

    private fun startForegroundService() {
        val channelId = "pro_gfx_gaming"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Gaming Engine", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        startForeground(105, NotificationCompat.Builder(this, channelId)
            .setContentTitle("Pro GFX Overlay Active")
            .setSmallIcon(R.drawable.ic_launcher).build())
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { windowManager.removeView(it) }
    }
}
