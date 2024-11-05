package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Adapter.SelectHubImageAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.databinding.FragmentHubAddBinding
import com.embehome.embehome.getHubImage
import com.embehome.embehome.viewModel.AddHubViewModel


/** com.tronx.tt_things_app.Fragments
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 07-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class UpdateHubData : Fragment() {

    val viewModel : AddHubViewModel by viewModels()
    val hub = CacheHubData.getHub(CacheHubData.selectedMacID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hub == null)
            findNavController().navigateUp()

       hub?.let {
           viewModel.hubName.value = hub.name
           viewModel.imageRef.value = hub.image
           viewModel.hubMessage.value = ""
           viewModel.swapView.value = true
           viewModel.hubTitle.value = "UPDATE HUB"
           viewModel.actionBtn.value = "Update"
       }
    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentHubAddBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_hub_add, container, false)


        binding.addHubImageRecycler.adapter = SelectHubImageAdapter(requireContext(), viewModel.imageRef)

        viewModel.hubName.observe(viewLifecycleOwner, Observer {

        })

        viewModel.addHubBack.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigateUp()
            }
        })

        viewModel.imageRef.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.swapView.value = true
                binding.hubImage.visibility = View.VISIBLE
                binding.hubImage.setImageResource(getHubImage(it))
            }
        })

        binding.hubImage

        viewModel.saveHub.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.addHub(AppServices.wifiSSID(requireContext()))
            }

        })

        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(requireContext(), viewModel.toastString.value, Toast.LENGTH_SHORT).show()
            }
        })



        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "AddDevice" -> {
                        hub?.let {
                            if (viewModel.hubName.value.isNullOrEmpty()) {
                                AppServices.toastShort(requireContext(),"Please enter hub name.")
                            }
                            else
                            viewModel.updateHubData(requireContext(),  it.macID,  viewModel.hubName.value ?: "Hub 1",viewModel.imageRef.value ?: "",
                                it.version, "")
                        }
                    }
                }
                viewModel.action = ""
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigateUp()
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }


}