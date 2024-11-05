package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Adapter.TwoWayListAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentTwoWayListBinding
import com.embehome.embehome.viewModel.TwoWayListViewModel
import kotlinx.android.synthetic.main.fragment_two_way_list.view.*

/**
 * A simple [Fragment] subclass.
 */
class TwoWayList : Fragment() {

    val viewModel : TwoWayListViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentTwoWayListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_two_way_list, container, false)
        val twoWayAdapter  = TwoWayListAdapter(requireContext(), viewModel.twoWayData?.value ?: ArrayList())
        binding.root.two_way_list.adapter = twoWayAdapter

       /* binding.root.two_way_list.adapter = TwoWayListAdapter(requireContext()) {view, position ->

        }*/

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {

                    "Add" -> {
                        if (viewModel.twoWayData?.value?.size ?: 19 < 20) {
                            if (CacheHubData.selectedHubIP.length > 5) {
                                CacheSceneTwoWay.clearTwoWay()
                                findNavController().navigate(R.id.action_twoWayList_to_twoWaySSBSelection)
                            } else
                                AppServices.toastShort(requireContext(), CacheHubData.homeNetwork)
                        }
                        else AppServices.toastShort(requireContext(), "You can add only 20 Two way.")
                    }

                    "Back" -> {
                        requireActivity().finish()
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

}
