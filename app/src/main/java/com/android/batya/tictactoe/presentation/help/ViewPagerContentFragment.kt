package com.android.batya.tictactoe.presentation.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentContentViewpagerBinding

class ViewPagerContentFragment : Fragment(R.layout.fragment_content_viewpager) {
    private var _binding: FragmentContentViewpagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentViewpagerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.apply {
            binding.tvSubtitle.text = getString("subtitle")!!
            binding.tvDescription.text = getString("description")!!
            binding.image.setImageResource(getInt("image"))
        }

    }
}