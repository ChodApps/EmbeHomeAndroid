package com.embehome.embehome.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.Adapter.selectSceneItemAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentSceneDetailBinding
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.scene.viewModel.EditSceneViewModel
import com.embehome.embehome.viewModel.SceneDetailViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class SceneDetail : Fragment() {

    val viewModel : SceneDetailViewModel by activityViewModels()
    val activityViewModel : EditSceneViewModel by activityViewModels ()
    val args : SceneDetailArgs by navArgs()
    lateinit var sceneItemAdapter : selectSceneItemAdapter

    lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.sceneId > 0) {
            val scene = CacheSceneTwoWay.getScene(CacheHubData.selectedMacID, args.sceneId)
            if (scene != null) {
                viewModel.updateView(viewModel.option)
                activityViewModel.sceneEdit = true
                viewModel.submitButtonText.value = "Update Switches"
                viewModel.submitRemoteText.value = "Update Remote"
                activityViewModel.deviceSelected.value = scene.device_list
                activityViewModel.oldSceneID.value = scene.scene_id
                val sceneItem = CacheSceneTwoWay.getSceneItem(scene.scenename_id)
                viewModel.sceneName.value = sceneItem.scene_name
                viewModel.sceneItemId = sceneItem.scenename_id
                viewModel.subScene.value = scene.subscene_list
                activityViewModel.sceneNameId = sceneItem.scenename_id
                if (scene.ir_data.size > 0) {
                    activityViewModel.selectedButtonList.value = scene.ir_data
                    viewModel.updateView(viewModel.remote)
                }
                else if (scene.device_list.size > 0) {
                    viewModel.updateView(viewModel.device)
                }
                else {
                    viewModel.updateView(viewModel.create)
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentSceneDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_detail, container, false)
        val sceneItemData = CacheSceneTwoWay.getSceneItemList() ?: ArrayList()
        if (args.sceneId < 0) {
            if (viewModel.sceneItemId == -1) {
                viewModel.updateView(viewModel.image)
                /*viewModel.sceneName.value = sceneItemData[0].scene_name
                CacheSceneTwoWay.sceneNameID = sceneItemData[0].scenename_id
                viewModel.sceneItemId = sceneItemData[0].scenename_id*/
            }
            else viewModel.updateView(viewModel.option)
        }

        sceneItemAdapter = selectSceneItemAdapter (requireContext(), sceneItemData){scene ->
            val s = CacheSceneTwoWay.getSceneData(CacheHubData.selectedMacID)
            val r = s?.value?.filter {
                it.scenename_id == scene.scenename_id
            }
            if (r == null || r.isEmpty()) {
                CacheSceneTwoWay.sceneNameID = scene.scenename_id
                viewModel.sceneName.value = scene.scene_name
                viewModel.sceneItemId = scene.scenename_id
                if (scene.image != null) binding.imageView29.setImageBitmap(scene.image)
                viewModel.updateView(viewModel.option)
            }
            else AppServices.toastShort(requireContext(), "You have used this name already for another scene please select different name.")
        }
        binding.sceneItemRecycleView.adapter = sceneItemAdapter

        val scene = CacheSceneTwoWay.getSceneItem(viewModel.sceneItemId)
        if (scene.image != null) {
            binding.imageView29.setImageBitmap(scene.image)
        }
        else {
            GlobalScope.launch (Main) {
                val res = AppServices.imageSave(requireContext(), scene.scene_image)
                if (res != null && scene.image == null) {
                    scene.image = res
                    binding.imageView29.setImageBitmap(scene.image)
                }
            }
        }



        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "Add" -> {
                        if (viewModel.sceneItemId > 0 && CacheHubData.selectedHubIP.length > 5) {
                                val d = SceneDetailDirections.actionSceneDetailToSceneSelectBoards(viewModel.sceneItemId, args.sceneId)
                                findNavController().navigate(d)
                        }

                    }

                    "Back" -> {
                        requireActivity().finish()
                    }

                    "CreateScene" -> {
                        val d = SceneDetailDirections.actionSceneDetailToCreateArea3("Scene", "Create")
                        findNavController().navigate(d)
                    }

                    "IRB" -> {
                        GlobalScope.launch (Main) {
                            AppDialogs.startLoadScreen(requireContext())
                            CacheRemoteData.getIRBForScene(CacheHubData.selectedMacID).also {irbList ->
                                AppDialogs.stopLoadScreen()
                                if (activityViewModel.sceneEdit && activityViewModel.selectedButtonList.value?.size ?: 0 > 0) {
                                    activityViewModel.sceneNameId = viewModel.sceneItemId
                                    activityViewModel.subScene.value = viewModel.subScene.value
                                    findNavController().navigate(R.id.action_sceneDetail_to_sceneRemoteIntegration)
                                }
                                else {
                                    if (irbList.size > 0) {
                                        activityViewModel.irbList = irbList
                                        activityViewModel.irbSelected.value = irbList[0]
                                        activityViewModel.remoteList.value = ArrayList(CacheRemoteData.getRemoteForScene(CacheHubData.selectedMacID, irbList[0].thing_serial_number))
                                        activityViewModel.sceneNameId = viewModel.sceneItemId
                                        activityViewModel.selectedButtonList.value = ArrayList()
                                        findNavController().navigate(R.id.action_sceneDetail_to_sceneRemoteIntegration)
                                    } else {
                                        AppServices.toastShort(
                                            requireContext(),
                                            "Remotes Not found"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "SelectSceneItem" -> {
                       /* dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
                        dialog.setContentView(R.layout._select_area)
                        dialog.select_item_dialog_title.text = "Select Scene Name"
                        dialog.goto_create_area.text = "Create Scene Name"
                        dialog.show()
                        dialog.goto_create_area.visibility = View.GONE
                        dialog.select_item_or_text.visibility = View.GONE
                        dialog.list_area_select.adapter = selectSceneItemAdapter (requireContext(), sceneItemData){scene ->
                            CacheSceneTwoWay.sceneNameID = scene.scenename_id
                            viewModel.sceneName.value = scene.scene_name
                            viewModel.sceneItemId = scene.scenename_id
                            if (scene.image != null)binding.imageView29.setImageBitmap(scene.image)
                            dialog.dismiss()
                        }
                        dialog.setCanceledOnTouchOutside(true)
                        dialog.select_area_parent.setOnClickListener{

                        }
                        dialog.select_area_root.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.goto_create_area.setOnClickListener {
                            dialog.dismiss()
                            //  findNavController().navigate(AddBoardDirections.actionAddBoardToCreateArea())
                        }*/
                    }

                    "Scene" -> {
                        findNavController().navigate(R.id.action_sceneDetail_to_selectSubScene)
                    }

                    else -> {

                    }
                }
                viewModel.action = ""
            }
        })


        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(::sceneItemAdapter.isInitialized) sceneItemAdapter.notifyDataSetChanged()
    }

}
