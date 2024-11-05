package com.embehome.embehome.curtain.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.embehome.embehome.Activity.FragmentHandler
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.curtain.CurtainDisplayViewModel
import com.embehome.embehome.databinding.FragmentCurtainDisplayBinding

/**
 * A simple [Fragment] subclass.
 */
class CurtainDisplay (val deviceID : Int, val status : MutableLiveData<Int>) : Fragment() {

    val viewModel : CurtainDisplayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b =CacheSceneTwoWay.getBoardData(CacheHubData.selectedMacID, deviceID)
        if (b == null) requireActivity().finish()
        viewModel.curtain.value = b?.switchesList?.get(0)
        viewModel.b = b
    }

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentCurtainDisplayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_curtain_display, container, false)

        try {

            status.observe(viewLifecycleOwner, Observer {
                it?.let {
                    binding.seekBar.progress = it
                    if (it == 100) {
                        binding.imageView122.setImageResource(R.drawable.tt_cur_curtain_with_rod)
                    }
                    else if (it == 0) {
                        binding.imageView122.setImageResource(R.drawable.tt_cur_window_curtain_with_rod)
                    }
                    else {
                        binding.imageView122.setImageResource(R.drawable.tt_cur_curtain_half)
                    }
                }
            })
        }
        catch (e : Exception) {

        }

        viewModel.curtain.value?.let {
            if (it.switchstatus == 0) it.switchLevel = 0
            if (it.switchLevel == 100) {
                binding.imageView122.setImageResource(R.drawable.tt_cur_curtain_with_rod)
            }
            else if (it.switchLevel == 0) {
                binding.imageView122.setImageResource(R.drawable.tt_cur_window_curtain_with_rod)
            }
            else {
                binding.imageView122.setImageResource(R.drawable.tt_cur_curtain_half)
            }
        }

        viewModel.curtain.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.switchstatus == 0) it.switchLevel = 0
                binding.seekBar.progress = it.switchLevel
                if (it.switchLevel == 100) {
                    binding.imageView122.setImageResource(R.drawable.tt_cur_curtain_with_rod)
                }
                else if (it.switchLevel == 0) {
                    binding.imageView122.setImageResource(R.drawable.tt_cur_window_curtain_with_rod)
                }
                else {
                    binding.imageView122.setImageResource(R.drawable.tt_cur_curtain_half)
                }
            }
        })

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.configMode.value?.also {
                    seekBar?.let {
                        viewModel.playCurtain(requireContext(), deviceID, it.progress, false)
                    }
                }
            }


        })

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "Config" -> {
                        if (CacheHubData.selectedHubIP.length > 5) {
//                            viewModel.configMode.value = true
                            viewModel.b?.let {
                                val i = getConfigIntent(requireContext(), CacheHubData.selectedMacID, it.thing_serial_number)
                                startActivity(i)
                            }
                        }
                        else
                            Toast.makeText(requireContext(), CacheHubData.homeNetwork, Toast.LENGTH_SHORT).show()
                    }

                    "Open" -> {
                        viewModel.configMode.value?.also {
                           viewModel.playCurtain(requireContext(), deviceID, 0, it)
                        }
//                        Toast.makeText(requireContext(), "Closing Curtain", Toast.LENGTH_SHORT).show()
                    }

                    "Close" -> {
                        viewModel.configMode.value?.also {
                            viewModel.playCurtain(requireContext(), deviceID, 100, it)
                        }
//                        Toast.makeText(requireContext(), "Opening Curtain", Toast.LENGTH_SHORT).show()
                    }

                    "Stop" -> {
                        viewModel.configMode.value?.also {
                            viewModel.playCurtain(requireContext(), deviceID, 200, it)
                        }
//                        Toast.makeText(requireContext(), "Stopping Curtain", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun getConfigIntent (context: Context, macID : String, thingsID : String) : Intent {
        val intent = Intent (context, FragmentHandler::class.java)
        intent.putExtra("fragment", "ConfigCur")
        intent.putExtra("macID", macID)
        intent.putExtra("thingsID", thingsID)
        return intent
    }

    override fun onStart() {
        super.onStart()
        status.value = status.value
    }

}
