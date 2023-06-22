package com.android.batya.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.batya.tictactoe.databinding.FragmentGameBinding
import com.android.batya.tictactoe.databinding.FragmentMenuBinding
import com.android.batya.tictactoe.databinding.FragmentPauseBinding
import jp.wasabeef.blurry.Blurry

class PauseFragment : Fragment(R.layout.fragment_pause) {
    private var _binding: FragmentPauseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPauseBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Blurry.with(requireContext()).radius(25).sampling(2).onto(binding.layoutTitle)
    }
}