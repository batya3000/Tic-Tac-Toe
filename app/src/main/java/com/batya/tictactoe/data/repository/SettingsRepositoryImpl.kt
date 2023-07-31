package com.batya.tictactoe.data.repository

import com.batya.tictactoe.data.storage.SettingsStorage
import com.batya.tictactoe.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage) : SettingsRepository {
    override fun saveAreCrossesFirst(areCrossesFirst: Boolean) {
        settingsStorage.saveAreCrossesFirst(areCrossesFirst)
    }

    override fun saveIsLightMode(isLightMode: Boolean) {
        settingsStorage.saveIsLightMode(isLightMode)
    }

    override fun saveIsVibrationOn(isVibrationOn: Boolean) {
        settingsStorage.saveIsVibrationOn(isVibrationOn)
    }

    override fun getAreCrossesFirst(): Boolean {
        return settingsStorage.getAreCrossesFirst()
    }

    override fun getIsLightMode(): Boolean {
        return settingsStorage.getIsLightMode()
    }

    override fun getIsVibrationOn(): Boolean {
        return settingsStorage.getIsVibrationOn()
    }

}