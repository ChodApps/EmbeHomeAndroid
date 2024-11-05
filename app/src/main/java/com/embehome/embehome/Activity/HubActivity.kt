package com.embehome.embehome.Activity

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.embehome.embehome.*
import com.embehome.embehome.Services.MqttClient
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.databinding.ActivityHubBinding
import com.embehome.embehome.viewModel.HubActivityViewModel
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import kotlinx.android.synthetic.main.activity_hub.*

class HubActivity : AppCompatActivity() {

    @ExperimentalStdlibApi
    lateinit var callBack : ConnectivityManager.NetworkCallback
    private val viewModel : HubActivityViewModel by viewModels ()
    lateinit var navController :NavController


    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityHubBinding = DataBindingUtil.setContentView(this, R.layout.activity_hub)
//        navController = findNavController(R.id.fragment)
//        bottomNavigationView.setupNavController(navController)
        initNavHost()
        binding.setUpBottomNavigation()
//        if (CacheHubData.selectedHubIP.isNullOrEmpty()) viewModel.getHubIp(this, CacheHubData.selectedMacID)

        CacheHubData.getHub(CacheHubData.selectedMacID)?.let {
            if (CacheHubData.selectedHubIP.isNullOrEmpty() && it.version == "5.0.4") viewModel.getHubIp(this, CacheHubData.selectedMacID)

        }

        val hubIp = CacheHubData.getIp(CacheHubData.selectedMacID).also { h ->
            h.value.let {
                CacheHubData.selectedHubIP = it ?: ""
                if (it != null && it.length > 5) {
                    AppServices.saveToken(this, "ip${CacheHubData.selectedMacID}", it)
                    bottomNavigationView.models.get(2).icon = R.drawable.home_1
                }
                else {
                    bottomNavigationView.models.get(2).icon = R.drawable.home_0
                }
            }
        }




        callBack  = Validators.registerToWifi(this, lo = {
            hubIp.value = ""
        })


//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController

        AppServices.log("App version - " + BuildConfig.VERSION_NAME)

        CacheHubData.getHub(CacheHubData.selectedMacID)?.ip?.observe(this, Observer {
            CacheHubData.selectedHubIP = it ?: ""

            if (it.length > 5) {
                bottomNavigationView.models.get(2).icon = R.drawable.home_1
            }
            else {
                bottomNavigationView.models.get(2).icon = R.drawable.home_0
            }
        })

        /*val builder = NetworkRequest.Builder()
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_VPN)

        callBack = object : ConnectivityManager.NetworkCallback () {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d("TronX","network found")
                GlobalScope.launch (Dispatchers.Main) {
                    CacheHubData.getAllIpConnectedInNetwork()
                    AppDialogs.stopLoadScreen()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("TronX","network lost")
                GlobalScope.launch (Dispatchers.Main) {
                    AppDialogs.startMsgLoadScreen(this@HubActivity, "Internet Connection lost")
                   CacheHubData.getAllIpConnectedInNetwork()
                }
            }
        }
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(builder.build(), callBack)*/

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

//            if (destination.id == R.id.roomFragment2) {

            val menu = bottomNavigationView.models
           menu.get(2).title = CacheHubData.getHub(CacheHubData.selectedMacID)?.name ?: "EmbeHome"
//            }

        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }


    @ExperimentalStdlibApi
    override fun onStart() {
        super.onStart()
        /*notificationReceived.observe(this, Observer {
            if (it != null &&  it.length > 5) {
                AppServices.toastLong(this, it)
                notificationReceived.value = ""
            }
        })*/
        registerNotification(this, this)
        callBack  = Validators.registerToWifi(this, lo = {
            CacheHubData.getIp(CacheHubData.selectedMacID)?.value = ""
        })
    }

    @ExperimentalStdlibApi
    override fun onStop() {
        super.onStop()
//        notificationReceived.removeObservers(this)
        deRegisterNotification(this)
        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)

    }

    @ExperimentalStdlibApi
    override fun onRestart() {
        super.onRestart()
        MqttClient.connect(applicationContext, CacheHubData.selectedMacID)
    }
    companion object {
        // you can put any unique id here, but because I am using Navigation Component I prefer to put it as
        // the fragment id.
        const val Rules_ITEM = R.id.roomSwitchBoards
        const val Scenes_ITEM = R.id.roomScenes
        const val EmbeHome_ITEM = R.id.roomFragment2
        const val Remotes_ITEM = R.id.roomRemotes
        const val Settings_ITEM = R.id.roomMenu


    }
    @ExperimentalStdlibApi
    override fun onDestroy() {
        super.onDestroy()
//        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)
    }
    private fun initNavHost() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun ActivityHubBinding.setUpBottomNavigation() {
        val bottomNavigationItems = mutableListOf(
            CurvedBottomNavigation.Model(Rules_ITEM, "Rules", R.drawable.rules),
            CurvedBottomNavigation.Model(Scenes_ITEM, "Scenes", R.drawable.scene),
            CurvedBottomNavigation.Model(EmbeHome_ITEM, "EmbeHome", R.drawable.bottom_navigation_hub),
            CurvedBottomNavigation.Model(Remotes_ITEM, "Remotes", R.drawable.remote),
            CurvedBottomNavigation.Model(Settings_ITEM, "Settings", R.drawable.settingsnew),

        )
        bottomNavigationView.apply {
            bottomNavigationItems.forEach { add(it) }
            setOnClickMenuListener {
                navController.navigate(it.id)
            }
            // optional
            setupNavController(navController)
        }
    }
    // if you need your backstack of your items always back to home please override this method
    override fun onBackPressed() {
        if (navController.currentDestination!!.id == EmbeHome_ITEM)
            super.onBackPressed()
        else {
            when (navController.currentDestination!!.id) {
                Rules_ITEM -> {
                    navController.popBackStack(R.id.roomFragment2, false)
                }
                Scenes_ITEM -> {
                    navController.popBackStack(R.id.roomFragment2, false)
                }
                EmbeHome_ITEM -> {
                    navController.popBackStack(R.id.roomFragment2, false)
                }
                Remotes_ITEM -> {
                    navController.popBackStack(R.id.roomFragment2, false)
                }
                Settings_ITEM -> {
                    navController.popBackStack(R.id.roomFragment2, false)
                }
                else -> {
                    navController.navigateUp()
                }
            }
        }
    }

}
