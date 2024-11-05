package com.embehome.embehome.Fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.embehome.embehome.Activity.AddSSB
import com.embehome.embehome.Adapter.AreaAdapter
import com.embehome.embehome.Adapter.RoomSceneAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Services.MqttClient
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentRoomBinding
import com.embehome.embehome.viewModel.RoomFragViewModel

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalStdlibApi
class RoomFragment : Fragment() {


    private val viewModel : RoomFragViewModel by viewModels()
    private lateinit var areaGridAdapter : AreaAdapter
    private lateinit var areaListAdapter: AreaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val res = AppServices.getToken(requireContext(),"G${CacheHubData.selectedMacID}")
        res.let {
            try {
                if (it.toInt() == View.VISIBLE) {
                    viewModel.gridView.value = it.toInt()
                    viewModel.listView.value = View.GONE
                }
            }
            catch (e : Exception) {
                
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentRoomBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_room, container, false)
        val sceneItemData = CacheSceneTwoWay.getSceneItemList() ?: ArrayList()

        AppDialogs.startLoadScreen(requireContext())
        binding.imageView275.setOnClickListener{
            val no = CacheHubData.generateHubID(CacheHubData.selectedMacID)
            if (CacheHubData.selectedHubIP.length > 1 && no < 100 ) {
//                    findNavController().navigate(RoomFragmentDirections.actionRoomFragment2ToAddBoard(FakeDB.macID))
                try {
                    requireActivity().startActivity(Intent(requireContext(), AddSSB::class.java))
                } catch (e : Exception) {
                    Toast.makeText(requireContext(),CacheHubData.homeNetwork, Toast.LENGTH_SHORT).show()
                    AppServices.log("TronX" , e.stackTrace.toList().toString())
                }
            } else if (no >= 100) {
                AppDialogs.startMsgLoadScreen(requireContext(), "You have added maximum number devices supported by the Hub.")
            }
            else
                Toast.makeText(requireContext(),CacheHubData.homeNetwork, Toast.LENGTH_SHORT).show()
        }

        /*viewModel.hubIp.observe(viewLifecycleOwner, Observer {
            viewModel.hubInLan.value = it.length > 1
            *//*if (it.length > 1)*//* CacheHubData.selectedHubIP = it
        })*/

//        GlobalScope.launch (Dispatchers.IO) {
//            TronXDB.getDB(requireContext().applicationContext)?.ssbDao()?.
//            getAllBoardData()
//                ?.forEach {
//                    getDeviceFromDao(it).also {
//                        AppServices.log(it.toString())
//                    }
//                }
//        }

        val sceneAdapter = RoomSceneAdapter (requireContext(), viewModel.sceneData, viewModel.noSceneMessage)
        binding.listSceneRoom.adapter = sceneAdapter

        areaGridAdapter = AreaAdapter(requireContext(), viewModel.implRoomData, true)
        areaListAdapter = AreaAdapter(requireContext(), viewModel.implRoomData, false)

        binding.listBoards.adapter = areaListAdapter
        binding.gridBoards.adapter = areaGridAdapter

        viewModel.implRoomData.observe(viewLifecycleOwner, Observer {
            it?.let {d ->
                d.keys.filter {
                    it <= 0
                }.also {
                    it.forEach{
                        d.remove(it)
                    }
                }
            }
            areaGridAdapter.notifyDataSetChanged()
            areaListAdapter.notifyDataSetChanged()
            sceneAdapter.notifyDataSetChanged()
            it?.let {
                AppDialogs.stopLoadScreen()
            }
//            if (it != null) viewModel.loadImages(requireContext(), ArrayList(it.keys))

        })


        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                AppServices.startLoadScreen(requireContext())
            }
            else {
                AppServices.stopLoadScreen()
            }
        })


        viewModel.roomFragmentBack.observe(viewLifecycleOwner, Observer {
            if (it)
                requireActivity().onBackPressed()
        })



        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(requireContext(), viewModel.toastString.value, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.gridView.observe(viewLifecycleOwner, Observer {
            it?.let {
                AppServices.saveToken(requireContext(), "G${CacheHubData.selectedMacID}", it.toString())
            }
        })


        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }



    override fun onStart() {
        super.onStart()
        MqttClient.subscribe(requireContext(), CacheHubData.selectedMacID)
        viewModel.implRoomData.value = viewModel.implRoomData.value

    }


}
