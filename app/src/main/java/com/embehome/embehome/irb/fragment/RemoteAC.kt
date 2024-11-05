package com.embehome.embehome.irb.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.embehome.embehome.R
import com.embehome.embehome.databinding.FragmentRemoteACBinding
import com.embehome.embehome.irb.viewmodel.RemoteACViewModel

/**
 * A simple [Fragment] subclass.
 */
class RemoteAC : Fragment() {

    val viewModel : RemoteACViewModel by viewModels ()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentRemoteACBinding =  DataBindingUtil.inflate(inflater, R.layout.fragment_remote_a_c, container, false)




//        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}
