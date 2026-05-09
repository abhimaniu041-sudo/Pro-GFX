package com.asgfx.bgmi

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class ControllerActivity : AppCompatActivity() {

    private var hidDevice: BluetoothHidDevice? = null
    private val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var isEditMode = false

    // Standard HID Mapping
    private val KEY_MAP = mapOf(
        "FIRE" to 0x04, "SCOPE" to 0x08, "JUMP" to 0x01,
        "CROUCH" to 0x02, "PRONE" to 0x10, "RELOAD" to 0x20,
        "SPRINT" to 0x40, "PEEK_L" to 0x80, "PEEK_R" to 0x01
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        // 🛡️ CRASH FIX: Check Permissions for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE), 101)
                return
            }
        }

        if (btAdapter == null || !btAdapter.isEnabled) {
            Toast.makeText(this, "Please enable Bluetooth first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupBluetoothHID()

        val controls = mapOf(
            R.id.btn_fire_left to "FIRE", R.id.btn_scope to "SCOPE",
            R.id.btn_jump to "JUMP", R.id.btn_crouch to "CROUCH",
            R.id.btn_prone to "PRONE", R.id.btn_reload to "RELOAD",
            R.id.btn_sprint to "SPRINT", R.id.btn_peek_left to "PEEK_L",
            R.id.btn_peek_right to "PEEK_R", R.id.joystick_base to "JOYSTICK"
        )

        controls.forEach { (id, cmd) ->
            findViewById<View>(id)?.let { view ->
                setupGamingTouch(view, cmd)
                loadPosition(view, id.toString())
            }
        }

        findViewById<View>(R.id.controllerRoot).setOnLongClickListener {
            isEditMode = !isEditMode
            Toast.makeText(this, if (isEditMode) "Edit Mode: ON" else "Gaming Mode: ON", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupBluetoothHID() {
        try {
            btAdapter?.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    hidDevice = proxy as BluetoothHidDevice
                    val sdp = BluetoothHidDeviceAppSdpSettings(
                        "Zenith Pad", "Gamepad", "Google", BluetoothHidDevice.SUBCLASS1_COMBO, null
                    )
                    hidDevice?.registerApp(sdp, null, null, { it.run() }, object : BluetoothHidDevice.Callback() {
                        override fun onAppStatusChanged(device: BluetoothDevice?, reg: Boolean) {
                            if (reg) runOnUiThread { Toast.makeText(this@ControllerActivity, "HID Ready!", Toast.LENGTH_SHORT).show() }
                        }
                    })
                }
                override fun onServiceDisconnected(p: Int) { hidDevice = null }
            }, BluetoothProfile.HID_DEVICE)
        } catch (e: Exception) { e.printStackTrace() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGamingTouch(view: View, cmd: String) {
        var dX = 0f; var dY = 0f
        view.setOnTouchListener { v, event ->
            if (isEditMode) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { dX = v.x - event.rawX; dY = v.y - event.rawY }
                    MotionEvent.ACTION_MOVE -> { v.x = event.rawX + dX; v.y = event.rawY + dY }
                    MotionEvent.ACTION_UP -> savePosition(v, v.id.toString())
                }
            } else {
                if (cmd == "JOYSTICK") {
                    handleJoystick(event)
                } else {
                    val code = KEY_MAP[cmd] ?: 0x00
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> sendReport(code, true)
                        MotionEvent.ACTION_UP -> sendReport(code, false)
                    }
                }
            }
            true
        }
    }

    private fun handleJoystick(event: MotionEvent) {
        // Joystick movement logic for TV
        val x = ((event.x / findViewById<View>(R.id.joystick_base).width) * 255).toInt()
        val y = ((event.y / findViewById<View>(R.id.joystick_base).height) * 255).toInt()
        val report = ByteArray(3)
        report[1] = x.toByte()
        report[2] = y.toByte()
        hidDevice?.getConnectedDevices()?.forEach { hidDevice?.sendReport(it, 1, report) }
    }

    private fun sendReport(code: Int, pressed: Boolean) {
        val report = ByteArray(3)
        report[0] = if (pressed) code.toByte() else 0x00
        try {
            hidDevice?.getConnectedDevices()?.forEach { hidDevice?.sendReport(it, 1, report) }
        } catch (e: Exception) { e.printStackTrace() }
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
