package com.embehome.embehome.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Activity.AddSSB
import com.embehome.embehome.Activity.TwoWay
import com.embehome.embehome.R
import com.embehome.embehome.Utils.*
import com.embehome.embehome.databinding.FragmentRoomMenuBinding
import com.embehome.embehome.getHubImage
import com.embehome.embehome.notification.NotificationDisplay
import com.embehome.embehome.viewModel.RoomMenuViewModel

/**
 * A simple [Fragment] subclass.
 */
class RoomMenu : Fragment() {

    val viewModel : RoomMenuViewModel by viewModels()
    private val requestLocation = 1000

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentRoomMenuBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_room_menu, container, false)

        /*try {
            viewModel.hubIp.observe(viewLifecycleOwner, Observer {
                if (it != null)
                if (it.length > 1) CacheHubData.selectedHubIP = it
            })
        } catch (e: Exception) {

        }*/

        CacheHubData.getHub(CacheHubData.selectedMacID)?.let {
            getHubImage(it.image).let {
                binding.imageView21.setImageResource(it)
            }
            if (it.version.isNotEmpty()) {
                binding.hubVersion.text = "Hub version : ${it.version}"
            }
        }
        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            if (it)
            Toast.makeText(requireContext(), viewModel.toastString.value, Toast.LENGTH_SHORT).show()
        })

        viewModel.openAddBoard.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (CacheHubData.selectedHubIP.length > 1) {
//                    findNavController().navigate(RoomFragmentDirections.actionRoomFragment2ToAddBoard(FakeDB.macID))
                    try {
                        requireActivity().startActivity(Intent(requireContext(), AddSSB::class.java))
                    } catch (e : Exception) {
                       AppServices.toastShort(requireContext(),CacheHubData.homeNetwork)
                        AppServices.log("TronX" , e.stackTrace.toList().toString())
                    }
                }
                else
                    AppServices.toastShort(requireContext(),CacheHubData.homeNetwork)

            }
        })

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {

                    "UpdateHubFirmware" -> {
                        if (CacheHubData.selectedHubIP.length > 5) {
                            val hub = CacheHubData.getHub(CacheHubData.selectedMacID)
                            if (hub != null && (hub.version != "05.00.02" && hub.version != "5.0.2")) {
                                viewModel.checkAndUpdateHubFirmware(
                                    requireContext(),
                                    hub.macID,
                                    findNavController()
                                )
                            } else AppServices.toastShort(requireContext(), "No Update available")
                        } else AppServices.toastShort(requireContext(), CacheHubData.homeNetwork)
                    }


                    "TwoWay" -> {
                        CacheSceneTwoWay.twoWayPid = -1
                        CacheSceneTwoWay.twoWaySid = -1
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                TwoWay::class.java
                            )
                        )
                    }

                    "Finish" -> {
                        requireActivity().finish()
                    }

                    "HubReConfig" -> {
                        openHubConfiguration()
                    }

                    "UpdateHub" -> {
                        findNavController().navigate(R.id.action_roomMenu_to_updateHubData)
                    }

                    "DeleteHub" -> {
                        if (CacheHubData.selectedHubIP.length > 5)
                            viewModel.deleteHub(requireContext())
                        else AppServices.toastShort(requireContext(), CacheHubData.homeNetwork)
                    }

                    "Log" -> {
                        requireActivity().startActivity(
                            Intent(
                                requireContext(),
                                NotificationDisplay::class.java
                            )
                        )
                    }
                    else -> {

                    }
                }
            }
        })



        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun openHubConfiguration () {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val wifi = AppServices.wifiSSID(requireContext())
            if (wifi != "WIFI_NOT_CONNECTED") {
                findNavController().navigate(R.id.action_roomMenu_to_hubReconfigure)
            }
            else
                AppServices.toastShort(requireContext(),"Wifi not connected")
        }
        else {
            requestPermissions(Array<String> (2) {
                when (it) {
                    0 -> {android.Manifest.permission.ACCESS_FINE_LOCATION}
                    1 -> {android.Manifest.permission.ACCESS_COARSE_LOCATION}
                    else -> {android.Manifest.permission.ACCESS_FINE_LOCATION}
                }
            }, requestLocation)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestLocation && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openHubConfiguration()
        }
        else {
//            AppDialogs.openMessageDialog(requireContext(), "PLease give Location permission in Phone Settings to Configure Hub")
            AppDialogs.openTitleDialog(
                requireContext(),
                "Location Permission",
                "Please give location permission in phone settings to Configure Hub",
                "Close",
                "Settings"
            ){d, m ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.setData(Uri.fromParts("package", requireActivity().packageName, null))
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.hubVersion.value = CacheHubData.getHub(CacheHubData.selectedMacID)?.version
        viewModel.hubName.value = CacheHubData.getHub(CacheHubData.selectedMacID)?.name
    }

}
