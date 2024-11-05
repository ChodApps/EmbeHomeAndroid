package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Adapter.TwoWaySelectSwitchAdapter
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.TwoWaySwitchDetails

import com.embehome.embehome.R
import com.embehome.embehome.Repository.HubFeatureHandleRepository
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentTwoWaySwitchSelectionBinding
import com.embehome.embehome.viewModel.TwoWaySwitchSelectionViewModel
import kotlinx.android.synthetic.main.fragment_two_way_switch_selection.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class TwoWaySwitchSelection : Fragment() {

    val viewModel : TwoWaySwitchSelectionViewModel by viewModels()

    private fun getBoardDataFromCache (): ArrayList<BoardDetails> {
        val ids = ArrayList<Int>().apply {
            add (CacheSceneTwoWay.twoWayPid)
            add (CacheSceneTwoWay.twoWaySid)
        }
        return CacheSceneTwoWay.getBoardList(
                CacheHubData.selectedMacID,
                ids
        )
    }

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentTwoWaySwitchSelectionBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_two_way_switch_selection,
                container, false
        )


        val data = getBoardDataFromCache()
        if (data.size == 2) {
            binding.root.two_way_switch_list.adapter = TwoWaySelectSwitchAdapter(requireContext(), data)
        }

        viewModel.performOperation.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {

                    "Back" -> {
                        findNavController().navigateUp()
                    }

                    "Cancel" -> {
                        requireActivity().finish()
                    }
                     "Save" -> {
                         if (CacheSceneTwoWay.twoWayPsid != -1 && CacheSceneTwoWay.twoWaySsid != -1){
                             val data1 = TwoWaySwitchDetails(
                                     CacheSceneTwoWay.twoWayPsid,
                                     CacheSceneTwoWay.twoWayPid,
                                     CacheSceneTwoWay.twoWaySsid,
                                     CacheSceneTwoWay.twoWaySid,
                                     CacheSceneTwoWay.twopsno,
                                     CacheSceneTwoWay.twossno
                             )
                             GlobalScope.launch(Main) {
                                AppDialogs.startLoadScreen(requireContext())
                                 val id = CacheSceneTwoWay.generateTwoWayId(CacheHubData.selectedMacID)
                                 val res = HubFeatureHandleRepository.addTwoWay(requireActivity(), CacheHubData.selectedMacID,CacheHubData.selectedHubIP, data1)
                                 if (res) {



                                     requireActivity().finish().also {
                                         AppDialogs.stopLoadScreen()
                                     }
                                 }
                                 AppDialogs.stopLoadScreen()
                             }
                         }
                         else {
                             showTaost("Please Select One Switch From each SSB")
                         }

                     }

                    else -> {
                        showTaost(viewModel.action)
                    }
                }
            }
        })





        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    fun showTaost (msg : String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}
