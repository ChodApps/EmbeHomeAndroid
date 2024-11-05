package com.embehome.embehome.irb.fragment

import android.app.Dialog
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
import com.embehome.embehome.databinding.FragmentRemoteCategoryBinding
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.irb.RemoteCategoryEnum
import com.embehome.embehome.irb.adapter.RemoteCategoryAdapter
import com.embehome.embehome.irb.adapter.SelectIrbAdapter
import com.embehome.embehome.irb.viewmodel.RemoteCategoryViewModel
import com.embehome.embehome.irb.viewmodel.RemoteCmnViewModel
import kotlinx.android.synthetic.main._select_area.*

/**
 * A simple [Fragment] subclass.
 */
class RemoteCategory : Fragment() {

    val viewModel : RemoteCategoryViewModel by viewModels ()
    val cmnViewModel : RemoteCmnViewModel by activityViewModels ()
    lateinit var dialog : Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentRemoteCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_remote_category, container, false)

        val adapter = RemoteCategoryAdapter (requireContext(), RemoteCategoryEnum.values().filter {
           true
        }, viewModel.irbCategory)
        binding.remoteCategoryList.adapter = adapter

        viewModel.irbSelected.observe(viewLifecycleOwner, Observer {
//           AppServices.toastShort(requireContext(), "IRB  ${it.thing_name}")
            if (it != null) viewModel.irbName.value = it.thing_name
            if (::dialog.isInitialized) {
                dialog.dismiss()
            }
        })

        viewModel.irbCategory.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.notifyDataSetChanged()
            }

        })

        viewModel.actionTrigger.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {

                    "Back" -> {
                        requireActivity().finish()
                    }

                    "selectIRB" -> {
                        dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
                        dialog.setContentView(R.layout._select_area)
                        dialog.select_item_dialog_title.text = "Select IRB Device"
                        dialog.show()
                        dialog.list_area_select.adapter = SelectIrbAdapter (requireContext(), viewModel.irbList ?: ArrayList(), viewModel.irbSelected)


                        dialog.setCanceledOnTouchOutside(true)
                        dialog.select_area_root.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.select_item_or_text.visibility = View.GONE
                        dialog.goto_create_area.visibility = View.GONE
                    }

                    "Toast" -> {
                        Toast.makeText(requireContext(), viewModel.toastText, Toast.LENGTH_LONG).show()
                    }

                    "Continue" -> {
//                        if (viewModel.irbCategory.value?.name == "TV") {
//                            val remoteId = CacheRemoteData.generateRemoteID(viewModel.irbSelected.value!!.thing_serial_number)
//                            val direction = RemoteCategoryDirections.actionRemoteCategoryToRemoteTV2(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId)
//                            findNavController().navigate(direction)
//                        }
                        val cat = RemoteCategoryEnum.valueOf(viewModel.irbCategory.value?.name.toString())
                        initRemote(viewModel, cmnViewModel, cat)
                        findNavController().navigate(R.id.action_remoteCategory_to_IRRemote)
                        val remoteId = CacheRemoteData.generateRemoteID(viewModel.irbSelected.value!!.thing_serial_number)
//                        when (cat) {
//                            RemoteCategoryEnum.TV -> {
//
//                                val direction = RemoteCategoryDirections.actionRemoteCategoryToRemoteTV2(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId)
//                                findNavController().navigate(direction)
//                            }
//
//                           RemoteCategoryEnum.AC -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteAC(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                            RemoteCategoryEnum.DISH -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteSetupBox(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                            RemoteCategoryEnum.DVD -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteDVDPlayer(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                            RemoteCategoryEnum.SPEAKERS -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteAudioSystem(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                            RemoteCategoryEnum.TABLE_FAN -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteTableFan(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                            RemoteCategoryEnum.AIR_PURIFIER -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteAirPurifier(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                            RemoteCategoryEnum.COOLER -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteCooler(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//
//                            RemoteCategoryEnum.FAN -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteCeilingFan(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                            RemoteCategoryEnum.GEYSER -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteGeyser(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                            RemoteCategoryEnum.OVEN -> {
//                                findNavController().navigate(RemoteCategoryDirections.actionRemoteCategoryToRemoteOven(macID = CacheHubData.selectedMacID,  thingsID = viewModel.irbSelected.value?.thing_id ?: -1,remoteName =  viewModel.remoteName.value.toString(), serialNumber = viewModel.irbSelected.value!!.thing_serial_number, operationType = "Create", remoteID = remoteId))
//                            }
//                        }
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

    private fun initRemote (vm : RemoteCategoryViewModel, cvm : RemoteCmnViewModel, category : RemoteCategoryEnum) {
        cvm.irb.value = vm.irbSelected.value
        cvm.macID = CacheHubData.selectedMacID
        cvm.remoteID = CacheRemoteData.generateRemoteID(viewModel.irbSelected.value?.thing_serial_number!!)
        cvm.remoteCategory.value = category
        cvm.remoteIRData = HashMap()
        cvm.remoteName.value = vm.remoteName.value
    }

}
