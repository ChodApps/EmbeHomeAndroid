package com.embehome.embehome.Activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.embehome.embehome.Adapter.BoardAreaAdapter
import com.embehome.embehome.Adapter.SwitchAreaAdapter
import com.embehome.embehome.Fragments.DialerFragment
import com.embehome.embehome.Fragments.FanController
import com.embehome.embehome.R
import com.embehome.embehome.Repository.OperationRepository
import com.embehome.embehome.Services.MqttClient
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.curtain.fragment.CurtainDisplay
import com.embehome.embehome.databinding.ActivityAreaOperationBinding
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.getSwitchImages
import com.embehome.embehome.irb.adapter.MiniRemoteRoomListAdapter
import com.embehome.embehome.irb.fragment.RemoteTVMini
import com.embehome.embehome.registerNotification
import com.embehome.embehome.viewModel.AreaActivityViewModel

class AreaOperation : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    val viewModel : AreaActivityViewModel by viewModels()
    private lateinit var switchAdapter : SwitchAreaAdapter
    private lateinit var boardAdapter : BoardAreaAdapter
    lateinit var callBack : ConnectivityManager.NetworkCallback

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivityAreaOperationBinding = DataBindingUtil.setContentView(this, R.layout.activity_area_operation)
        binding.imageView13.setOnClickListener {
            PopupMenu(this, it).apply {
                setOnMenuItemClickListener(this@AreaOperation)
                inflate(R.menu.area_options)
                show()
            }
        }

        val area = CacheHubData.getArea(CacheHubData.selectedAreaId)
        viewModel.fragmentTitle.value = area.area_name
        if (area.image != null) {
            binding.imageView226.visibility = View.GONE
        }

        if (viewModel.boards.value != null) {
            boardAdapter = BoardAreaAdapter(this, viewModel.boards.value ?: ArrayList(), viewModel.selectedBoard)
            binding.listAreaBoards.adapter = boardAdapter
            viewModel.selectedBoard.value = 0
            switchAdapter =
                SwitchAreaAdapter(this , viewModel.switchData, viewModel.selectedSwitch, viewModel.selectSwitchDetail)
        }
        else {
            finish()
        }




        val remoteAdapter = MiniRemoteRoomListAdapter (this, viewModel.remotes, viewModel.selectedRemote) {
//            Toast.makeText(this, "You selected remote ${it.remote_name}", Toast.LENGTH_SHORT).show()

        }

//        try {
//            when (viewModel.boards.value?.get(viewModel.selectedBoard.value!!)?.thing_type) {
//                "SSB" -> {
//                    binding.remoteContainer.visibility = View.GONE
//                    viewModel.selectedRemote.value = null
//                    viewModel.switchData.value = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList
//                    viewModel.setSleepStatus()
//                    binding.listAreaSwitches.adapter = switchAdapter
//
//                }
//
//                "IRB" -> {
//                    viewModel.setSleepStatus()
//                    binding.fanController.visibility = View.GONE
//                    binding.remoteContainer.visibility = View.GONE
//                    viewModel.getRemoteList(CacheHubData.selectedMacID, viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_serial_number)
//                    binding.listAreaSwitches.adapter = remoteAdapter
//                }
//            }
//        }
//        catch (e : Exception) {
//            AppServices.log("${viewModel.boards.value?.get(viewModel.selectedBoard.value!!)?.thing_type} ${e.message}")
//        }

        viewModel.selectedRemote.observe(this, Observer {
            if (it != null) {
                viewModel.boards.value?.let {bl ->
                    viewModel.selectedBoard.value?.let {sb ->
                        if (bl[sb].thing_type == "IRB") {
                            binding.fanController.visibility = View.GONE
                            binding.remoteContainer.visibility = View.VISIBLE
                            binding.textView31.text =  viewModel.selectedRemote.value?.remote_name ?: ""
                            binding.imageView22.setImageResource(R.drawable.bottom_navigation_remote)
                            viewModel.deviceType.value = "Remotes"
                            supportFragmentManager.beginTransaction().replace( R.id.remoteContainer, RemoteTVMini(viewModel.selectedRemote)).commit()
                        }
                    }
                }
            }

            /*if (it != null && viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_type == "IRB") {
                binding.fanController.visibility = View.GONE
                binding.remoteContainer.visibility = View.VISIBLE
                binding.textView31.text =  viewModel.selectedRemote.value!!.remote_name
                binding.imageView22.setImageResource(R.drawable.bottom_navigation_remote)
                viewModel.deviceType.value = "Remotes"
                supportFragmentManager.beginTransaction().replace( R.id.remoteContainer, RemoteTVMini(viewModel.selectedRemote)).commit()
//                when (viewModel.selectedRemote.value!!.ir_category) {
//                    "TV" -> {
//                        supportFragmentManager.beginTransaction().replace( R.id.remoteContainer, RemoteTVMini(viewModel.selectedRemote)).commit()
//                    }
//
//                    else -> {
//                        AppServices.log("Undefined Remote category")
//                    }
//                }

            }
            else {
//                binding.fanController.visibility = View.GONE
//                binding.remoteContainer.visibility = View.GONE
//                binding.textView31.text = "Please Add Remote in Remote TAB"
            }*/
        })


        viewModel.selectSwitchDetail.observe(this, Observer {
            if (it != null) {
                try {
                    when (viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_type) {
                        "SSB", "ISB", "RSB" -> {
                            val switch = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it]
                            if (switch.switchstatus != 0) switch.switchstatus = switch.switchLevel
                            if (switch.switchType != "E") {
                                binding.imageView22.visibility = View.VISIBLE
                                switchAdapter.notifyDataSetChanged()
                                binding.textView31.text = switch.switchName
                                viewModel.switchAlert.value = switch.alert_data != null
                                val rid = getSwitchImages(
                                    switch.switchIconId,
                                    switch.switchstatus > 0
                                )
                                binding.imageView22.setImageResource(rid)
                                binding.remoteContainer.visibility = View.GONE
                                if (switch.switchType == "B") {

                                    /*if (switch.switchLevel != 999) {
                                        switch.switchstatus = switch.switchLevel + 0
                                        switch.switchLevel = 999
                                    }*/
                                    val ri = getSwitchStatusImage(
                                        switch.switchType,
                                        switch.switchLevel
                                    )
                                    binding.imageView22.setImageResource(ri)
                                    binding.fanController.visibility = View.VISIBLE
                                    supportFragmentManager.beginTransaction().replace(
                                        R.id.fanController,
                                        FanController(switch.switchstatus) { level ->
//                                            AppServices.toastShort(this, "Turning ${switch.switchName} to $level")
                                            OperationRepository.performSwitchOperation(
                                                applicationContext,
                                                CacheHubData.selectedMacID,
                                                CacheHubData.selectedAreaId,
                                                viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                                                viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchId,
                                                level,
                                                CacheHubData.selectedHubIP
                                            )
                                        }).commit()
                                } else if (switch.switchType == "C") {
                                    binding.fanController.visibility = View.VISIBLE
                                    binding.imageView22.visibility = View.GONE
                                    supportFragmentManager.beginTransaction().replace(
                                        R.id.fanController,
                                        DialerFragment(switch.switchstatus) { level ->
                                            Log.d("TronX", "level received $level")
                                            OperationRepository.performSwitchOperation(
                                                applicationContext,
                                                CacheHubData.selectedMacID,
                                                CacheHubData.selectedAreaId,
                                                viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                                                viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchId,
                                                level,
                                                CacheHubData.selectedHubIP
                                            )
                                        }).commit()
                                } else {
                                    binding.fanController.visibility = View.GONE
                                }
                            }
                        }

                        "IRB" -> {
//                            binding.remoteContainer.visibility = if (viewModel.selectedRemote.value != null ) View.VISIBLE else View.GONE
//                            binding.textView31.text = if (viewModel.selectedRemote.value != null) viewModel.selectedRemote.value!!.remote_name else "Please Add Remote in Remote TAB"

                        }

                    }
                }
                catch (e :Exception) {
                    AppServices.log(e.message.toString())
                    AppServices.log("TronX _ TCP" , e.stackTrace.toList().toString())
                }



            }
        })


        viewModel.selectedSwitch.observe(this , Observer {

            if (it >= 0) {
                /*if (false) {
                    viewModel.performAction(viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                        viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchId,
                        toggleSwitchStatus(viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchstatus))
                }
                else {
                    // Toast.makeText(this, "Operation is Limited to Home Network only", Toast.LENGTH_SHORT).show()
                }*/
                val type = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchType
                if (type == "E") {
                    val i = Intent(this, RemoteList::class.java)
                    i.putExtra("sno", viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_serial_number)
                    startActivity(i)
                }
                else {
                    viewModel.selectSwitchDetail.value = viewModel.selectedSwitch.value
                    OperationRepository.performSwitchOperation(
                        applicationContext,
                        CacheHubData.selectedMacID,
                        CacheHubData.selectedAreaId,
                        viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                        viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchId,
                        toggleSwitchStatus(viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchstatus,
                            viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchLevel,
                            type
                        ),
                        CacheHubData.selectedHubIP
                    )
                }
            }
        })

        viewModel.boards.observe(this, Observer {
            try {
                switchAdapter.notifyDataSetChanged()
                viewModel.selectSwitchDetail.value = viewModel.selectSwitchDetail.value
//            viewModel.selectedBoard.value =  viewModel.selectedBoard.value
                viewModel.boards.value?.let {bl ->
                    viewModel.selectedBoard.value?.let {sb ->
                        if (bl[sb].thing_type =="CUR" && bl[sb].switchesList.size > 0)
                            viewModel.selectedCurtainStatus.value = bl[sb].switchesList[0].switchstatus
                        }
                    }

//                if (viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_type == "CUR") {
//                    viewModel.selectedCurtainStatus.value =
//                        viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[0].switchstatus
//                }
                viewModel.setSleepStatus()
            }
            catch (e : Exception) {
                AppServices.log("TronX_sw" , e.message.toString())
            }
        })


        viewModel.switchUpdate.observe(this , Observer {
            if (it) {
                switchAdapter.notifyDataSetChanged()
            }
        })

        viewModel.selectedBoard.observe(this , Observer {
            if (it >= 0) {
                try {
                    boardAdapter.notifyDataSetChanged()
                    viewModel.selectedCurtainStatus.value = null

                    if (viewModel.boards.value == null){
                        finish()
                    }
                    else if (viewModel.boards.value != null && viewModel.boards.value?.size ?: 0 == 0) {
                        finish()
                    }

                    viewModel.boards.value?.let {bl ->
                        when (bl[it].thing_type) {
                            "SSB", "ISB", "RSB" -> {
                                viewModel.deviceType.value = "Switches"
                                binding.imageView22.visibility = View.VISIBLE
                                binding.alertContainer.visibility = View.GONE
                                viewModel.selectSwitchDetail.value = 0
                                viewModel.switchData.value = bl[it].switchesList
                                binding.listAreaSwitches.adapter = switchAdapter
                                switchAdapter.notifyDataSetChanged()
                                viewModel.setSleepStatus()
                            }

                            "IRB" -> {
                                viewModel.deviceType.value = ""
                                binding.alertContainer.visibility = View.GONE
                                binding.remoteContainer.visibility = View.GONE
                                viewModel.setSleepStatus()
                                binding.textView31.text =  ""
                                binding.imageView22.setImageResource(R.drawable.bottom_navigation_remote)
                                binding.imageView22.visibility = View.GONE
                                viewModel.getRemoteList(CacheHubData.selectedMacID, bl[it].thing_serial_number)
                                binding.fanController.visibility = View.GONE
                                binding.listAreaSwitches.adapter = remoteAdapter
                                remoteAdapter.notifyDataSetChanged()
                            }

                            "CUR" -> {
                                viewModel.setSleepStatus()
                                binding.alertContainer.visibility = View.VISIBLE
                                /*if (bl[it].switchesList[0].switchLevel != 999) {
                                    bl[it].switchesList[0].switchstatus = bl[it].switchesList[0].switchLevel + 0
                                    bl[it].switchesList[0].switchLevel = 999
                                }*/
                                viewModel.selectedCurtainStatus.value = bl[it].switchesList[0].switchstatus
                                supportFragmentManager.beginTransaction().replace(R.id.alertContainer, CurtainDisplay(bl[it].thing_id, viewModel.selectedCurtainStatus)).commit()
                            }
                            else -> {
                                finish()
                            }
                        }
                    }

                }
                catch (e : Exception) {
                    AppServices.log(e.message.toString())
                    AppServices.log("TronX _ TCP" , e.stackTrace.toList().toString())
                }
            }
        })

        viewModel.toggleToPerformAction.observe(this, Observer {
            if (it) {
                when (viewModel.operation) {
                    "SLEEP" -> {
                        OperationRepository.performSwitchSleepOperation(applicationContext, CacheHubData.selectedMacID, CacheHubData.selectedAreaId,
                                viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                                if (viewModel.sleep.value!!) 1 else 0, CacheHubData.selectedHubIP)
                    }

                    "Update" -> {

                        viewModel.isSetting.value = true
                        val intent = Intent (this, FragmentHandler::class.java)
                        intent.putExtra("fragment", "updateArea")
                        intent.putExtra("macID", CacheHubData.selectedMacID)
                        intent.putExtra("thingsID", viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id)
                        startActivity(intent)
                       /* binding.areaSettings.visibility = View.VISIBLE
                        supportFragmentManager.beginTransaction().replace(R.id.areaSettings, AreaSettings(CacheHubData.selectedMacID,
                            viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id )).addToBackStack("setting").commit()*/
//                        supportFragmentManager.beginTransaction().replace(R.id.areaSettings, DeviceConfig(viewModel.boards.value!![viewModel.selectedBoard.value!!])).addToBackStack("setting").commit()
                    }
                    "setAlert" -> {
                        try {
                            val macID = CacheHubData.selectedMacID
                            val serialNo = viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id
                            val name = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[viewModel.selectSwitchDetail.value ?: 0].switchName
                            val switchId = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[viewModel.selectSwitchDetail.value ?: 0].switchId
                            val intent = getSetAlertIntent(this, macID, serialNo, name, switchId)
                            startActivity(intent)
                        } catch (e: Exception) {
                            AppServices.log(e.message.toString())
                            AppServices.log("TronX _ TCP" , e.stackTrace.toList().toString())
                        }
                    }

                    "Power" -> {
                        val serialNo = viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_serial_number
                        val switchId = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[viewModel.selectSwitchDetail.value ?: 0].switchId
                        val intent = Intent(this, PowerAnalytics::class.java)
                        intent.putExtra("sno", serialNo)
                        intent.putExtra("id",switchId)
                        startActivity(intent)
//                        AppServices.toastShort(this, "Work In progress")
                    }

                    "refreshSwitchList" -> {
                        remoteAdapter.notifyDataSetChanged()
//                        viewModel.selectedRemote.value = viewModel.remotes.value!![0]
                    }

                    "BACK" -> {
                        finish()
                    }
                    "DeleteBoard" -> {
                        viewModel.deleteDevice(this)
                    }

                    else -> {
                        Toast.makeText(this , viewModel.operation, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })



        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun getSwitchStatusImage (switchType : String, status : Int) : Int {
       return when (switchType) {
            "A" -> {
                if (status > 0)
                    R.drawable.bulb_on
                else
                    R.drawable.bulb_off
            }
            "B" -> {
                when(status) {
                    1 -> R.drawable.fan_regulator_controller_01
                    2 -> R.drawable.fan_regulator_controller_02
                    3 -> R.drawable.fan_regulator_controller_03
                    4 -> R.drawable.fan_regulator_controller_04
                    else -> R.drawable.fan_regulator_controller_00
                }
            }
            "C"  -> {
                if (status > 0)
                    R.drawable.tt_device_enable_led
                else
                    R.drawable.tt_device_disable_led
            }
            "D" -> {
                R.drawable.bulb_off
            }
            else -> {
                R.drawable.bulb_off
            }
        }
    }

    private fun toggleSwitchStatus(switchStatus : Int, level : Int, type : String) : Int {
        return if (switchStatus == 0 && type == "A" || level == 0)
            1
        else if (switchStatus == 0)
        level
        else
            0
    }

    private fun getSetAlertIntent (context: Context, macID : String, thingsID : Int, name : String, switchId: Int) : Intent{
        val intent = Intent (this, FragmentHandler::class.java)
        intent.putExtra("fragment", "setAlert")
        intent.putExtra("macID", macID)
        intent.putExtra("thingsID", thingsID)
        intent.putExtra("name", name)
        intent.putExtra("switchId", switchId)
        return intent
    }

    override fun onResume() {
        super.onResume()


    }

    @ExperimentalStdlibApi
    override fun onStart() {
        super.onStart()
        registerNotification(this, this)
        callBack = Validators.registerToWifi(this, av = {
            MqttClient.connect(applicationContext, CacheHubData.selectedMacID)
            MqttClient.subscribe(this, CacheHubData.selectedMacID)
        }, lo = {
            CacheHubData.getHub(CacheHubData.selectedMacID)?.ip?.value = ""
            CacheHubData.selectedHubIP = ""
        })
        MqttClient.connect(applicationContext, CacheHubData.selectedMacID)
        if (viewModel.isSetting.value == true) {
            try {

                if (viewModel.boards.value != null && viewModel.boards.value?.size ?: 0 > 0) {
                    viewModel.boards.value = viewModel.boards.value
                    viewModel.selectedBoard.value = viewModel.selectedBoard.value
                    viewModel.isSetting.value = false
                } else {
                    finish()
                }
            }
            catch (e : Exception) {
                e.message?.let { Log.d("TronX", it) }
            }
        }

        if (::switchAdapter.isInitialized) switchAdapter.notifyDataSetChanged()
        if (::boardAdapter.isInitialized) boardAdapter.notifyDataSetChanged()
    }


    @ExperimentalStdlibApi
    override fun onRestart() {
        super.onRestart()
//        MqttClient.connect(applicationContext, CacheHubData.selectedMacID)
    }

    override fun onStop() {
        super.onStop()
        deRegisterNotification(this)
        if (::callBack.isInitialized)Validators.unregisterToWifi(this, callBack)
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        when (p0?.itemId) {
            R.id.irb_menu_delete -> {
                viewModel.delete()
            }
            R.id.irb_menu_edit -> {
                viewModel.updateArea()
            }
            R.id.power -> {
                val intent = Intent(this, PowerAnalytics::class.java)
                intent.putExtra("area", CacheHubData.selectedAreaId)
                startActivity(intent)
            }
            R.id.devicePower -> {
                val type = viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_type
                if (type == "SSB" || type == "ISB" || type == "RSB") {
                    val intent = Intent(this, PowerAnalytics::class.java)
                    intent.putExtra("sno",viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_serial_number)
                    startActivity(intent)
                }
                else {
                    AppDialogs.openMessageDialog(this, "Please select a smart switch board to see the power consumption.")
                }
            }

        }
        return true
    }

    @ExperimentalStdlibApi
    override fun onDestroy() {
        super.onDestroy()
//        if (::callBack.isInitialized)Validators.unregisterToWifi(this, callBack)
    }

}
