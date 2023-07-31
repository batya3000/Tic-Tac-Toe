package com.batya.tictactoe.presentation.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.batya.tictactoe.R
import com.batya.tictactoe.databinding.FragmentHelpBinding
import com.batya.tictactoe.util.gone
import com.batya.tictactoe.util.visible
import com.google.android.material.tabs.TabLayoutMediator

class HelpFragment : Fragment(R.layout.fragment_help) {
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val pagerAdapter = ScreenSlidePagerAdapter(this, 2)
        binding.viewPager.adapter = pagerAdapter
        binding.bnNext.setOnClickListener {
            binding.viewPager.currentItem = 1
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ ->
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.bnNext.visible()
                } else {
                    binding.bnNext.gone()
                }
            }
        })
        binding.bnMainMenu.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}

class ScreenSlidePagerAdapter(fragment: Fragment, private val mPageNumbers: Int) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int = mPageNumbers

    override fun createFragment(position: Int): Fragment {
        val fragment1 = ViewPagerContentFragmentFirst()
        val fragment2 = ViewPagerContentFragmentSecond()

        return when(position) {
            0 -> fragment1
            else -> fragment2
        }
    }
}