package com.asgfx.bgmi

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ControllerActivity : AppCompatActivity() {

    private var hidDevice: BluetoothHidDevice? = null
    private val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var isEditMode = false

    // HID Key Codes based on standard Gamepad Mapping
    private val KEY_MAP = mapOf(
        "FIRE" to 0x04,   // Button X
        "SCOPE" to 0x08,  // Button Y
        "JUMP" to 0x01,   // Button A
        "CROUCH" to 0x02, // Button B
        "PRONE" to 0x10,  // Button L1
        "RELOAD" to 0x20, // Button R1
        "SPRINT" to 0x40, // Button L2
        "PEEK_L" to 0x80, // Button R2
        "PEEK_R" to 0x01  // Secondary
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        if (btAdapter == null || !btAdapter.isEnabled) {
            Toast.makeText(this, "Enable Bluetooth First!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupBluetoothHID()

        // List of all buttons to make functional
        val controls = mapOf(
            R.id.btn_fire_left to "FIRE",
            R.id.btn_scope to "SCOPE",
            R.id.btn_jump to "JUMP",
            R.id.btn_crouch to "CROUCH",
            R.id.btn_prone to "PRONE",
            R.id.btn_reload to "RELOAD",
            R.id.btn_sprint to "SPRINT",
            R.id.btn_peek_left to "PEEK_L",
            R.id.btn_peek_right to "PEEK_R",
            R.id.joystick_base to "JOYSTICK"
        )

        controls.forEach { (id, cmd) ->
            findViewById<View>(id)?.let { view ->
                setupGamingTouch(view, KEY_MAP[cmd] ?: 0x00)
                loadPosition(view, id.toString())
            }
        }

        // Long press background to toggle Edit Mode
        findViewById<View>(R.id.controllerRoot).setOnLongClickListener {
            isEditMode = !isEditMode
            Toast.makeText(this, if(isEditMode) "Layout Edit Mode" else "Gaming Mode", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupBluetoothHID() {
        btAdapter?.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                hidDevice = proxy as BluetoothHidDevice
                val sdp = BluetoothHidDeviceAppSdpSettings(
                    "Zenith Controller", "Gamepad", "Google", BluetoothHidDevice.SUBCLASS1_COMBO, null
                )
                try {
                    hidDevice?.registerApp(sdp, null, null, { it.run() }, object : BluetoothHidDevice.Callback() {
                        override fun onAppStatusChanged(device: BluetoothDevice?, reg: Boolean) {
                            if (reg) runOnUiThread { Toast.makeText(this@ControllerActivity, "HID Ready!", Toast.LENGTH_SHORT).show() }
                        }
                    })
                } catch (e: Exception) { e.printStackTrace() }
            }
            override fun onServiceDisconnected(p: Int) { hidDevice = null }
        }, BluetoothProfile.HID_DEVICE)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGamingTouch(view: View, hidCode: Int) {
        var dX = 0f; var dY = 0f
        view.setOnTouchListener { v, event ->
            if (isEditMode) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { dX = v.x - event.rawX; dY = v.y - event.rawY }
                    MotionEvent.ACTION_MOVE -> { v.x = event.rawX + dX; v.y = event.rawY + dY }
                    MotionEvent.ACTION_UP -> savePosition(v, v.id.toString())
                }
            } else {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> sendReport(hidCode, true)
                    MotionEvent.ACTION_UP -> sendReport(hidCode, false)
                }
            }
            true
        }
    }

    private fun sendReport(code: Int, pressed: Boolean) {
        val report = ByteArray(3)
        report[0] = if (pressed) code.toByte() else 0x00
        hidDevice?.getConnectedDevices()?.forEach { hidDevice?.sendReport(it, 1, report) }
    }

    private fun savePosition(v: View, key: String) {
        getSharedPreferences("Layout", Context.MODE_PRIVATE).edit().putFloat("${key}x", v.x).putFloat("${key}y", v.y).apply()
    }

    private fun loadPosition(v: View, key: String) {
        val p = getSharedPreferences("Layout", Context.MODE_PRIVATE)
        val x = p.getFloat("${key}x", -1f)
        if (x != -1f) { v.x = x; v.y = p.getFloat("${key}y", 0f) }
    }
}
