package com.batya.tictactoe.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batya.tictactoe.domain.repository.SettingsRepository

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private var _areCrossesFirst: MutableLiveData<Boolean> = MutableLiveData()
    val areCrossesFirst: LiveData<Boolean> get() = _areCrossesFirst

    private var _isLightMode: MutableLiveData<Boolean> = MutableLiveData()
    val isLightMode: LiveData<Boolean> get() = _isLightMode

    private var _isVibrationOn: MutableLiveData<Boolean> = MutableLiveData()
    val isVibrationOn: LiveData<Boolean> get() = _isVibrationOn


    fun loadSettings() {
        _areCrossesFirst.value = settingsRepository.getAreCrossesFirst()
        _isLightMode.value = settingsRepository.getIsLightMode()
        _isVibrationOn.value = settingsRepository.getIsVibrationOn()
    }

    fun saveAreCrossesFirst(areCrossesFirst: Boolean) {
        settingsRepository.saveAreCrossesFirst(areCrossesFirst)
    }
    fun saveIsLightMode(isLightMode: Boolean) {
        settingsRepository.saveIsLightMode(isLightMode)
    }
    fun saveIsVibrationOn(isVibrationOn: Boolean) {
        settingsRepository.saveIsVibrationOn(isVibrationOn)
    }

}