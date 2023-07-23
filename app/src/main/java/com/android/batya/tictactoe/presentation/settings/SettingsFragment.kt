package com.android.batya.tictactoe.presentation.settings

import android.R.id.message
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentSettingsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)


        settingsViewModel.loadSettings()
        observeSettings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.rgFirstTurn.setOnCheckedChangeListener { _, i ->
            settingsViewModel.saveAreCrossesFirst(i == R.id.bnCrosses)
            //Log.d("TAG", "onViewCreated: isCrossesFirst=$isCrossesFirst")
        }
        binding.rgTheme.setOnCheckedChangeListener { _, i ->
            settingsViewModel.saveIsLightMode(i == R.id.bnLight)

        }
        binding.rgSounds.setOnCheckedChangeListener { _, i ->
            settingsViewModel.saveIsVibrationOn(i == R.id.bnSoundsOn)
        }

        binding.bnTelegram.setOnClickListener {
            startTelegram()
        }
        binding.bnBug.setOnClickListener {
            sendEmail()
        }
        binding.bnDonate.setOnClickListener {
            startBoosty()
        }
    }

    private fun observeSettings() {
        settingsViewModel.areCrossesFirst.observe(viewLifecycleOwner) { areCrossesFirst ->
            if (areCrossesFirst) binding.rgFirstTurn.check(R.id.bnCrosses)
            else binding.rgFirstTurn.check(R.id.bnZeros)
        }
        settingsViewModel.isLightMode.observe(viewLifecycleOwner) { isLightMode ->
            if (isLightMode) binding.rgTheme.check(R.id.bnLight)
            else binding.rgTheme.check(R.id.bnDark)
        }
        settingsViewModel.isVibrationOn.observe(viewLifecycleOwner) { areSoundsOn ->
            if (areSoundsOn) binding.rgSounds.check(R.id.bnSoundsOn)
            else binding.rgSounds.check(R.id.bnSoundsOff)
        }
    }

    private fun startTelegram() {
        try {
            val telegramIntent = Intent(Intent.ACTION_VIEW)
            telegramIntent.data = Uri.parse("https://telegram.me/tictactoe_infinity")
            startActivity(telegramIntent)
        } catch (e: Exception) {
            // show error message
        }
    }
    private fun sendEmail() {
        val intent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "dev.gresss@gmail.com", null
            )
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, "Баг")
        intent.putExtra(Intent.EXTRA_TEXT, "Обнаружил баг: ")
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))

    }
    private fun startBoosty() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://boosty.to/tictactoe_infinity")
            startActivity(intent)
        } catch (e: Exception) {
            // show error message
        }
    }
}