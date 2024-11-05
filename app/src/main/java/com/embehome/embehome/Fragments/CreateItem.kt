package com.embehome.embehome.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.databinding.FragmentCreateAreaBinding
import com.embehome.embehome.viewModel.CreateAreaViewModel


/** com.tronx.things.Fragments
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 11-08-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class CreateItem (val type : String, val action : String, val areaId : Int = -1) : Fragment() {

    val viewModel: CreateAreaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (areaId > 0) CacheHubData.getArea(areaId).also {
            if (it.area_id == areaId) {
                viewModel.areaName.value = it.area_name
                it.image?.let { img ->
                    viewModel.areaImage.value = img
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : FragmentCreateAreaBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_area, container, false)

        binding.textView226.text = "$action $type"

        viewModel.createAreaBack.observe(viewLifecycleOwner, Observer {
            if (it) {
                requireActivity().finish()
            }
        })

        viewModel.areaImage.observe(viewLifecycleOwner, Observer {
            if (it != null){
                binding.imageView265.setImageBitmap(it)
            }
        })

        viewModel.importImage.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.readImage(requireContext(), requireActivity(), this)
            }
        })

        viewModel.saveArea.observe(viewLifecycleOwner, Observer {
            if (it && viewModel.validateImage(requireContext())) {

                if (areaId > 0) {
                    viewModel.updateArea(requireContext(), areaId)
                }
                else {
                    viewModel.createArea(requireContext())
                }
            }
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            1-> {
                if (resultCode == -1) viewModel.processCameraImage()
            }

            2 -> {
                if (resultCode == -1) viewModel.processGalleryImage(data, requireContext(), requireActivity())
            }

        }
    }


}