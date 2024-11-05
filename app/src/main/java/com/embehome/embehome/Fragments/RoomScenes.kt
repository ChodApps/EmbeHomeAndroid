package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Adapter.SceneHomeAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Repository.HubFeatureHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.databinding.FragmentRoomScenesBinding
import com.embehome.embehome.scene.viewModel.RoomSceneViewModel

/**
 * A simple [Fragment] subclass.
 */
class RoomScenes : Fragment() {

    val viewModel : RoomSceneViewModel by viewModels ()
    lateinit var listAdapter : SceneHomeAdapter

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentRoomScenesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_room_scenes, container, false)

//        viewModel.hubIp.observe(viewLifecycleOwner, Observer {
//            if (it.length > 1) CacheHubData.selectedHubIP = it
//        })

        listAdapter =  SceneHomeAdapter(requireContext(), (viewModel.sceneData ?: MutableLiveData(ArrayList())).also {
            it.value?.size?.let {
                if (it > 0) {
                    binding.textView243.visibility = View.GONE
                }
                else {
                    binding.textView243.visibility = View.VISIBLE
                }
            }
        }, { view, position ->
//                Toast.makeText(requireContext(), "Playing Scene", Toast.LENGTH_SHORT).show()
                HubFeatureHandleRepository.playScene(requireContext(), CacheHubData.selectedHubIP, CacheHubData.selectedMacID,
                    viewModel.sceneData!!.value!![position].scene_id, viewModel.sceneData!!.value!![position].subscene_list)
            }, {sceneID ->
            val d = RoomScenesDirections.actionRoomScenesToScene(sceneID)
            findNavController().navigate(d)
        })
        binding.roomSceneHomeList.adapter = listAdapter

        viewModel.sceneData?.observe(viewLifecycleOwner, Observer {
            listAdapter.notifyDataSetChanged()
            if (it.size > 0) {
                binding.textView243.visibility = View.GONE
            }
            else {
                binding.textView243.visibility = View.VISIBLE
            }
        })
//        sceneData  = CacheSceneTwoWay.getSceneData(CacheHubData.selectedMacID)

//        sceneData?.observe(viewLifecycleOwner, Observer {
//            v.room_scene_home_list.adapter = SceneHomeAdapter(requireContext(), sceneData!!.value
//                    ?: ArrayList()){ view, position ->
//                Toast.makeText(requireContext(), "Playing Scene", Toast.LENGTH_SHORT).show()
//                HubFeatureHandleRepository.playScene(CacheHubData.selectedHubIP, CacheHubData.selectedMacID,
//                    sceneData!!.value!![position].scene_id)
//            }
//        })
//
//        v.room_scene_home_list.adapter = SceneHomeAdapter(requireContext(), sceneData?.value ?: ArrayList()){ view, position ->
//            Toast.makeText(requireContext(), "Playing Scene", Toast.LENGTH_SHORT).show()
//            if (sceneData != null) {
//                HubFeatureHandleRepository.deleteScene(requireActivity(), CacheHubData.selectedMacID, sceneData!!.value?.get(position)!!.scene_id)
//            }
//
//        }
//
//        v.room_scene_add.setOnClickListener {
//            if (CacheHubData.selectedHubIP.length > 5)
//            requireActivity().startActivity(Intent(requireActivity(),SceneConfiguration::class.java))
//            else
//                Toast.makeText(requireContext(), "Operation work in HOME network only", Toast.LENGTH_SHORT).show()
//        }
//        v.room_scene_home.setOnClickListener {
//            requireActivity().finish()
//        }

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "Back" -> {
                        requireActivity().finish()
                    }

                    "Add" -> {
                        viewModel.sceneData?.value?.let {
                            if (it.size < 20) {
                                if (CacheHubData.selectedHubIP.length > 5) {
                                    val d = RoomScenesDirections.actionRoomScenesToSceneConfiguration(-1)
                                    findNavController().navigate(d)
                                }
                                else
                                    AppServices.toastShort(requireContext(),CacheHubData.homeNetwork)
                            }
                            else {
                                AppDialogs.startMsgLoadScreen(requireContext(), "You can add only 20 scenes.")
                            }
                        }
                        /*if (CacheHubData.selectedHubIP.length > 5) {
                            val d = RoomScenesDirections.actionRoomScenesToSceneConfiguration(-1)
                            findNavController().navigate(d)
                        }
                        else
                            Toast.makeText(requireContext(), "Operation work in HOME network only", Toast.LENGTH_SHORT).show()*/
                    }

                    else -> {

                    }
                }



            }
            viewModel.action = ""
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        listAdapter.notifyDataSetChanged()
    }
}
