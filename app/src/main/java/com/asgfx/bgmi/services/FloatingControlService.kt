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

class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private lateinit var params: WindowManager.LayoutParams
    
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

        // 🔥 MASTER DRAG LOGIC (Smooth & Precise)
        floatingView?.setOnTouchListener(object : View.OnTouchListener {
            private var lastAction = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        lastAction = MotionEvent.ACTION_DOWN
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialTouchX).toInt()
                        val dy = (event.rawY - initialTouchY).toInt()
                        if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                            params.x = initialX + dx
                            params.y = initialY + dy
                            windowManager.updateViewLayout(floatingView, params)
                            lastAction = MotionEvent.ACTION_MOVE
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            v.performClick() // Trigger buttons if not moved
                        }
                        return true
                    }
                }
                return false
            }
        })

        setupUI()
        windowManager.addView(floatingView, params)
    }

    private fun setupUI() {
        floatingView?.let { v ->
            val expandedMenu = v.findViewById<View>(R.id.expandedMenu)
            val collapsedIcon = v.findViewById<View>(R.id.collapsedIcon)
            val modsView = v.findViewById<View>(R.id.modsView)
            val gamesView = v.findViewById<View>(R.id.gamesView)
            val gameGrid = v.findViewById<GridLayout>(R.id.gameGrid)

            // Minimize Logic
            v.findViewById<View>(R.id.btnHide).setOnClickListener {
                expandedMenu.visibility = View.GONE
                collapsedIcon.visibility = View.VISIBLE
            }
            collapsedIcon.setOnClickListener {
                collapsedIcon.visibility = View.GONE
                expandedMenu.visibility = View.VISIBLE
            }

            // Game Launcher Toggle
            v.findViewById<View>(R.id.btnSmartRun).setOnClickListener {
                modsView.visibility = View.GONE
                gamesView.visibility = View.VISIBLE
                loadGamesGrid(gameGrid)
            }

            // Back to Mods
            v.findViewById<View>(R.id.btnBackToMods).setOnClickListener {
                gamesView.visibility = View.GONE
                modsView.visibility = View.VISIBLE
            }

            v.findViewById<View>(R.id.btnRunBgmi).setOnClickListener { launchApp("com.pubg.imobile") }
            v.findViewById<View>(R.id.btnClose).setOnClickListener { stopSelf() }
        }
    }

    private fun loadGamesGrid(grid: GridLayout) {
        grid.removeAllViews()
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(mainIntent, 0)

        for (app in apps) {
            val pkg = app.activityInfo.packageName
            // Simple Game Detection (Filter common ones)
            if (pkg.contains("game") || pkg.contains("pubg") || pkg.contains("mobile") || pkg.contains("freefire") || pkg.contains("cod")) {
                
                val item = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setPadding(10, 10, 10, 10)
                    setOnClickListener { launchApp(pkg) }
                }

                val icon = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(90, 90)
                    setImageDrawable(app.loadIcon(pm))
                }

                val label = TextView(this).apply {
                    text = app.loadLabel(pm).toString().take(8) + ".."
                    setTextColor(0xFFFFFFFF.toInt())
                    textSize = 8f
                    gravity = Gravity.CENTER
                }

                item.addView(icon)
                item.addView(label)
                grid.addView(item)
            }
        }
    }

    private fun launchApp(pkg: String) {
        packageManager.getLaunchIntentForPackage(pkg)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
        }
    }

    private fun createNotification(): Notification {
        val channelId = "zenith_v2"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Zenith Engine", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId).setContentTitle("Zenith Active").setSmallIcon(R.drawable.ic_launcher).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { try { windowManager.removeView(it) } catch (e: Exception) {} }
    }
}
