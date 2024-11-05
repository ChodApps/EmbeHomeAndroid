package com.embehome.embehome.Fragments


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
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
import com.embehome.embehome.Manager.HUBManager
import com.embehome.embehome.R
import com.embehome.embehome.Services.HubServices
import com.embehome.embehome.StringConstants
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.databinding.FragmentHubRegistrationBinding
import com.embehome.embehome.instructions.HubInstructions
import com.embehome.embehome.viewModel.HubRegistrationViewModel
import kotlinx.android.synthetic.main.fragment_hub_registration.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class HubRegistration : Fragment() {

    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentHubRegistrationBinding =  DataBindingUtil.inflate(inflater,R.layout.fragment_hub_registration, container, false)
        val viewModel : HubRegistrationViewModel by viewModels()

        val builder = NetworkRequest.Builder()
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_VPN)

        callBack = object : ConnectivityManager.NetworkCallback () {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("TronX","network found")
                GlobalScope.launch (Main) {
                viewModel.wifiSSID.value = AppServices.wifiSSID(requireContext())
                }
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("TronX","network lost")
                GlobalScope.launch (Main) {
                viewModel.wifiSSID.value = ""
                    findNavController().navigateUp()
                }
            }
        }
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(builder.build(), callBack)

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
                    if (success) {
                        delay(0)
                        val result =
                            HUBManager.getHubToAdd()
                        if (result.size > 1 || result.size == 0) {
                            delay(5000)
                            val res =
                                HUBManager.getHubToAdd()
                            if (res.size > 1 || res.size == 0) {
                                delay(5000)
                                val res1 =
                                    HUBManager.getHubToAdd()
                                if (res1.size > 1 || res1.size == 0) {
                                    Toast.makeText(
                                        activity,
                                        if (res1.size > 1) "More than 1 Hub found, please switch off all the extra hubs" else "Hub not found",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else {
                                    FakeDB.packet = res1[0]
                                    findNavController().navigate(HubRegistrationDirections.actionHubRegistrationToHubAdd())
                                }
                            }
                            else {
                                FakeDB.packet = res[0]
                                findNavController().navigate(HubRegistrationDirections.actionHubRegistrationToHubAdd())
                            }
                        }
                        /*Toast.makeText(
                            activity,
                            "Found Multiple Hubs",
                            Toast.LENGTH_LONG
                        ).show()*/
                        else {
                            /*val packet = result[0]
                            val data = HttpClient.registerHub(packet, viewModel.wifiSSID.value.toString())
                            Log.d("TronX", data.value!!.message)
                            val res = LocalNetworkClient.writeOnTcp(
                                FakeDB.HubIp,
                                NetworkHosts.TCP_PORT,
                                "{AAAA^${FakeDB.ApplicationToken}"
                            )
                            Log.d("TronX", "TCP packet received $res")
                            val response = HttpClient.updateHubDetails(res)
                            Log.d("TronX", "${response.value}")*/
                            FakeDB.packet = result[0]
                            findNavController().navigate(HubRegistrationDirections.actionHubRegistrationToHubAdd())
                        }
                    } else {
                        Toast.makeText(activity, StringConstants.HUB_CONFIGURE_FAIL_ERROR_MESSAGE, Toast.LENGTH_LONG).show()
                    }
                    AppServices.stopLoadScreen()
                }
            }
        })

        viewModel.back.observe(viewLifecycleOwner, Observer {
            if (it)
                findNavController().navigateUp()
        })

        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigateUp()
        }

        binding.textView215.setOnClickListener {
           HubInstructions().show(requireActivity().supportFragmentManager,"hello")
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
