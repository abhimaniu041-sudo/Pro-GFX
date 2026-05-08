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
    
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        startForeground(110, createNotification())

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        try {
            floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_control, null)

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = 100
                y = 300
            }

            // 🔥 MOVING LOGIC
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

            setupActions()
            windowManager.addView(floatingView, params)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupActions() {
        floatingView?.let { v ->
            val container = v.findViewById<ScrollView>(R.id.featureContainer)
            
            v.findViewById<ImageView>(R.id.btnHide).setOnClickListener {
                isExpanded = !isExpanded
                container.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            v.findViewById<ImageView>(R.id.btnRunBgmi).setOnClickListener {
                launchApp("com.pubg.imobile")
            }

            // 🔥 SMART LAUNCHER: Detects any installed game
            v.findViewById<ImageView>(R.id.btnSmartRun).setOnClickListener {
                val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                val apps = packageManager.queryIntentActivities(intent, 0)
                val gameKeywords = listOf("game", "pubg", "freefire", "cod", "mobile", "battle", "arena")
                
                val game = apps.find { app ->
                    val pkg = app.activityInfo.packageName.lowercase()
                    gameKeywords.any { pkg.contains(it) } && pkg != packageName
                }
                
                game?.let { launchApp(it.activityInfo.packageName) } 
                    ?: Toast.makeText(this, "No games detected!", Toast.LENGTH_SHORT).show()
            }

            v.findViewById<ImageView>(R.id.btnClose).setOnClickListener { stopSelf() }
        }
    }

    private fun launchApp(pkg: String) {
        packageManager.getLaunchIntentForPackage(pkg)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
        } ?: Toast.makeText(this, "Target app not found", Toast.LENGTH_SHORT).show()
    }

    private fun createNotification(): Notification {
        val channelId = "pro_gfx_v4"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "GFX Engine", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Pro GFX Menu Active")
            .setSmallIcon(R.drawable.ic_launcher).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { try { windowManager.removeView(it) } catch (e: Exception) {} }
    }
}
