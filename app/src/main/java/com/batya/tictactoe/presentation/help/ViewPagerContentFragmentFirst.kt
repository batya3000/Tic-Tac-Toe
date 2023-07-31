package com.batya.tictactoe.presentation.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.batya.tictactoe.R
import com.batya.tictactoe.databinding.FragmentContentViewpagerFirstBinding

class ViewPagerContentFragmentFirst : Fragment(R.layout.fragment_content_viewpager_first) {
    private var _binding: FragmentContentViewpagerFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentViewpagerFirstBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}