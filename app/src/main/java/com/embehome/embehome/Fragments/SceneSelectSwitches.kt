package com.embehome.embehome.Fragments

import android.os.Bundle
import android.util.Log
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
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentSceneSelectSwitchsBinding
import com.embehome.embehome.scene.adapter.SceneBoardExpandListAdapter
import com.embehome.embehome.viewModel.SceneDetailViewModel
import com.embehome.embehome.viewModel.SceneSelectSwitchsViewModel
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class SceneSelectSwitches : Fragment() {

    private val viewModel : SceneSelectSwitchsViewModel by viewModels()
    private val activityViewModel : SceneDetailViewModel by activityViewModels ()
    private val args : SceneSelectSwitchesArgs by navArgs()
    private val twoWay : ArrayList<Int> = ArrayList()

    private fun getBoardDataFromCache (): ArrayList<BoardDetails> {
        return CacheSceneTwoWay.getBoardList(
                CacheHubData.selectedMacID,
                CacheSceneTwoWay.sceneCreateSSBData
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scene = CacheSceneTwoWay.getScene(CacheHubData.selectedMacID, args.sceneId)
        if (scene != null) {
            args.thingsIDs.forEach {thingsId ->
                scene.device_list.filter {
                    it.thing_id == thingsId
                }.forEach{ it ->
                    viewModel.selectedSwitch.value?.add(it)
                    CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, it.thing_id)?.let {b ->
                        b.switchesList.filter { it.switchId == it.switchId }.also { res ->
                            if (res.isNotEmpty()) {
                                res[0].twoway_id?.let {
                                    try {
                                        twoWay.add(it.toInt())
                                    }catch (e : Exception) {

                                    }
                                }
                            }
                        }
                    }
                }
            }
            viewModel.submitButtonText.value = "Update Scene"
        }

    }

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentSceneSelectSwitchsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_select_switchs, container, false)

        val macID = args.macID
        val boardIDs = args.thingsIDs
        val board = ArrayList <BoardDetails> ()
        boardIDs.forEach {
            val b = CacheSceneTwoWay.getBoardData(macID, it)
            if (b != null) board.add(b)
        }
        Log.d("TronX _ Scene","${args.macID} ${args.thingsIDs.size}")

        //val data = getBoardDataFromCache()
        binding.sceneSelectSwitchList.adapter =
            SceneBoardExpandListAdapter(
                requireContext(),
                viewModel.expanded,
                board,
                viewModel.selectedSwitch,
                twoWay
            ) {

            }

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {

                when(viewModel.action) {

                    "Save" -> {
                        if (viewModel.selectedSwitch.value?.size ?: 0 >= 1 && args.sceneItem > 0) {
                            if (args.sceneId > 0) {
                                viewModel.updateScene(
                                    requireActivity(),
                                    macID,
                                    CacheHubData.selectedHubIP,
                                    args.sceneItem,
                                    args.sceneId,
                                    viewModel.selectedSwitch.value!!,
                                     ArrayList(),
                                    activityViewModel.subScene.value ?: ArrayList()
                                )
                            }
                            else
                                viewModel.saveScene(requireActivity(), macID, CacheHubData.selectedHubIP, args.sceneItem, viewModel.selectedSwitch.value!!, activityViewModel.subScene.value ?: ArrayList())
                        }
                        else {
                            showToast("Please Select Two or More Switches to Save the scene")
                        }
                    }

                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "IRB" -> {

                    }


                    else -> {
                        showToast(viewModel.action)
                    }
                }

            }
        })


        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    fun showToast (msg : String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

}
