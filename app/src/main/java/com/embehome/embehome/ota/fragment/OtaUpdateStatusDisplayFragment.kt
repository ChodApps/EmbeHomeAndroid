package com.embehome.embehome.ota.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.databinding.OtaUpdateStatusDisplayFragmentBinding
import com.embehome.embehome.viewModel.HubActivityViewModel

class OtaUpdateStatusDisplayFragment : Fragment() {

    private val  viewModel: OtaUpdateStatusDisplayViewModel by viewModels ()
    private val activityViewModel : HubActivityViewModel by activityViewModels()
    private val args : OtaUpdateStatusDisplayFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.link != "-" && args.size != "-")
        viewModel.setDetails(args.type, args.version, args.link, args.size)
        else  {
            AppServices.toastShort(requireContext(), "Internal error please retry")
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : OtaUpdateStatusDisplayFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.ota_update_status_display_fragment, container, false)



        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback (true) {
            override fun handleOnBackPressed() {
                viewModel.back(requireContext(), findNavController())
            }

        })

        binding.activity = requireActivity()
        binding.viewModel = viewModel
        binding.navControl = findNavController()
        binding.context = requireContext()
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        activityViewModel.changeBottomBarDisplay(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityViewModel.changeBottomBarDisplay(true)
    }

}