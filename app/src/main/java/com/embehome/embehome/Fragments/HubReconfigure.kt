package com.embehome.embehome.Fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Services.HubServices
import com.embehome.embehome.StringConstants
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.databinding.FragmentHubRegistrationBinding
import com.embehome.embehome.instructions.HubInstructions
import com.embehome.embehome.viewModel.HubRegistrationViewModel
import kotlinx.android.synthetic.main.fragment_hub_registration.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/** com.tronx.tt_things_app.Fragments
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 15-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class HubReconfigure  : Fragment() {

    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentHubRegistrationBinding =  DataBindingUtil.inflate(inflater,
            R.layout.fragment_hub_registration, container, false)
        val viewModel : HubRegistrationViewModel by viewModels()


        callBack = object : ConnectivityManager.NetworkCallback () {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("TronX","network found")
                GlobalScope.launch (Dispatchers.Main) {
                    viewModel.wifiSSID.value = AppServices.wifiSSID(requireContext())
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("TronX","network lost")
                GlobalScope.launch (Dispatchers.Main) {
                    viewModel.wifiSSID.value = ""
                    findNavController().navigateUp()
                }
            }
        }
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callBack)

        viewModel.wifiSSID.value = AppServices.wifiSSID(requireContext())
        viewModel.wifiPassword.observe(viewLifecycleOwner, Observer {
            viewModel.isPasswordValid(Validators.passwordWifi(it.toCharArray()))
        })

        viewModel.isPasswordValid.observe(viewLifecycleOwner, Observer{

        })

        viewModel.errorWifiPassword.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.root.hub_reg_wifi_pwd_layout.error = "Invalid Password"
        })

        val temp = true
        viewModel._submit.observe(viewLifecycleOwner, Observer {
            if (it  && temp ) {
                GlobalScope.launch(Dispatchers.Main) {
                    AppServices.startLoadScreen(requireContext())
                    val success = HubServices.getHubDetails(
                        requireActivity().applicationContext,
                        viewModel.wifiPassword.value.toString()
                    )
                    if (success)
                    {
                        Toast.makeText(
                            requireContext(),
                            "Hub Reconfigured Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.hubRegBack()
                    }
                    else {
                        try {
                            Toast.makeText(
                                requireContext(),
                                StringConstants.HUB_CONFIGURE_FAIL_ERROR_MESSAGE,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        catch (e:Exception)
                        {

                        }
                    }
                    AppServices.stopLoadScreen()
                }
            }
        })

        viewModel.back.observe(viewLifecycleOwner, Observer {
            if (it)
                findNavController().navigateUp()
        })

        binding.textView215.setOnClickListener {
            HubInstructions().show(requireActivity().supportFragmentManager,"hello")
        }

        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigateUp()
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (::callBack.isInitialized) connectivityManager.unregisterNetworkCallback(callBack)
    }

}