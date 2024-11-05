package com.embehome.embehome.scene.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentSceneRemoteIntegrationBinding
import com.embehome.embehome.irb.adapter.SelectIrbAdapter
import com.embehome.embehome.irb.getResourceRemoteIcon
import com.embehome.embehome.scene.adapter.SceneSelectedRemoteBtn
import com.embehome.embehome.scene.adapter.SelectRemoteAdapter
import com.embehome.embehome.scene.adapter.SelectRemoteButtonAdapter
import com.embehome.embehome.scene.viewModel.EditSceneViewModel
import kotlinx.android.synthetic.main._list_name_icon.view.*
import kotlinx.android.synthetic.main._select_area.*

class SceneRemoteIntegration : Fragment() {

    val activityViewModel : EditSceneViewModel by activityViewModels ()
    lateinit var dialog : Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activityViewModel.sceneEdit) {
            activityViewModel.irbSaveButton.value = activityViewModel.update
            if (activityViewModel.selectedButtonList.value?.size ?: 0 > 0) {
                val device = CacheSceneTwoWay.getBoardData(
                    CacheHubData.selectedMacID,
                    activityViewModel.selectedButtonList.value!![0].thing_id
                )
                if (device != null) {
                    activityViewModel.irbName.value = device.thing_name
                    activityViewModel.irbSelected.value = device
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : FragmentSceneRemoteIntegrationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_remote_integration, container, false)

        activityViewModel.irbSelected.observe(viewLifecycleOwner, Observer {
            if (::dialog.isInitialized) {
                dialog.dismiss()
            }
            if (it != null) {
                activityViewModel.getRemote(CacheHubData.selectedMacID, it.thing_serial_number)
                activityViewModel.irbName.value = it.thing_name
            }
        })

        val selectedBtnAdapter = SceneSelectedRemoteBtn(requireContext(), activityViewModel.selectedButtonList)
        binding.recyclerViewRemoteScene.adapter = selectedBtnAdapter

        activityViewModel.remoteList.observe(viewLifecycleOwner, Observer {
            if (it != null && it.size > 0) {
                activityViewModel.selectedRemote.value = it [0]
            }
        })

        activityViewModel.selectedRemote.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (::dialog.isInitialized) dialog.dismiss()
                activityViewModel.remoteName.value = it.remote_name
                binding.imageView128.setImageResource(getResourceRemoteIcon(it.ir_category))
                binding.spinner2.adapter = SelectRemoteButtonAdapter(requireContext(), it.ir_data.keys.map { it.drop(4).replace('_',' ') } , "TV")

            }
        })

        activityViewModel.selectedButtonList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                activityViewModel.checkEditOption()
            }
        })

//        binding.spinner2.adapter = SelectRemoteButtonAdapter(requireContext(), ArrayList<String>().apply {
//            add("1asddf")
//            add("2asddf")
//            add("3asddf")
//            add("4asddf")
//            add("5asddf")
//            add("6asddf")
//            add("7asddf")
//            add("8asddf")
//
//        } as List<String>, "TV")

//        binding.spinner2.adapter = SelectRemoteButtonAdapter(requireContext(), TVRemoteInfo.values().map { it.name.replace('_',' ') }, "TV")

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val b = p1?.select_list_name?.text.toString()
                activityViewModel.selectedButton.value = b
            }

        }


        activityViewModel.actionTrigger.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (activityViewModel.action) {

                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "selectIRB" -> {
                        dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
                        dialog.setContentView(R.layout._select_area)
                        dialog.select_item_dialog_title.text = "Select IRB Device"
                        dialog.show()
                        dialog.list_area_select.adapter = SelectIrbAdapter (requireContext(), activityViewModel.irbList ?: ArrayList(), activityViewModel.irbSelected)


                        dialog.setCanceledOnTouchOutside(true)
                        dialog.select_area_root.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.select_item_or_text.visibility = View.GONE
                        dialog.goto_create_area.visibility = View.GONE
                    }

                    "Toast" -> {
                        Toast.makeText(requireContext(), activityViewModel.toastText, Toast.LENGTH_LONG).show()
                        activityViewModel.toastText = ""
                    }

                    "selectRemote" ->{

                        dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
                        dialog.setContentView(R.layout._select_area)
                        dialog.select_item_dialog_title.text = "Select Remote"
                        dialog.show()
                        dialog.list_area_select.adapter = SelectRemoteAdapter (requireContext(), activityViewModel.remoteList.value ?: ArrayList(), activityViewModel.selectedRemote)


                        dialog.setCanceledOnTouchOutside(true)
                        dialog.select_area_root.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.select_item_or_text.visibility = View.GONE
                        dialog.goto_create_area.visibility = View.GONE
                    }

                    "Continue" -> {
                    }

                    "Refresh" -> {
                        AppServices.fragHideSoftKeyBoard(requireContext(), requireView())
                        selectedBtnAdapter.notifyDataSetChanged()
                    }

                    "Exit" -> {
                        AppDialogs.stopLoadScreen()
                        requireActivity().finish()
                    }

                    "loadOn" -> {
                        AppDialogs.startLoadScreen(requireContext())
                    }

                    "loadOff" -> {
                        AppDialogs.stopLoadScreen()
                    }

                    else -> {

                    }
                }
            }
        })


        binding.viewModel = activityViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

}