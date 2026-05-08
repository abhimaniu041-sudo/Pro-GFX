package com.asgfx.bgmi.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
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

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var isExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(this)
        
        try {
            // 1. Inflate the layout
            floatingView = inflater.inflate(R.layout.layout_floating_control, null)

            // 2. Set Window Parameters (Modern Android Support)
            val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            // Initial Position
            params.gravity = Gravity.TOP or Gravity.START
            params.x = 100
            params.y = 200

            // 3. Add to Window
            windowManager?.addView(floatingView, params)

            // 4. Setup Controls
            setupControlButtons()
            loadDynamicModList()

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            stopSelf()
        }
    }

    private fun setupControlButtons() {
        floatingView?.let { view ->
            val featureContainer = view.findViewById<ScrollView>(R.id.featureContainer)
            
            view.findViewById<ImageView>(R.id.btnHide).setOnClickListener {
                isExpanded = !isExpanded
                featureContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            view.findViewById<ImageView>(R.id.btnRunBgmi).setOnClickListener {
                val launchIntent = packageManager.getLaunchIntentForPackage("com.pubg.imobile")
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(launchIntent)
                } else {
                    Toast.makeText(this, "BGMI not installed!", Toast.LENGTH_SHORT).show()
                }
            }

            view.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                stopSelf()
            }
        }
    }

    private fun loadDynamicModList() {
        floatingView?.let { view ->
            val modContainer = view.findViewById<LinearLayout>(R.id.modListLayout)
            val sharedPref = getSharedPreferences("ModSettings", Context.MODE_PRIVATE)
            
            val configFolder = File(getExternalFilesDir(null), "Configs")
            val zipFiles = configFolder.listFiles { file -> file.extension == "zip" }

            modContainer.removeAllViews()

            zipFiles?.forEach { file ->
                val fileName = file.name
                if (sharedPref.getBoolean(fileName, false)) {
                    val modItem = LayoutInflater.from(this).inflate(R.layout.item_mod_switch, null)
                    val tvName = modItem.findViewById<TextView>(R.id.tvModName)
                    val swMod = modItem.findViewById<SwitchCompat>(R.id.swModApply)

                    tvName.text = fileName
                    swMod.setOnCheckedChangeListener { _, isChecked ->
                        Toast.makeText(this, "${if (isChecked) "Applying" else "Removing"} $fileName", Toast.LENGTH_SHORT).show()
                    }
                    modContainer.addView(modItem)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let {
            windowManager?.removeView(it)
        }
    }
}
