package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.embehome.embehome.R
import com.embehome.embehome.databinding.CurtainFragmentBinding
import com.embehome.embehome.viewModel.CurtainViewModel

class Curtain : Fragment() {


      val viewModel: CurtainViewModel by viewModels ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : CurtainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.curtain_fragment, container, false)




        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }



}