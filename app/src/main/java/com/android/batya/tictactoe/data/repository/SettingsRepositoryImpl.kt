package com.android.batya.tictactoe.data.repository

import com.android.batya.tictactoe.data.storage.SettingsStorage
import com.android.batya.tictactoe.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage) : SettingsRepository {
    override fun saveAreCrossesFirst(areCrossesFirst: Boolean) {
        settingsStorage.saveAreCrossesFirst(areCrossesFirst)
    }

    override fun saveIsLightMode(isLightMode: Boolean) {
        settingsStorage.saveIsLightMode(isLightMode)
    }

    override fun saveAreSoundsOn(areSoundsOn: Boolean) {
        settingsStorage.saveAreSoundsOn(areSoundsOn)
    }

    override fun getAreCrossesFirst(): Boolean {
        return settingsStorage.getAreCrossesFirst()
    }

    override fun getIsLightMode(): Boolean {
        return settingsStorage.getIsLightMode()
    }

    override fun getAreSoundsOn(): Boolean {
        return settingsStorage.getAreSoundsOn()
    }

}