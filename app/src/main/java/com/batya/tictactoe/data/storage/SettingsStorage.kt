package com.batya.tictactoe.data.storage


interface SettingsStorage {

    fun saveAreCrossesFirst(areCrossesFirst: Boolean)
    fun saveIsLightMode(isLightMode: Boolean)
    fun saveIsVibrationOn(isVibrationOn: Boolean)

    fun getAreCrossesFirst(): Boolean
    fun getIsLightMode(): Boolean
    fun getIsVibrationOn(): Boolean

}