package com.android.batya.tictactoe.presentation.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var isCrossesFirst = true
    private var isLightTheme = false
    private var isSoundsOn = false

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

//        val bundle = bundleOf("success" to "true")
//        bundle.putString(FirebaseAnalytics.Param.METHOD, "add_friend")
//        FirebaseAnalytics.getInstance(requireContext()).logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            val token = task.result
//            Log.d("TAG", "token=$token")
//        })
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
            settingsViewModel.saveAreSoundsOn(i == R.id.bnSoundsOn)
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
        settingsViewModel.areSoundsOn.observe(viewLifecycleOwner) { areSoundsOn ->
            if (areSoundsOn) binding.rgSounds.check(R.id.bnSoundsOn)
            else binding.rgSounds.check(R.id.bnSoundsOff)
        }
    }
}