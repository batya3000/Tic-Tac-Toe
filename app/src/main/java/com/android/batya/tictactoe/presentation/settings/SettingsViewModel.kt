package com.android.batya.tictactoe.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.batya.tictactoe.domain.model.Result
import com.android.batya.tictactoe.domain.model.Turn
import com.android.batya.tictactoe.domain.repository.GameRepository
import com.android.batya.tictactoe.domain.repository.SettingsRepository

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private var _areCrossesFirst: MutableLiveData<Boolean> = MutableLiveData()
    val areCrossesFirst: LiveData<Boolean> get() = _areCrossesFirst

    private var _isLightMode: MutableLiveData<Boolean> = MutableLiveData()
    val isLightMode: LiveData<Boolean> get() = _isLightMode

    private var _areSoundsOn: MutableLiveData<Boolean> = MutableLiveData()
    val areSoundsOn: LiveData<Boolean> get() = _areSoundsOn


    fun loadSettings() {
        _areCrossesFirst.value = settingsRepository.getAreCrossesFirst()
        _isLightMode.value = settingsRepository.getIsLightMode()
        _areSoundsOn.value = settingsRepository.getAreSoundsOn()
    }

    fun saveAreCrossesFirst(areCrossesFirst: Boolean) {
        settingsRepository.saveAreCrossesFirst(areCrossesFirst)
    }
    fun saveIsLightMode(isLightMode: Boolean) {
        settingsRepository.saveIsLightMode(isLightMode)
    }
    fun saveAreSoundsOn(areSoundsOn: Boolean) {
        settingsRepository.saveAreSoundsOn(areSoundsOn)
    }

}