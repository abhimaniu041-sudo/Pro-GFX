package com.asgfx.bgmi

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ControllerActivity : AppCompatActivity() {

    private val tvIpAddress = "192.168.1.XX" // 🔥 Apna TV IP update karein
    private val port = 8888
    private var socket: DatagramSocket? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        Thread { socket = DatagramSocket() }.start()

        // 📝 BGMI Full Control List from your screenshots
        val bgmiControls = mapOf(
            R.id.btn_fire to "KEY_FIRE",
            R.id.btn_scope to "KEY_SCOPE",
            R.id.btn_jump to "KEY_JUMP",
            R.id.btn_crouch to "KEY_CROUCH",
            R.id.btn_prone to "KEY_PRONE",
            R.id.btn_reload to "KEY_RELOAD",
            R.id.btn_peek_left to "KEY_PEEK_L",
            R.id.btn_peek_right to "KEY_PEEK_R",
            R.id.btn_sprint to "KEY_SPRINT",
            R.id.joystick_base to "JOY_MOVE"
        )

        bgmiControls.forEach { (id, cmd) ->
            val view = findViewById<View>(id)
            setupGamingTouch(view, cmd)
            loadPosition(view, id.toString())
        }

        // Edit Mode Toggle: Long tap anywhere on background
        findViewById<View>(android.R.id.content).setOnLongClickListener {
            isEditMode = !isEditMode
            Toast.makeText(this, if(isEditMode) "Layout Edit On" else "Gaming Mode On", Toast.LENGTH_SHORT).show()
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGamingTouch(view: View, command: String) {
        var dX = 0f
        var dY = 0f

        view.setOnTouchListener { v, event ->
            if (isEditMode) {
                // DRAG LOGIC (To match your BGMI layout)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { dX = v.x - event.rawX; dY = v.y - event.rawY }
                    MotionEvent.ACTION_MOVE -> { v.x = event.rawX + dX; v.y = event.rawY + dY }
                    MotionEvent.ACTION_UP -> savePosition(v, v.id.toString())
                }
            } else {
                // 🔥 GAMING COMMANDS
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> sendCommand("${command}_START")
                    MotionEvent.ACTION_UP -> sendCommand("${command}_STOP")
                    MotionEvent.ACTION_MOVE -> if(command == "JOY_MOVE") {
                        sendCommand("AXIS_${event.x.toInt()}_${event.y.toInt()}")
                    }
                }
            }
            true
        }
    }

    private fun sendCommand(msg: String) {
        Thread {
            try {
                val packet = DatagramPacket(msg.toByteArray(), msg.length, InetAddress.getByName(tvIpAddress), port)
                socket?.send(packet)
            } catch (e: Exception) { e.printStackTrace() }
        }.start()
    }

    private fun savePosition(v: View, key: String) {
        getSharedPreferences("LayoutPrefs", Context.MODE_PRIVATE).edit()
            .putFloat("${key}_x", v.x).putFloat("${key}_y", v.y).apply()
    }

    private fun loadPosition(v: View, key: String) {
        val p = getSharedPreferences("LayoutPrefs", Context.MODE_PRIVATE)
        val x = p.getFloat("${key}_x", -1f)
        if (x != -1f) { v.x = x; v.y = p.getFloat("${key}_y", 0f) }
    }
}
