package com.embehome.embehome.Activity

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.embehome.embehome.Model.PowerUnitModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.databinding.ActivityPowerAnalyticsBinding
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.powerNumber.Bar
import com.embehome.embehome.powerNumber.Line
import com.embehome.embehome.powerNumber.Pie
import com.embehome.embehome.registerNotification
import com.embehome.embehome.viewModel.ChartsDisplayViewModel

class PowerAnalytics : AppCompatActivity() {

    val viewModel : ChartsDisplayViewModel by viewModels()
    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityPowerAnalyticsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_power_analytics)

//        callBack = Validators.registerToWifi(this)

        binding.imageView242.setOnClickListener {
            finish()
        }

        val tabType = binding.tabLayout
        val tabTime = binding.tabLayout2

        val sno = intent.extras?.get("sno")
        val id = intent.extras?.get("id")
        val area = intent.extras?.get("area")


        if (sno != null && id != null) {
            viewModel.getSwitchPowerNumber(sno.toString(), id.toString().toInt())
        }
        else if (sno != null) {
            viewModel.getDevicePowerNumber(sno.toString())
        }
        else if (area != null) {
            viewModel.getAreaPowerNumber(area.toString().toInt())
        }
        else {
            finish()
        }

        /*if (sno == null || id == null)
            finish()
        else
            viewModel.getSwitchPowerNumber(sno.toString(), id.toString().toInt())*/

//        binding.bar.
        val pagerAdapter = ScreenSlidePagerAdapter(this, viewModel.data, viewModel.time)
        binding.viewpager2.adapter = pagerAdapter

        TabLayoutMediator(tabType, binding.viewpager2) { tab, position ->
            tab.text = when (position) {
                0 -> {
                    "BAR CHART"
                }

                1 -> {
                    "LINE CHART"
                }

                2 -> {
                    "PIE CHART"
                }
                else -> {
                    ""
                }
            }
        }.attach()

            /*tabType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabSelected(tab: TabLayout.Tab?) {

                }
            })*/

            tabTime.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    Log.d("TronX _ Power", "${tab?.text} reselected")
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    Log.d("TronX _ Power", "${tab?.text} unselected")
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Log.d("TronX _ Power", "${tab?.text} selected")
                    tab?.let {
                        when(it.text) {
                            "DAY" -> {
                                viewModel.time.value = PowerTimeDiff.DAY
                            }
                            "WEEK" -> {
                                viewModel.time.value = PowerTimeDiff.WEEK
                            }
                            "MONTH" -> {
                                viewModel.time.value = PowerTimeDiff.MONTH
                            }

                            else -> {

                            }
                        }
                    }
                }

            })


        }

        private inner class ScreenSlidePagerAdapter(fa: FragmentActivity,val  data : MutableLiveData<PowerUnitModel>,val time : MutableLiveData<PowerTimeDiff>) :
            FragmentStateAdapter(fa) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> {
                    Bar(data, time)
                }

                1 -> {
                    Line(data, time)
                }

                2 -> {
                    Pie(data, time)
                }
                else -> {
                    Bar(data, time)
                }
            }
        }

    @ExperimentalStdlibApi
    override fun onDestroy() {
        super.onDestroy()
//        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)
    }


    @ExperimentalStdlibApi
    override fun onStart() {
        super.onStart()
        registerNotification(this, this)
        callBack = Validators.registerToWifi(this)

    }

    override fun onStop() {
        super.onStop()
        deRegisterNotification(this)
        if (::callBack.isInitialized) Validators.unregisterToWifi(this, callBack)

    }

}

enum class PowerTimeDiff {
    DAY, WEEK, MONTH
}