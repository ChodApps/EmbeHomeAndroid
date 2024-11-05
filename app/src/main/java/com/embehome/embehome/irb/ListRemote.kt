package com.embehome.embehome.irb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.ListRemoteFragmentBinding
import com.embehome.embehome.irb.adapter.IRBRemoteListRoomAdapter
import com.embehome.embehome.irb.model.RemoteCloudModel
import com.embehome.embehome.irb.viewmodel.RemoteCmnViewModel

class ListRemote : Fragment() {

    private val viewModel: ListRemoteViewModel by viewModels()
    private val remoteViewModel : RemoteCmnViewModel by activityViewModels()
    private val args : ListRemoteArgs by navArgs()

    private lateinit var adapter : IRBRemoteListRoomAdapter
    private lateinit var adapterAllRemote : IRBRemoteListRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppServices.log(args.sno)

        if (args.remoteId != -1) {
            val r = CacheRemoteData.getRemote(args.sno, args.remoteId)
            r?.let {
                if(initRemote(it, remoteViewModel))
                    findNavController().navigate(R.id.action_listRemote_to_IRRemote2)
            }
        }
        else {
            if (viewModel.irbRemoteData.value?.get(args.sno) == null) {
                viewModel.irbRemoteData.value?.set(args.sno, ArrayList())
            }
            val b = CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, args.sno)
            b?.let {
                viewModel.tid.value = it.thing_id
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : ListRemoteFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.list_remote_fragment, container, false)

        viewModel.tid.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.allRemotes (it)
            }
        })

        val b = CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, args.sno)
        b?.let {
            binding.expandListItemName.text = b.thing_name
        }

        viewModel.irbRemoteData.value!![args.sno].also {
            Log.d("TronX", "")
        }

        adapter = IRBRemoteListRoomAdapter(requireContext(), viewModel.irbRemoteData.value!![args.sno]){
           if(initRemote(it, remoteViewModel))
               findNavController().navigate(R.id.action_listRemote_to_IRRemote2)
        }

        adapterAllRemote = IRBRemoteListRoomAdapter(requireContext(), viewModel.allRemotes) {
            if(initRemote(it, remoteViewModel))
                findNavController().navigate(R.id.action_listRemote_to_IRRemote2)
        }

        viewModel.viewAllRemote.observe(viewLifecycleOwner, Observer {
            if (it == true)
            adapterAllRemote.notifyDataSetChanged()
        })

        binding.recyclerViewAllRemote.adapter = adapterAllRemote
        binding.expandedRecList.adapter = adapter

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                when (viewModel.action) {
                    "refreshAdapter" -> {
                        adapter.notifyDataSetChanged()
                        adapterAllRemote.notifyDataSetChanged()
                    }
                }
                viewModel.action = ""
            }
        })

        binding.imageView262.setOnClickListener {
            requireActivity().finish()
        }


        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized) adapter.notifyDataSetChanged()
        if (::adapterAllRemote.isInitialized) adapterAllRemote.notifyDataSetChanged()
    }

    private fun initRemote (remote : RemoteCloudModel, vm : RemoteCmnViewModel) : Boolean{
        val irb = CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, remote.thing_id)
            ?: return false
        vm.irb.value = irb
        vm.macID = CacheHubData.selectedMacID
        vm.remoteName.value = remote.remote_name
        vm.remoteID = remote.remote_id
        vm.remoteCategory.value = RemoteCategoryEnum.valueOf(remote.ir_category)
        vm.operationType = "Operate"
        vm.editMode.value = View.GONE
        vm.remoteIRData = remote.ir_data
        viewModel.tid.value?.let {
            vm.rThingsID = it
        }
        return true
    }

}