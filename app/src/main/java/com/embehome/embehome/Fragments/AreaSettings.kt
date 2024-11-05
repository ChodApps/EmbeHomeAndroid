package com.embehome.embehome.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.embehome.embehome.Adapter.SelectAreaAdapter
import com.embehome.embehome.Interface.FragmentHandlerInterface
import com.embehome.embehome.Manager.SSBManager
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.AreaSettingsFragmentBinding
import com.embehome.embehome.viewModel.AreaSettingsViewModel
import kotlinx.android.synthetic.main._select_area.*

class AreaSettings (val macID : String, val thingsID : Int) : Fragment() {

    val viewModel : AreaSettingsViewModel by viewModels ()
    lateinit var dialog : Dialog
    lateinit var adapter: SelectAreaAdapter
    lateinit var listener : FragmentHandlerInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as FragmentHandlerInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (macID.length > 1 && thingsID > 0) {
            viewModel.board.value = CacheSceneTwoWay.getBoardData(macID, thingsID)?.also {
                val hubVersion = CacheHubData.getHub(CacheHubData.selectedMacID)?.version ?: ""
                if ((it.thing_type == "SSB" || it.thing_type == "ISB") && (it.switchesList.size > 4) && it.thing_version ?: "" > "05.00.04" && hubVersion != "5.0.2")
                    viewModel.isTouchEnabled.value = true
            }
        }
        else {
            viewModel.back()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : AreaSettingsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.area_settings_fragment, container, false)
        adapter = SelectAreaAdapter(requireContext(), viewModel.area)

        viewModel.board.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.area.value = CacheHubData.getArea(it.area_id)
                viewModel.boardName.value = it.thing_name
                binding.imageView124.setImageResource(SSBManager.getSSBImage(it.switchesList.size, true, it.thing_type))
                it.thing_version?.let { v ->
                    if (v.isNotEmpty()) {
                        binding.deviceVersion.text = "Device version : $v "
                    }
                }
            }
        })

        viewModel.area.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.areaName.value = it.area_name
                if (::dialog.isInitialized)   if (dialog.isShowing) dialog.dismiss()
            }
        })


        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "Back" -> {
                        requireActivity().onBackPressed()
                    }

                    "Edit" -> {
                        viewModel.deviceEdit.value = true
                    }

                    "Finish" -> {
                        requireActivity().finish()
                    }

                    "Save" -> {
                        requireView().clearFocus()
                        AppServices.fragHideSoftKeyBoard(requireContext(), requireView())
                        viewModel.deviceEdit.value = false
                        viewModel.board.value?.let {

                            try {
                                viewModel.area.value?.area_id?.let { it1 ->
                                    viewModel.boardName.value?.let { name ->
                                        if (name.isNotEmpty())
                                            viewModel.updateBoardData(
                                                requireContext(), macID,
                                                BoardDetails(
                                                    it.thing_serial_number,
                                                    it.thing_type,
                                                    it.category,
                                                    it1,
                                                    name,
                                                    it.thing_image,
                                                    it.thing_id,
                                                    it.switchesList,
                                                    it.thing_version,
                                                    it.touch_sensitivity
                                                ),
                                                it.area_id
                                            )
                                        else AppServices.toastShort(
                                            requireContext(),
                                            "Please enter valid device name."
                                        )
                                    }
                                }
                            }
                            catch (e : Exception) {
                                AppServices.log("TronX _ AreaSettings",e.message.toString())
                            }
                        }
                    }

                    "DisplayID" -> {
                        viewModel.board.value?.let {

                            AppDialogs.openMessageDialog(requireContext(), "Device ID - ${it.thing_id}")
                        }
                    }

                    "ChangeArea" -> {
                        dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)

                        dialog.setContentView(R.layout._select_area)
                        dialog.show()
                        dialog.list_area_select.visibility = View.GONE
                        dialog.grid_area_select.visibility = View.VISIBLE
                        dialog.grid_area_select.adapter = adapter
                        dialog.setCanceledOnTouchOutside(true)
                        dialog.goto_create_area.visibility = View.GONE
                        dialog.select_item_or_text.visibility = View.GONE
                        dialog.select_area_parent.setOnClickListener{

                        }
                        dialog.select_area_root.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.goto_create_area.setOnClickListener {
                            dialog.dismiss()
                        }
                    }

                    "GoToTouchSensitivity" -> {
//                        childFragmentManager.beginTransaction().add(R.id.container, DeviceTouchSensitivity()).addToBackStack("AreaSettings").commit()
                        try {
                            listener.gotoTouchSensitivity(viewModel.board.value!!)
                        }
                        catch (e : Exception) {
                            AppServices.log("TronX", e.message.toString())
                            AppServices.toastShort(requireContext(), "Error loading the details")
                        }
                    }
                }
            }
        })

        binding.context = requireContext()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized) adapter.notifyDataSetChanged()
    }

}