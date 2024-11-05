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
import com.embehome.embehome.Adapter.ExpandableListAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentTwoWaySSBSelectionBinding
import com.embehome.embehome.viewModel.TwoWaySSBSelectionViewModel
import kotlinx.android.synthetic.main.fragment_two_way_s_s_b_selection.view.*

/**
 * A simple [Fragment] subclass.
 */
class TwoWaySSBSelection : Fragment() {

    val viewModel : TwoWaySSBSelectionViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentTwoWaySSBSelectionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_two_way_s_s_b_selection, container, false)


        binding.root.two_way_ssb_list.adapter = ExpandableListAdapter (requireContext(), "TwoWay")
        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {

                    "Next" -> {
                        if (checkTwoBoardSelected()) {
                            findNavController().navigate(R.id.action_twoWaySSBSelection_to_twoWaySwitchSelection)
                            CacheSceneTwoWay.twoWayPsid = -1
                            CacheSceneTwoWay.twoWaySsid = -1
                        }
                        else
                            showToast("Please Select Two Switch Boards")

                    }

                    "Back" -> {
                        findNavController().navigateUp()
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

    fun showToast (msg : String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun checkTwoBoardSelected () = CacheSceneTwoWay.twoWayPid >= 0 && CacheSceneTwoWay.twoWaySid >= 0

}
