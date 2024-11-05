package com.embehome.embehome.irb.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.embehome.embehome.R
import com.embehome.embehome.databinding.RemoteAirPurifierFragmentBinding

class RemoteAirPurifier : Fragment() {


    private val viewModel: RemoteAirPurifierViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : RemoteAirPurifierFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.remote_air_purifier_fragment, container, false)





//        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}