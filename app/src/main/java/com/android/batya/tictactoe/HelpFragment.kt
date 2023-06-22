package com.android.batya.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.batya.tictactoe.databinding.FragmentHelpBinding
import com.android.batya.tictactoe.settings.ViewPagerContentFragment
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
        binding.viewPager1.adapter = pagerAdapter
//        binding.viewPager2.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager1) { tab, position ->
        }
            .attach()
//        TabLayoutMediator(binding.tabLayout, binding.viewPager1) { tab, position ->
//        }.attach()
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
                    putString("description", "На бесконечном поле игркои по очереди ставят свой знак (крестик или нолик)")
                }
            1 ->
                fragment.arguments = Bundle().apply {
                    putString("subtitle", "Цель игры")
                    putString("description", "Построить непрерывный ряд из 5 фигур по горизонтали, вертикали или диагонали")
                }
        }
        return fragment
    }
}