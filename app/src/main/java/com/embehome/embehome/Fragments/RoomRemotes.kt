package com.embehome.embehome.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentRoomRemotesBinding
import com.embehome.embehome.irb.RemoteCategoryEnum
import com.embehome.embehome.irb.activity.RemoteAddOperations
import com.embehome.embehome.irb.adapter.IRBListRoomAdapter
import com.embehome.embehome.irb.model.RemoteCloudModel
import com.embehome.embehome.irb.viewmodel.RemoteCmnViewModel
import com.embehome.embehome.viewModel.RoomRemoteViewModel

/**
 * A simple [Fragment] subclass.
 */
class RoomRemotes : Fragment() {

    val viewModel : RoomRemoteViewModel by viewModels()
    private val remoteViewModel : RemoteCmnViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentRoomRemotesBinding=  DataBindingUtil.inflate(inflater, R.layout.fragment_room_remotes, container, false)

//        viewModel.hubIp.observe(viewLifecycleOwner, Observer {
//            if (it.length > 1) CacheHubData.selectedHubIP = it
//        })

        val adapter = IRBListRoomAdapter(requireContext(), viewModel.irbList.also {
            if (it.size > 0) {
                binding.textView244.visibility = View.GONE
            }
            else {
                binding.textView244.visibility = View.VISIBLE
            }
        }, viewModel.irbRemoteData) {

            if(initRemote(it, remoteViewModel))
                findNavController().navigate(R.id.action_roomRemotes_to_IRRemote3)

//            when (it.ir_category) {
//                "TV" -> {
//                    val direction = RoomRemotesDirections.actionRoomRemotesToRemoteTV3(macID = CacheHubData.selectedMacID,  thingsID = it.thing_id,remoteName =  it.remote_name, serialNumber = it.thing_serial_number, operationType = "Operate", remoteID = it.remote_id)
//                    findNavController().navigate(direction)
//                }
//            }
        }

        binding.recIrbListRoom.adapter = adapter

        viewModel.actionTrigger.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "AddRemote"  -> {
                        viewModel.action =""
                        if (CacheHubData.selectedHubIP.length > 5) {
                            if (CacheHubData.getAllIRBListAddRemote(CacheHubData.selectedMacID)
                                    .isNotEmpty()
                            ) startActivity(
                                Intent(
                                    requireActivity(),
                                    RemoteAddOperations::class.java
                                )
                            )
                            else
                                Toast.makeText(requireContext(), "Please add IRB to create remote", Toast.LENGTH_SHORT).show()
                        }
                        else
                            Toast.makeText(requireContext(), CacheHubData.homeNetwork, Toast.LENGTH_SHORT).show()
                    }

                    "refreshAdapter" -> {
                        adapter.notifyDataSetChanged()
                    }

                    "Exit" -> {
                        requireActivity().finish()
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

    private fun initRemote (remote : RemoteCloudModel, vm : RemoteCmnViewModel) : Boolean{
        val irb = CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, remote.thing_id)
            ?: return false
        vm.irb.value = irb
        vm.macID = CacheHubData.selectedMacID
        vm.remoteName.value = remote.remote_name
        vm.remoteID = remote.remote_id
        vm.remoteCategory.value = RemoteCategoryEnum.valueOf(remote.ir_category)
        vm.operationType = "Operate"
        vm.editMode.value = View.GONE
        vm.remoteIRData = remote.ir_data
        return true
    }

}
