package com.embehome.embehome.irb.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.embehome.embehome.R
import com.embehome.embehome.databinding.FragmentRemoteRoundButtonBinding
import com.embehome.embehome.irb.viewmodel.RemoteRoundButtonViewModel

/**
 * A simple [Fragment] subclass.
 */
class RemoteRoundButton : Fragment() {

    val viewModel : RemoteRoundButtonViewModel by viewModels ()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentRemoteRoundButtonBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_remote_round_button, container, false)

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(requireContext(), viewModel.action, Toast.LENGTH_SHORT).show()
            }
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}
