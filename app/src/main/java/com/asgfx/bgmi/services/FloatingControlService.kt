package com.asgfx.bgmi.services

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.ApplicationInfo
import android.graphics.PixelFormat
import android.os.*
import android.view.*
import android.widget.*
import androidx.core.app.NotificationCompat
import com.asgfx.bgmi.R

class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private lateinit var params: WindowManager.LayoutParams
    
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isMoving = false

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        startForeground(110, createNotification())

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_control, null)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100; y = 300
        }

        // 🔥 PERFECT DRAG LOGIC (Works on whole view)
        floatingView?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isMoving = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffX = (event.rawX - initialTouchX).toInt()
                    val diffY = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(diffX) > 10 || Math.abs(diffY) > 10) {
                        params.x = initialX + diffX
                        params.y = initialY + diffY
                        windowManager.updateViewLayout(floatingView, params)
                        isMoving = true
                    }
                    true
                }
                MotionEvent.ACTION_UP -> !isMoving // Allow clicks if not moved
                else -> false
            }
        }

        setupButtons()
        windowManager.addView(floatingView, params)
    }

    private fun setupButtons() {
        floatingView?.let { v ->
            val expandedMenu = v.findViewById<View>(R.id.expandedMenu)
            val collapsedIcon = v.findViewById<View>(R.id.collapsedIcon)
            val dynamicLayout = v.findViewById<LinearLayout>(R.id.dynamicContentLayout)

            v.findViewById<ImageView>(R.id.btnHide).setOnClickListener {
                expandedMenu.visibility = View.GONE
                collapsedIcon.visibility = View.VISIBLE
            }

            collapsedIcon.setOnClickListener {
                collapsedIcon.visibility = View.GONE
                expandedMenu.visibility = View.VISIBLE
            }

            // Launch BGMI Default
            v.findViewById<ImageView>(R.id.btnRunBgmi).setOnClickListener {
                launchApp("com.pubg.imobile")
            }

            // 🔥 SMART GAME LAUNCHER (Show List)
            v.findViewById<ImageView>(R.id.btnSmartRun).setOnClickListener {
                showGameList(dynamicLayout)
            }

            v.findViewById<ImageView>(R.id.btnClose).setOnClickListener { stopSelf() }
        }
    }

    private fun showGameList(container: LinearLayout) {
        container.removeAllViews()
        val title = TextView(this).apply {
            text = "SELECT GAME TO LAUNCH"
            setTextColor(0xFFFF1744.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 20)
            textSize = 12f
        }
        container.addView(title)

        val mainIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolvedInfos = packageManager.queryIntentActivities(mainIntent, 0)

        for (info in resolvedInfos) {
            val pkg = info.activityInfo.packageName
            val label = info.loadLabel(packageManager).toString()
            
            // Filtering games (Common keywords)
            if (pkg.contains("game") || pkg.contains("pubg") || pkg.contains("mobile") || pkg.contains("freefire") || pkg.contains("cod")) {
                val btn = Button(this).apply {
                    text = label
                    background = null
                    setTextColor(0xFFFFFFFF.toInt())
                    setOnClickListener { launchApp(pkg) }
                }
                container.addView(btn)
            }
        }
    }

    private fun launchApp(pkg: String) {
        packageManager.getLaunchIntentForPackage(pkg)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
        } ?: Toast.makeText(this, "App not found!", Toast.LENGTH_SHORT).show()
    }

    private fun createNotification(): Notification {
        val channelId = "zenith_v1"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Zenith Engine", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Zenith OS Overlay Active").setSmallIcon(R.drawable.ic_launcher).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { try { windowManager.removeView(it) } catch (e: Exception) {} }
    }
}
