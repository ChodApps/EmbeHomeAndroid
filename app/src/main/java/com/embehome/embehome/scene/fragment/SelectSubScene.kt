package com.embehome.embehome.scene.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Model.SceneCloudModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.SelectSceneFragmentBinding
import com.embehome.embehome.rules.adapter.SubSceneAdapter
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel
import com.embehome.embehome.rules.viewModels.SelectSceneViewModel
import com.embehome.embehome.viewModel.SceneDetailViewModel


/** com.tronx.tt_things_app.scene.fragment
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 25-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class SelectSubScene  : Fragment() {

    private val tempViewModel: SelectSceneViewModel by viewModels()
    private val v : RuleTypeSelectionViewModel by viewModels()
    private val viewModel : SceneDetailViewModel by activityViewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.subScene.value?.let {
            it.forEach {
                tempViewModel.selectedSceneID.value?.add(it)
            }
            /*if (it.size > 0) {
                tempViewModel.selectedSceneID.value?.addAll(it)
            }*/
        }
//        tempViewModel.selectedSceneID.value = viewModel.selectedSceneID.value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : SelectSceneFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.select_scene_fragment, container, false)
        val scene = CacheSceneTwoWay.getSceneData(CacheHubData.selectedMacID)
        val d = scene?.value?.filter {
            it.device_list.size == 0
        }


        binding.recycleViewSubScene.adapter = SubSceneAdapter(requireContext(), ArrayList(d ?: ArrayList<SceneCloudModel>()) ?: ArrayList(), tempViewModel.selectedSceneID)

       v.performAction.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                when (v.action) {
                    "Back" -> {
                        findNavController().navigateUp()

                    }

                    "actionSubSceneSave" -> {
                        tempViewModel.selectedSceneID.value?.let { sceneId ->
                            if (sceneId.size > 0) {
                                viewModel.subScene.value?.addAll(sceneId)
                                findNavController().navigateUp()
                            }
                        }
                    }

                }
            }
        })


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = v
        return binding.root
    }
}