package com.android.batya.tictactoe.data.storage.sharedprefs

import android.content.Context
import com.android.batya.tictactoe.data.storage.SettingsStorage
import com.android.batya.tictactoe.util.Constants.SHARED_PREFS_ARE_CROSSES_FIRST
import com.android.batya.tictactoe.util.Constants.SHARED_PREFS_SETTINGS
import com.android.batya.tictactoe.util.Constants.SHARED_PREFS_IS_VIBRATION_ON
import com.android.batya.tictactoe.util.Constants.SHARED_PREFS_IS_LIGHT_MODE

class SharedPrefSettingsStorage(context: Context) : SettingsStorage {

    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFS_SETTINGS,
        Context.MODE_PRIVATE
    )

    override fun saveAreCrossesFirst(areCrossesFirst: Boolean) {
        sharedPreferences.edit().putBoolean(SHARED_PREFS_ARE_CROSSES_FIRST, areCrossesFirst).apply()

    }
    override fun saveIsLightMode(isLightMode: Boolean) {
        sharedPreferences.edit().putBoolean(SHARED_PREFS_IS_LIGHT_MODE, isLightMode).apply()
    }
    override fun saveIsVibrationOn(isVibrationOn: Boolean) {
        sharedPreferences.edit().putBoolean(SHARED_PREFS_IS_VIBRATION_ON, isVibrationOn).apply()
    }

    override fun getAreCrossesFirst(): Boolean {
        return sharedPreferences.getBoolean(SHARED_PREFS_ARE_CROSSES_FIRST, true)
    }
    override fun getIsLightMode(): Boolean {
        return sharedPreferences.getBoolean(SHARED_PREFS_IS_LIGHT_MODE, true)

    }
    override fun getIsVibrationOn(): Boolean {
        return sharedPreferences.getBoolean(SHARED_PREFS_IS_VIBRATION_ON, true)
    }

}