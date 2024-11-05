package com.embehome.embehome.irb.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.embehome.embehome.R
import com.embehome.embehome.databinding.FragmentRemoteAudioSystemBinding
import com.embehome.embehome.irb.CacheRemoteData
import com.embehome.embehome.irb.RemoteCategoryEnum
import com.embehome.embehome.irb.viewmodel.RemoteAudioSystemViewModel

/**
 * A simple [Fragment] subclass.
 */
class RemoteAudioSystem : Fragment() {

    private val viewModel : RemoteAudioSystemViewModel by viewModels ()
    private val dataArgs : RemoteAudioSystemArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.thingsID = dataArgs.thingsID
        viewModel.macID = dataArgs.macID
        viewModel.sno = dataArgs.serialNumber
        viewModel.remoteName.value = dataArgs.remoteName.toUpperCase()
        viewModel.operationType = dataArgs.operationType
        viewModel.setEditMode(dataArgs.operationType)
        viewModel.remoteID = dataArgs.remoteID
        if (dataArgs.operationType == "Operate") {
            viewModel.remoteIRData = CacheRemoteData.getRemote(viewModel.sno, dataArgs.remoteID)?.ir_data ?: HashMap()
        }
        Log.d("TronX _ TV", "${dataArgs.macID} ${dataArgs.remoteName} ${dataArgs.thingsID} ${dataArgs.serialNumber} ${dataArgs.operationType}")
    }

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentRemoteAudioSystemBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_remote_audio_system, container, false)

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "Save" -> {
                        val id = CacheRemoteData.generateRemoteID(viewModel.sno)
                        val res = viewModel.saveRemote(requireContext(), viewModel.macID, viewModel.sno, viewModel.thingsID, viewModel.remoteName.value.toString(), id, RemoteCategoryEnum.SPEAKERS.name, viewModel.remoteIRData)
                    }

                    "Exit" -> {
                        requireActivity().finish()
                    }

                    "ClickButton" -> {
                        if (viewModel.button != null) {
                            viewModel.action(
                                requireContext(),
                                viewModel.button?.name.toString(),
                                viewModel.remoteIRData[viewModel.button!!.name].toString(), viewModel.operationType, viewModel.thingsID)

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

}
