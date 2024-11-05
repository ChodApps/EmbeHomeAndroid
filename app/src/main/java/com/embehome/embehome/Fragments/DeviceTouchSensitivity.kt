package com.embehome.embehome.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.databinding.FragmentDeviceTouchSensitivityBinding
import com.embehome.embehome.viewModel.DeviceTouchSensitivityViewModel


class DeviceTouchSensitivity (val board : BoardDetails) : Fragment(), View.OnClickListener {

    val viewModel : DeviceTouchSensitivityViewModel by viewModels()



    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel.disableAll()
        if (!board.touch_sensitivity.isNullOrEmpty()) {
            try {
                val level = board.touch_sensitivity?.toInt() ?: 0
                viewModel.enableSensitivity(level)
            }
            catch (e : Exception) {
                AppServices.log("Touch sensitivity pre check in existing data - ${e.message}")
            }
        }

    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding : FragmentDeviceTouchSensitivityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_touch_sensitivity, container, false)
        viewModel.getTouchSensitivityLevel(requireContext(), CacheHubData.selectedHubIP, board)

        binding.touchDisableLayout.setOnClickListener(this)
        binding.touchFeatherLayout.setOnClickListener(this)
        binding.touchMildLayout.setOnClickListener(this)
        binding.touchHardLayout.setOnClickListener(this)
        binding.imageView281.setOnClickListener(this)



        binding.button53.setOnClickListener {
            //requireActivity().onBackPressed()
            if (viewModel.touch != -1) {
                viewModel.setTouchSensitivityLevel(requireActivity(), requireContext(), CacheHubData.selectedHubIP, board, viewModel.touch)
            }
            else {
                AppServices.toastShort(requireContext(), "Please Select Touch Sensitivity level")
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.touchDisableLayout -> {
                    viewModel.enableDisableTouch()
                }
                R.id.touchFeatherLayout -> {
                    viewModel.enableFeatherTouch()
                }
                R.id.touchMildLayout -> {
                    viewModel.enableMildTouch()
                }
                R.id.touchHardLayout -> {
                    viewModel.enableHardTouch()
                }
                R.id.imageView281 -> {
                    requireActivity().onBackPressed()
                }
            }
        }

    }


}