package com.android.batya.tictactoe.data.storage


interface SettingsStorage {

    fun saveAreCrossesFirst(areCrossesFirst: Boolean)
    fun saveIsLightMode(isLightMode: Boolean)
    fun saveAreSoundsOn(areSoundsOn: Boolean)

    fun getAreCrossesFirst(): Boolean
    fun getIsLightMode(): Boolean
    fun getAreSoundsOn(): Boolean

}