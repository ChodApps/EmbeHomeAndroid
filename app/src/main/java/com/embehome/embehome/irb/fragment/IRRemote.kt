package com.embehome.embehome.irb.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import com.embehome.embehome.databinding.*
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.irb.RemoteCategoryEnum
import com.embehome.embehome.irb.viewmodel.RemoteCmnViewModel


/** com.tronx.tt_things_app.irb.fragment
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 25-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

class IRRemote : Fragment(), PopupMenu.OnMenuItemClickListener {

    val viewModel : RemoteCmnViewModel by activityViewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.remoteCategory.value == null) {
            viewModel.back()
        }
    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = when (viewModel.remoteCategory.value) {
            RemoteCategoryEnum.TV -> {
                (DataBindingUtil.inflate(inflater, R.layout.fragment_remote_t_v, container, false) as FragmentRemoteTVBinding).also {
                    it.viewModel =  viewModel
                    it.imageView211.setOnClickListener{v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }

                    }
                }
            }
            RemoteCategoryEnum.DISH -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_setup_box_fragment, container, false) as RemoteSetupBoxFragmentBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            RemoteCategoryEnum.DVD -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_d_v_d_player_fragment, container, false) as RemoteDVDPlayerFragmentBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }

                }
            }
            RemoteCategoryEnum.SPEAKERS -> {
                (DataBindingUtil.inflate(inflater, R.layout.fragment_remote_audio_system, container, false) as FragmentRemoteAudioSystemBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            RemoteCategoryEnum.AC -> {
                (DataBindingUtil.inflate(inflater, R.layout.fragment_remote_a_c, container, false) as FragmentRemoteACBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                    viewModel.fanLevel.observe(viewLifecycleOwner, Observer {level ->
                        viewModel.setFanImage(requireContext(), it.imageView272, level)
                    })
                }
            }
            RemoteCategoryEnum.TABLE_FAN -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_table_fan_fragment, container, false) as RemoteTableFanFragmentBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            RemoteCategoryEnum.AIR_PURIFIER -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_air_purifier_fragment, container, false) as RemoteAirPurifierFragmentBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            RemoteCategoryEnum.COOLER -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_cooler_fragment, container, false) as RemoteCoolerFragmentBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            RemoteCategoryEnum.FAN -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_ceiling_fan_fragment, container, false) as RemoteCeilingFanFragmentBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            RemoteCategoryEnum.GEYSER -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_geyser_fragment, container, false) as RemoteGeyserFragmentBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            RemoteCategoryEnum.OVEN -> {
                (DataBindingUtil.inflate(inflater, R.layout.remote_oven_fragment, container, false) as RemoteOvenFragmentBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            RemoteCategoryEnum.PROJECTOR -> {
                (DataBindingUtil.inflate(inflater, R.layout.fragment_remote_projector, container, false) as FragmentRemoteProjectorBinding).also {
                    it.viewModel =  viewModel
                    it.imageView213.setOnClickListener { v ->
                        val menu = PopupMenu(requireActivity(), v).apply {
                            setOnMenuItemClickListener(this@IRRemote)
                            inflate(R.menu.remote_options)
                            show()
                        }
                    }
                }
            }
            else -> {
                viewModel.back()
                ( DataBindingUtil.inflate(inflater, R.layout.fragment_remote_t_v, container, false) as FragmentRemoteTVBinding).also {
                    it.viewModel =  viewModel
                }
            }
        }


        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "Save" -> {
                        val id = CacheRemoteData.generateRemoteID(viewModel.irb.value!!.thing_serial_number)
                        viewModel.saveRemote(requireContext(), viewModel.macID, viewModel.irb.value!!, viewModel.remoteName.value.toString(), id, viewModel.remoteCategory.value!!.name, viewModel.remoteIRData)
                    }

                    "Update" -> {
                        viewModel.saveRemote(requireContext(), viewModel.macID, viewModel.irb.value!!, viewModel.remoteName.value.toString(), viewModel.remoteID, viewModel.remoteCategory.value!!.name, viewModel.remoteIRData)
                    }

                    "Exit" -> {
                        requireActivity().finish()
                    }

                    "ClickButton" -> {
                        if (viewModel.button != null) {
                            viewModel.action(
                                requireContext(),
                                viewModel.button?.name.toString(),
                                viewModel.remoteIRData[viewModel.button!!.name] ?: "", viewModel.operationType, if (viewModel.rThingsID == -1 ) viewModel.irb.value!!.thing_id else viewModel.rThingsID)
                        }
                    }

                    else -> {
                        Toast.makeText(requireContext(),viewModel.action, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

//        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {

        when (p0?.itemId) {
            R.id.irb_menu_delete -> {
                val remoteName = viewModel.remoteName.value.toString()
                if (CacheHubData.selectedHubIP.length > 5) {
                    AppDialogs.openTitleDialog(
                        requireContext(),
                        "Delete Remote - $remoteName",
                        "Are you sure you want to delete $remoteName ?",
                        "No",
                        "yes"
                    ) { d, m ->
                        viewModel.delete(
                            requireContext(),
                            viewModel.macID,
                            viewModel.irb.value!!.thing_serial_number,
                            viewModel.remoteID,
                            remoteName
                        )
                    }
                }
                else {
                    AppServices.toastShort(requireContext(),CacheHubData.homeNetwork)
                }
//                viewModel.delete(requireContext(), viewModel.macID, viewModel.irb.value!!.thing_serial_number, viewModel.remoteID, viewModel.remoteName.value.toString())
            }
            R.id.irb_menu_edit -> {
                if (CacheHubData.selectedHubIP.length > 5 && viewModel.rThingsID == viewModel.irb.value?.thing_id) {
                    viewModel.editMode.value = View.VISIBLE
                    viewModel.operationType = "Edit"
                }
                else Toast.makeText(requireContext(),CacheHubData.homeNetwork, Toast.LENGTH_SHORT).show()
            }

        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.button = null
    }
}