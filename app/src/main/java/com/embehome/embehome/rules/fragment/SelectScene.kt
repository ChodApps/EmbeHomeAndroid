package com.embehome.embehome.rules.fragment

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
import com.embehome.embehome.databinding.SelectSceneFragmentBinding
import com.embehome.embehome.rules.adapter.SubSceneAdapter
import com.embehome.embehome.rules.viewModels.RuleTypeSelectionViewModel
import com.embehome.embehome.rules.viewModels.SelectSceneViewModel

class SelectScene : Fragment() {

    private val tempViewModel: SelectSceneViewModel by viewModels()
    private val viewModel : RuleTypeSelectionViewModel by activityViewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.ruleAction.value?.let {
            it.scenes_list.forEach {
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
        val scene = CacheSceneTwoWay.getSceneData(CacheHubData.selectedMacID).also {
            it?.value?.let {
                if (it.isEmpty()) {
                    Toast.makeText(requireContext(), "Scene's not available", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }

        binding.recycleViewSubScene.adapter = SubSceneAdapter(requireContext(), scene?.value ?: ArrayList(), tempViewModel.selectedSceneID)

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                when (viewModel.action) {
                    "Back" -> {
                        findNavController().navigateUp()

                    }

                    "actionSubSceneSave" -> {
                       tempViewModel.selectedSceneID.value?.let { sceneId ->
                           if (sceneId.size > 0) {
                               var l = ""
                               scene?.value?.let {s ->

                                   sceneId.forEach { id ->
                                       s.forEach {
                                           if (id == it.scene_id) {
                                               CacheSceneTwoWay.getSceneItem(it.scenename_id).also {
                                                   if (it.scene_name.length > 2) {
                                                       l = "$l ${it.scene_name},"
                                                   }
                                               }
                                           }
                                       }
                                   }

                               }


                               viewModel.actionSubSceneSave(sceneId, l.dropLast(1))
                               findNavController().navigateUp()
                           }
                       }
                    }

                }
            }
        })


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }
}