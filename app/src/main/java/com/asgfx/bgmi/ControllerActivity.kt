package com.asgfx.bgmi

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ControllerActivity : AppCompatActivity() {

    private val tvIpAddress = "192.168.1.XX" // 🔥 Apne TV ka IP yahan dalo
    private val port = 8888
    private var socket: DatagramSocket? = null
    private var isEditMode = false // Edit layout toggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        Thread { socket = DatagramSocket() }.start()

        // Buttons IDs
        val buttonIds = listOf(R.id.btn_a, R.id.btn_b, R.id.btn_x, R.id.btn_y, R.id.joystick_base)
        
        buttonIds.forEach { id ->
            val view = findViewById<View>(id)
            setupControllerView(view, getCommandForId(id))
            loadPosition(view, id.toString()) // Purani saved position load karo
        }

        // Long press on screen to toggle Edit Mode
        findViewById<View>(android.R.id.content).setOnLongClickListener {
            isEditMode = !isEditMode
            val msg = if (isEditMode) "EDIT MODE: ON (Drag buttons)" else "EDIT MODE: OFF (Gaming mode)"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupControllerView(view: View, command: String) {
        var dX = 0f
        var dY = 0f

        view.setOnTouchListener { v, event ->
            if (isEditMode) {
                // 🔥 DRAG LOGIC (Edit Mode)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        v.x = event.rawX + dX
                        v.y = event.rawY + dY
                    }
                    MotionEvent.ACTION_UP -> {
                        savePosition(v, v.id.toString()) // Position save karlo
                    }
                }
            } else {
                // 🎮 GAMING LOGIC (Control Mode)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> sendCommand("${command}_DOWN")
                    MotionEvent.ACTION_UP -> sendCommand("${command}_UP")
                    MotionEvent.ACTION_MOVE -> {
                        if (command == "JOYSTICK") {
                            sendCommand("MOVE_${event.x.toInt()}_${event.y.toInt()}")
                        }
                    }
                }
            }
            true
        }
    }

    private fun getCommandForId(id: Int): String {
        return when (id) {
            R.id.btn_a -> "BTN_A"
            R.id.btn_b -> "BTN_B"
            R.id.btn_x -> "BTN_X"
            R.id.btn_y -> "BTN_Y"
            else -> "JOYSTICK"
        }
    }

    private fun savePosition(view: View, key: String) {
        val sharedPref = getSharedPreferences("ControllerPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putFloat("${key}_x", view.x)
            putFloat("${key}_y", view.y)
            apply()
        }
    }

    private fun loadPosition(view: View, key: String) {
        val sharedPref = getSharedPreferences("ControllerPrefs", Context.MODE_PRIVATE)
        val x = sharedPref.getFloat("${key}_x", -1f)
        val y = sharedPref.getFloat("${key}_y", -1f)
        if (x != -1f && y != -1f) {
            view.x = x
            view.y = y
        }
    }

    private fun sendCommand(msg: String) {
        Thread {
            try {
                val buf = msg.toByteArray()
                val packet = DatagramPacket(buf, buf.size, InetAddress.getByName(tvIpAddress), port)
                socket?.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
