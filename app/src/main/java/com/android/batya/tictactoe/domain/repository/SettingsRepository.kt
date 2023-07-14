package com.android.batya.tictactoe.domain.repository

interface SettingsRepository {

    fun saveAreCrossesFirst(areCrossesFirst: Boolean)
    fun saveIsLightMode(isLightMode: Boolean)
    fun saveAreSoundsOn(areSoundsOn: Boolean)

    fun getAreCrossesFirst(): Boolean
    fun getIsLightMode(): Boolean
    fun getAreSoundsOn(): Boolean
}