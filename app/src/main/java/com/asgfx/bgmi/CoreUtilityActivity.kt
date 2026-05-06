package com.asgfx.bgmi

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asgfx.bgmi.databinding.ActivityCoreUtilityBinding
import com.asgfx.bgmi.utils.DeviceUtils

class CoreUtilityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoreUtilityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoreUtilityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStats()
    }

    private fun setupStats() {
        binding.tvSdk.text = "Android Version: SDK ${Build.VERSION.SDK_INT}"
        // Dummy progress for RAM
        binding.pbRam.progress = DeviceUtils.getStoragePercent()
    }
}
