package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.rules.adapter.RulesRoomListAdapter
import com.embehome.embehome.rules.utils.RuleDataRepository
import kotlinx.android.synthetic.main.fragment_room_switch_boards.view.*

/**
 * A simple [Fragment] subclass.
 */
class RoomSwitchBoards : Fragment() {

    lateinit var adapter : RulesRoomListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_room_switch_boards, container, false)


        v.imageView259.setOnClickListener {
            requireActivity().finish()
        }


        val rules = RuleDataRepository.getRuleList(CacheHubData.selectedMacID).also {
            it?.value?.size?.let {
                if (it > 0) {
                    v.textView242.visibility = View.GONE
                }
                else {
                    v.textView242.visibility = View.VISIBLE
                }
            }
        }
        adapter = RulesRoomListAdapter(requireContext(), rules)
        v.recyclerViewRulesList.adapter = adapter

        rules?.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
            if (it != null && it.size > 0) {
                v.textView242.visibility = View.GONE
            }
            else {
                v.textView242.visibility = View.VISIBLE
            }
        })

        v.imageView278.setOnClickListener {
            if (rules?.value?.size ?: 21 < 20 && CacheHubData.selectedHubIP.length > 5 && RuleDataRepository.getRuleItemList().size > 0)
            findNavController().navigate(R.id.action_roomSwitchBoards_to_addRule)
            else if (rules?.value?.size ?: 1 >= 20) {
                AppServices.toastShort(requireContext(), "You can add only 20 Rules.")
            }
            else AppServices.toastShort(requireContext(), CacheHubData.homeNetwork)
        }


        return v
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

}
