package com.noraej.oving7.managers

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import androidx.preference.PreferenceManager
import com.noraej.oving7.R


class MyPreferenceManager(activity: Context) {
    private val resources = activity.resources
    private val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
    private val editor: SharedPreferences.Editor = preferences.edit()

    fun editPreference(color: String) {
        editor.putString(resources.getString(R.string.background_color), color)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }

    fun updateBackgroundColor(): Int {
        val backgroundColorValues = resources.getStringArray(R.array.background_color_values)
        val value = getString(
            resources.getString(R.string.background_color),
            resources.getString(R.string.background_color_default_value)
        )

        when (value) {
            backgroundColorValues[0] -> return Color.WHITE
            backgroundColorValues[1] -> return Color.BLUE
            backgroundColorValues[2] -> return Color.RED
        }
        return Color.WHITE //Default value
    }

    fun registerListener(activity: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(activity)
    }

    fun unregisterListener(activity: SharedPreferences.OnSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(activity)
    }
}
