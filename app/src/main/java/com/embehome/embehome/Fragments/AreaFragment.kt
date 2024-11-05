package com.embehome.embehome.Fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.Activity.FragmentHandler
import com.embehome.embehome.Adapter.BoardAreaAdapter
import com.embehome.embehome.Adapter.SwitchAreaAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Repository.OperationRepository
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.databinding.FragmentAreaBinding
import com.embehome.embehome.irb.adapter.MiniRemoteRoomListAdapter
import com.embehome.embehome.irb.fragment.RemoteTVMini
import com.embehome.embehome.viewModel.AreaFragmentViewModel

@ExperimentalStdlibApi
class AreaFragment : Fragment() {

    private val data : AreaFragmentArgs by navArgs()
    private val viewModel : AreaFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (data.areaId <= 0) {
            requireActivity().finish()
        }
        viewModel.areaID.value = data.areaId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentAreaBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_area, container, false)
        val boardAdapter = BoardAreaAdapter(requireContext() , viewModel.boards.value!!, viewModel.selectedBoard)
        binding.listAreaBoards.adapter = boardAdapter
        val switchAdapter = SwitchAreaAdapter(requireContext() , viewModel.switchData, viewModel.selectedSwitch, viewModel.selectSwitchDetail)
        val remoteAdapter = MiniRemoteRoomListAdapter (requireContext(), viewModel.remotes, viewModel.selectedRemote) {
            Toast.makeText(requireContext(), "You selected remote ${it.remote_name}", Toast.LENGTH_SHORT).show()
            viewModel.selectedRemote.value = it
        }

        viewModel.areaID.observe(viewLifecycleOwner, Observer {
            if (it != null){
                viewModel.getBoardData(requireContext(), data.macId, data.areaId)
            }
        })

        viewModel.boards.observe(viewLifecycleOwner, Observer {
            if (it != null && it.size > 0) {
                if (viewModel.selectedBoard.value == null)
                    viewModel.selectedBoard.value = 0
                boardAdapter.notifyDataSetChanged()
            }
        })

        viewModel.selectedBoard.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                when (viewModel.boards.value!![it].thing_type) {
                    "SSB" -> {
                        binding.alertContainer.visibility = View.GONE
                        viewModel.switchData.value = viewModel.boards.value!![it].switchesList
                        viewModel.selectSwitchDetail.value = 0
                        binding.listAreaSwitches.adapter = switchAdapter
                        switchAdapter.notifyDataSetChanged()
                        viewModel.setSleepStatus()
                    }
                    "CUR" -> {
                        viewModel.setSleepStatus()
                        binding.alertContainer.visibility = View.VISIBLE
//                        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.alertContainer, CurtainDisplay(viewModel.boards.value!![it].thing_id)).commit()
                    }
                    "IRB" -> {
                        binding.alertContainer.visibility = View.GONE
                        binding.fanController.visibility = View.GONE
                        binding.textView31.text =  "Add Remotes in Remote TAB"
                        binding.imageView22.setImageResource(R.drawable.bottom_navigation_remote)
                        viewModel.getRemoteList(CacheHubData.selectedMacID, viewModel.boards.value!![it].thing_serial_number)
                        binding.listAreaSwitches.adapter = remoteAdapter
                        remoteAdapter.notifyDataSetChanged()
                        viewModel.setSleepStatus()
                    }
                    else -> {
                        viewModel.boards.value!!.removeAt(it)
                        viewModel.selectedBoard.value = null
                    }
                }
                boardAdapter.notifyDataSetChanged()
            }
        })

        viewModel.selectedRemote.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.fanController.visibility = View.GONE
                binding.remoteContainer.visibility = View.VISIBLE
                binding.textView31.text =  viewModel.selectedRemote.value!!.remote_name
                binding.imageView22.setImageResource(R.drawable.bottom_navigation_remote)
                when (viewModel.selectedRemote.value!!.ir_category) {
                    "TV" -> {
                        requireActivity().supportFragmentManager.beginTransaction().replace( R.id.remoteContainer, RemoteTVMini(viewModel.selectedRemote)).commit()
                    }

                    else -> {
                        AppServices.log("Undefined Remote category")
                    }
                }

            }
            else {
//
            }
        })


        viewModel.selectSwitchDetail.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                try {
                    when (viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_type) {
                        "SSB" -> {
                            switchAdapter.notifyDataSetChanged()
                            val switch = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it]
                            binding.textView31.text = switch.switchName
                            viewModel.switchAlert.value = switch.alert_data != null
                            val rid = getSwitchStatusImage(switch.switchType,
                                switch.switchstatus)
                            binding.imageView22.setImageResource(rid)
                            binding.remoteContainer.visibility = View.GONE
                            if (switch.switchType == "B") {
                                binding.fanController.visibility = View.VISIBLE
                                requireActivity().supportFragmentManager.beginTransaction().add(R.id.fanController, FanController (switch.switchLevel) {level ->
                                    Log.d("TronX","level received $level")
                                    OperationRepository.performSwitchOperation(
                                        requireContext().applicationContext,
                                        CacheHubData.selectedMacID,
                                        CacheHubData.selectedAreaId,
                                        viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                                        viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchId,
                                        level,
                                        CacheHubData.selectedHubIP
                                    )
                                }).commit()
                            }
                            else {
                                binding.fanController.visibility = View.GONE
                            }
                        }

                        "IRB" -> {
//
                                            }
                    }
                }
                catch (e :Exception) {
                    AppServices.log(e.message.toString())
                    AppServices.log("TronX _ TCP" , e.stackTrace.toList().toString())
                }



            }
        })


        viewModel.selectedSwitch.observe(viewLifecycleOwner , Observer {
            viewModel.selectSwitchDetail.value = viewModel.selectedSwitch.value
            if (it >= 0) {
                if (false) {
                    viewModel.performAction(viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                        viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchId,
                        toggleSwitchStatus(viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchstatus))
                }

                OperationRepository.performSwitchOperation(
                    requireContext().applicationContext,
                    data.macId,
                    data.areaId,
                    viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                    viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchId,
                    toggleSwitchStatus(viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[it].switchstatus),
                    CacheHubData.selectedHubIP
                )

            }
        })

        viewModel.switchUpdate.observe(viewLifecycleOwner , Observer {
            if (it) {
                switchAdapter.notifyDataSetChanged()
            }
        })

        viewModel.toggleToPerformAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.operation) {
                    "SLEEP" -> {
                        OperationRepository.performSwitchSleepOperation(requireContext().applicationContext, CacheHubData.selectedMacID, CacheHubData.selectedAreaId,
                            viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id,
                            if (viewModel.sleep.value!!) 1 else 0, CacheHubData.selectedHubIP)
                    }
                    "setAlert" -> {
                        try {
                            val macID = CacheHubData.selectedMacID
                            val serialNo = viewModel.boards.value!![viewModel.selectedBoard.value!!].thing_id
                            val name = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[viewModel.selectSwitchDetail.value ?: 0].switchName
                            val switchId = viewModel.boards.value!![viewModel.selectedBoard.value!!].switchesList[viewModel.selectSwitchDetail.value ?: 0].switchId
                            val intent = getSetAlertIntent(requireActivity(), macID, serialNo, name, switchId)
                            startActivity(intent)
                        } catch (e: Exception) {
                            AppServices.log(e.message.toString())
                            AppServices.log("TronX " , e.stackTrace.toList().toString())
                        }
                    }
                    "refreshSwitchList" -> {
                        remoteAdapter.notifyDataSetChanged()
                        viewModel.selectedRemote.value = viewModel.remotes.value!![0]
                    }

                    "BACK" -> {
                        findNavController().navigateUp()
                    }

                    else -> {
                        Toast.makeText(requireContext() , viewModel.operation, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
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
                R.drawable.bulb_off
            }
            "D" -> {
                R.drawable.bulb_off
            }
            else -> {
                R.drawable.bulb_off
            }
        }
    }

    private fun toggleSwitchStatus(switchStatus : Int) : Int {
        return if (switchStatus == 0)
            1
        else
            0
    }

    private fun getSetAlertIntent (context: Context, macID : String, thingsID : Int, name : String, switchId: Int) : Intent {
        val intent = Intent (requireActivity(), FragmentHandler::class.java)
        intent.putExtra("fragment", "setAlert")
        intent.putExtra("macID", macID)
        intent.putExtra("thingsID", thingsID)
        intent.putExtra("name", name)
        intent.putExtra("switchId", switchId)
        return intent
    }
}
