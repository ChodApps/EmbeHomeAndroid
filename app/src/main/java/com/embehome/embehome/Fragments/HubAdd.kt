package com.embehome.embehome.Fragments


import android.os.Bundle
import android.util.Log
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
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.databinding.FragmentHubAddBinding
import com.embehome.embehome.getHubImage
import com.embehome.embehome.viewModel.AddHubViewModel

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalStdlibApi
class HubAdd : Fragment() {

    val viewModel : AddHubViewModel by viewModels()

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
                requireActivity().onBackPressed()
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

        viewModel.skipHub.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("TronX","Saving Hub Details")


                //TODO Code to save HUB details

                Log.d("TronX", "Hub Details Saved")

                //TODO code to handle error

                Log.d("TronX","Error Occurred while saving HUB details")
            }
        })

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "AddDevice" -> {
                        viewModel.imageRef.value.let {
                            if (it.isNullOrEmpty())
                                Toast.makeText(requireContext(), "Please select image for HUB", Toast.LENGTH_SHORT).show()
                            else if (viewModel.hubName.value.isNullOrEmpty()) {
                                AppServices.toastShort(requireContext(),"Please enter hub name.")
                            }
                            else
                                viewModel.tempAddHub(requireContext(), AppServices.wifiBSSID(requireActivity()) ?: "", viewModel.hubName.value ?: "EmbeHome", viewModel.imageRef.value ?: "", FakeDB.packet)
                        }
                    }
                }
                viewModel.action = ""
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this){
            if(viewModel.swapView.value == false) {
                viewModel.changeView()
            } else {
                findNavController().navigateUp()
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }


}
