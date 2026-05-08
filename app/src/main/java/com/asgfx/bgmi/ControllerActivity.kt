package com.asgfx.bgmi

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ControllerActivity : AppCompatActivity() {

    private val tvIpAddress = "192.168.1.XX" // 🔥 Apne TV ka IP yahan dalo
    private val port = 8888
    private var socket: DatagramSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        // Socket initialize karne ke liye alag thread
        Thread { socket = DatagramSocket() }.start()

        // Buttons setup
        setupButton(R.id.btn_a, "BTN_A")
        setupButton(R.id.btn_b, "BTN_B")
        setupButton(R.id.btn_x, "BTN_X")
        setupButton(R.id.btn_y, "BTN_Y")
        
        // Joystick/Touch area for movement
        findViewById<View>(R.id.joystick_base).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                sendCommand("MOVE_${event.x}_${event.y}")
            }
            true
        }
    }

    private fun setupButton(id: Int, command: String) {
        findViewById<View>(id).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> sendCommand("${command}_DOWN")
                MotionEvent.ACTION_UP -> sendCommand("${command}_UP")
            }
            true
        }
    }

    private fun sendCommand(msg: String) {
        Thread {
            try {
                val buf = msg.toByteArray()
                val packet = DatagramPacket(buf, buf.size, InetAddress.getByName(tvIpAddress), port)
                socket?.send(packet)
            } catch (e: Exception) { e.printStackTrace() }
        }.start()
    }
}
