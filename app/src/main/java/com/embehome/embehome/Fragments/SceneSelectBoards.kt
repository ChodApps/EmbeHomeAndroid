package com.embehome.embehome.Fragments

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
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentSceneSelectBoardsBinding
import com.embehome.embehome.scene.adapter.SceneExpandableListAdapter
import com.embehome.embehome.scene.viewModel.EditSceneViewModel
import com.embehome.embehome.viewModel.SceneSelectBoardsViewModel

/**
 * A simple [Fragment] subclass.
 */
class SceneSelectBoards : Fragment() {

    val viewModel : SceneSelectBoardsViewModel by viewModels()
    val activityViewModel : EditSceneViewModel by activityViewModels ()
    val args : SceneSelectBoardsArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scene = CacheSceneTwoWay.getScene(CacheHubData.selectedMacID, args.sceneId)
        if (scene != null) {
            scene.device_list.forEach {
                viewModel.selectedBoard.value?.contains(it.thing_id).apply {
                    if (this == false) {
                        viewModel.selectedBoard.value?.add(it.thing_id)
                    }
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val binding : FragmentSceneSelectBoardsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_select_boards, container, false)
        val data = CacheHubData.getHubData(CacheHubData.selectedMacID)
        binding.expandableBoardList.adapter =
            SceneExpandableListAdapter(
                requireContext(),
                viewModel.expanded,
                data,
                viewModel.selectedBoard
            ) {

            }


        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {

                    "Next" -> {
                        if (viewModel.selectedBoard.value?.size!! > 0 && args.sceneItemName > 0) {
                            val d =
                                SceneSelectBoardsDirections.actionSceneSelectBoardsToSceneSelectSwitchs(
                                    macID = CacheHubData.selectedMacID,
                                    thingsIDs = viewModel.selectedBoard.value!!.toIntArray(),
                                    sceneItem = args.sceneItemName,
                                    sceneId = args.sceneId
                                )
                            findNavController().navigate(d)
                        }
                        else
                            showToast("Please select at least ONE SSB ")
                    }

                    "Back" -> {
                        findNavController().navigateUp()
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

    fun showToast (msg : String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}
