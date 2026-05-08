package com.asgfx.bgmi

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ControllerActivity : AppCompatActivity() {

    private var hidDevice: BluetoothHidDevice? = null
    private var isEditMode = false
    private val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    // 📝 HID Constants for Gamepad
    private val BUTTON_X = 0x04 // Fire
    private val BUTTON_Y = 0x08 // Scope
    private val BUTTON_A = 0x01 // Jump
    private val BUTTON_B = 0x02 // Crouch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        makeDiscoverable() // 🔥 Phone ko list mein lane ke liye
        setupBluetoothHID()

        val bgmiControls = mapOf(
            R.id.btn_fire to BUTTON_X,
            R.id.btn_scope to BUTTON_Y,
            R.id.btn_jump to BUTTON_A,
            R.id.btn_crouch to BUTTON_B,
            R.id.btn_reload to 0x10,
            R.id.btn_peek_left to 0x40,
            R.id.btn_peek_right to 0x80,
            R.id.btn_sprint to 0x20
        )

        bgmiControls.forEach { (id, cmd) ->
            findViewById<View>(id)?.let { view ->
                setupGamingTouch(view, cmd)
                loadPosition(view, id.toString())
            }
        }

        findViewById<View>(android.R.id.content).setOnLongClickListener {
            isEditMode = !isEditMode
            Toast.makeText(this, if(isEditMode) "Edit Mode On" else "Gaming Mode On", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun makeDiscoverable() {
        // TV ko phone dikhane ke liye advertise mode on karna zaroori hai
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivity(discoverableIntent)
    }

    private fun setupBluetoothHID() {
        btAdapter?.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                if (profile == BluetoothProfile.HID_DEVICE) {
                    hidDevice = proxy as BluetoothHidDevice
                    
                    // 🔥 ZAROORI: TV ko batana ki ye ek Gamepad hai
                    val sdp = BluetoothHidDeviceAppSdpSettings(
                        "Pro GFX Controller",
                        "Zenith Gamepad",
                        "Google",
                        BluetoothHidDevice.SUBCLASS1_COMBO,
                        null
                    )
                    
                    hidDevice?.registerApp(sdp, null, null, { it.run() }, object : BluetoothHidDevice.Callback() {
                        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
                            runOnUiThread {
                                if (registered) Toast.makeText(this@ControllerActivity, "Controller Ready: Pair with TV now!", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }
            }
            override fun onServiceDisconnected(profile: Int) {
                hidDevice = null
            }
        }, BluetoothProfile.HID_DEVICE)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGamingTouch(view: View, hidCode: Int) {
        var dX = 0f
        var dY = 0f

        view.setOnTouchListener { v, event ->
            if (isEditMode) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { dX = v.x - event.rawX; dY = v.y - event.rawY }
                    MotionEvent.ACTION_MOVE -> { v.x = event.rawX + dX; v.y = event.rawY + dY }
                    MotionEvent.ACTION_UP -> savePosition(v, v.id.toString())
                }
            } else {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> sendHidReport(hidCode, true)
                    MotionEvent.ACTION_UP -> sendHidReport(hidCode, false)
                }
            }
            true
        }
    }

    private fun sendHidReport(buttonCode: Int, isPressed: Boolean) {
        val report = ByteArray(3)
        report[0] = if (isPressed) buttonCode.toByte() else 0x00
        hidDevice?.getConnectedDevices()?.forEach { device ->
            hidDevice?.sendReport(device, 1, report)
        }
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
