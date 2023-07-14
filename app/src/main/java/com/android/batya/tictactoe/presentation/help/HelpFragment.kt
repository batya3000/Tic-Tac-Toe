package com.android.batya.tictactoe.presentation.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.android.batya.tictactoe.R
import com.android.batya.tictactoe.databinding.FragmentHelpBinding
import com.android.batya.tictactoe.util.gone
import com.android.batya.tictactoe.util.visible
import com.google.android.material.tabs.TabLayout
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
            findNavController().navigate(R.id.action_helpFragment_to_menuFragment)
        }
    }

}

class ScreenSlidePagerAdapter(fragment: Fragment, val mPageNumbers :Int) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int = mPageNumbers

    override fun createFragment(position: Int): Fragment {
        val fragment = ViewPagerContentFragment()

        when(position) {
            0 ->
                fragment.arguments = Bundle().apply {
                    putString("subtitle", "Ход игры")
                    putString("description", "На бесконечном поле игроки по очереди ставят свой знак (крестик или нолик)")
                    putInt("image", R.drawable.help_1)

                }
            1 ->
                fragment.arguments = Bundle().apply {
                    putString("subtitle", "Цель игры")
                    putString("description", "Построить непрерывный ряд из 5 фигур по горизонтали, вертикали или диагонали")
                    putInt("image", R.drawable.help_2)
                }
        }
        return fragment
    }
}