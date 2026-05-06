package com.asgfx.bgmi.utils

import android.content.Context
import com.google.gson.Gson

object ProfileManager {
    private const val PREF_NAME = "as_gfx_prefs"
    
    fun saveStatus(context: Context, key: String, value: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getStatus(context: Context, key: String): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, false)
    }
}
