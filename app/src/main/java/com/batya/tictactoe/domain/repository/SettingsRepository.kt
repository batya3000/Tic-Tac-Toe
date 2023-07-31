package com.batya.tictactoe.domain.repository

interface SettingsRepository {

    fun saveAreCrossesFirst(areCrossesFirst: Boolean)
    fun saveIsLightMode(isLightMode: Boolean)
    fun saveIsVibrationOn(isVibrationOn: Boolean)

    fun getAreCrossesFirst(): Boolean
    fun getIsLightMode(): Boolean
    fun getIsVibrationOn(): Boolean
}