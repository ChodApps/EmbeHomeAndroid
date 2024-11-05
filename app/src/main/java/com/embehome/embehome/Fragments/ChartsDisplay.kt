package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.databinding.ChartsDisplayFragmentBinding
import com.embehome.embehome.viewModel.ChartsDisplayViewModel


class ChartsDisplay : Fragment() {

   val  viewModel: ChartsDisplayViewModel by viewModels ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : ChartsDisplayFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.charts_display_fragment, container, false)



        binding.tabLyout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                AppServices.log("onTabReselect ${tab?.text}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                AppServices.log("onTabUnSelect ${tab?.text}")

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                AppServices.log("onTabSelect ${tab?.text}")
                when (tab?.text) {

                }
            }

        })


        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}