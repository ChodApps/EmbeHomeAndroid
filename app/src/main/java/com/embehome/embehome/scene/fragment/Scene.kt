package com.embehome.embehome.scene.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.SceneFragmentBinding
import com.embehome.embehome.scene.adapter.SceneListItemDisplayAdapter
import com.embehome.embehome.scene.viewModel.SceneViewModel

class Scene : Fragment(), PopupMenu.OnMenuItemClickListener {


    val viewModel: SceneViewModel by viewModels ()
    val sceneArgs : SceneArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sceneArgs.scenID == -1)
            findNavController().navigateUp()
        viewModel.getScene(sceneArgs.scenID)
    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : SceneFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.scene_fragment, container, false)
        val adapter = SceneListItemDisplayAdapter (requireContext(), viewModel.scene)
        binding.sceneListDisplay.adapter = adapter
        viewModel.scene.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val scene = CacheSceneTwoWay.getSceneItem(it.scenename_id)
                viewModel.sceneName.value = scene.scene_name
                if (scene.image != null) {
                    binding.imageView29.setImageBitmap(scene.image)
                }
                adapter.notifyDataSetChanged()
            }
        })

        binding.imageView214.setOnClickListener {
            PopupMenu(requireActivity(), it).apply {
                setOnMenuItemClickListener(this@Scene)
                inflate(R.menu.remote_options)
                show()
            }
        }


        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "Edit" -> {
                        if (CacheHubData.selectedHubIP.length > 5) {
                            viewModel.newSceneId.value = CacheSceneTwoWay.generateSceneID(CacheHubData.selectedMacID)
                            val d = SceneDirections.actionSceneToSceneConfiguration(sceneArgs.scenID)
                            findNavController().navigate(d)/*also {
                                requireActivity().onBackPressed()
                            }*/
                        }
                        else {
                            Toast.makeText(requireContext(), CacheHubData.homeNetwork, Toast.LENGTH_SHORT).show()
                        }
                    }
                    "DeleteScene" -> {
                        if (CacheHubData.selectedHubIP.length > 5) {
                            viewModel.deleteScene(requireContext())
                        }
                        else {
                            Toast.makeText(requireContext(), CacheHubData.homeNetwork, Toast.LENGTH_SHORT).show()
                        }

                    }

                    else -> {

                    }
                }
            }
        })

        viewModel.newSceneId.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                try {
                    viewModel.updateScene(it)
                }
                catch (e : Exception) {
                    AppServices.log("TronX _ SceneDetails", e.message.toString())
                }
            }
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return  binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.newSceneId.also {
            if (it.value != null) {
                it.value = it.value
            }
        }
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        when (p0?.itemId) {
            R.id.irb_menu_delete -> {
                viewModel.performAction("DeleteScene")
            }
            R.id.irb_menu_edit -> {
                viewModel.performAction("Edit")
            }

        }


        return true
    }

}