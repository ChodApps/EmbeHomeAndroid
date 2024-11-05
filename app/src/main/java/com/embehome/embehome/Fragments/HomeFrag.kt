package com.embehome.embehome.Fragments


import android.annotation.SuppressLint
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Adapter.HubAdapter
import com.embehome.embehome.Manager.HUBManager
import com.embehome.embehome.Model.HubData
import com.embehome.embehome.R
import com.embehome.embehome.Services.MqttClient
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.databinding.FragmentHomeBinding
import com.embehome.embehome.notification.NotificationDisplay
import com.embehome.embehome.viewModel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalStdlibApi
class HomeFrag : Fragment() {

   // private lateinit var hubAdapter : HubAdapter
    private lateinit var implHubAdapter : HubAdapter
    val viewModel : HomeViewModel by viewModels()
    private val requestLocation = 1000



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel.getHubData(requireContext(), viewModel.tempHubList)
       /* FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(object :
            OnCompleteListener<InstanceIdResult> {
            override fun onComplete(task: Task<InstanceIdResult>) {
                if (!task.isSuccessful)
                    return

                task.result?.token?.let {
                    AppServices.saveToken(requireContext(), "fcm", it)
                    GlobalScope.launch (Dispatchers.Main) {
                        val res = HttpManager.addFCMToken(it)
                        if (res.status) {
                            Log.d("TronX", "FCM Registered $it")
                        } else {
                            Log.d("TronX", res.body.toString())
                        }
                    }
                }
            }

        })*/
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.viewModel = viewModel
        //hubAdapter = HubAdapter(requireContext(), viewModel.hubData)
        implHubAdapter = HubAdapter(requireContext(), viewModel.implHubData as MutableLiveData<ArrayList<HubData>?>)

        viewModel.implHubData!!.observe(viewLifecycleOwner, Observer {
            implHubAdapter.notifyDataSetChanged()
            implHubAdapter.data!!.value?.forEach {
                MqttClient.connect(requireActivity().applicationContext, it.macID)
            }

        })

        viewModel.tempHubList.observe(viewLifecycleOwner, Observer {
            if (it != null) {

            }
        })

    /*GlobalScope.launch (IO) {
        TronXDB.getDB(requireContext().applicationContext)?.hubDao()?.getHubList()?.forEach {
            it?.name?.let { it1 -> AppServices.log(it1) }
        }
    }*/
        binding.homeHubList.adapter = implHubAdapter

        viewModel.hubDataUpdated.observe(viewLifecycleOwner, Observer {
            if (it) {
              //  hubAdapter.notifyDataSetChanged()
                implHubAdapter.notifyDataSetChanged()
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                AppServices.startLoadScreen(requireContext())
            }
            else {
                AppServices.stopLoadScreen()
            }
        })


        viewModel.addHub.observe(viewLifecycleOwner, Observer {
            if (it) {
                GlobalScope.launch(Dispatchers.Main) {
                    AppServices.startLoadScreen(requireContext())
                    val packets = HUBManager.getHubToAdd()
                    AppServices.log("TronX _ HUb", packets.toList().toString())
                    when {
                        packets.size > 1 -> {
                            /*Toast.makeText(
                                        requireContext(),
                                        "Found Multiple Hubs",
                                        Toast.LENGTH_LONG
                                    ).show()*/
                            //openHubConfiguration()
                            AppDialogs.openMessageDialog(requireContext(), getString(R.string.multiple_hub_during_onboard))

                        }
                        packets.size == 0 -> {
                            openHubConfiguration()
//                            findNavController().navigate(HomeFragDirections.actionHomeFragToHubRegistration2())
                        }

                        packets.size == 1 -> {
                            if (packets[0].cmd == "UDPB" && packets[0].status == 0) {
                                FakeDB.packet = packets[0]
                                findNavController().navigate(HomeFragDirections.actionHomeFragToHubAdd())
                            } else openHubConfiguration()
                        }

                        else -> {
                            openHubConfiguration()
                        }
                    }

                    /* }
                        else {
                            AppDialogs.startMsgLoadScreen(requireContext(), "You can add only 4 hubs")
                        }*/
                    AppServices.stopLoadScreen()
                }
            }

        })

        viewModel.profile.observe(viewLifecycleOwner, Observer {
            if (it) {
               findNavController().navigate(R.id.action_homeFrag_to_userProfile)

            }
        })

        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (viewModel.toastMessage.value.toString() == "NOTIFICATION") {
                    requireActivity().startActivity(Intent(requireContext(), NotificationDisplay::class.java))
                }
                else
                Toast.makeText(requireContext(), viewModel.toastMessage.value.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        implHubAdapter.notifyDataSetChanged()
        CacheHubData.getMacIdList()
    }

    private fun openHubConfiguration () {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val wifi = AppServices.wifiSSID(requireContext())
            if (wifi != "WIFI_NOT_CONNECTED") {
                findNavController().navigate(HomeFragDirections.actionHomeFragToHubRegistration2())
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


    override fun onDestroy() {
        super.onDestroy()
        MqttClient.mqttDisconnect(requireActivity().applicationContext)
    }

}
