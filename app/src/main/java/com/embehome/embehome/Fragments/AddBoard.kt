package com.embehome.embehome.Fragments


import android.app.Dialog
import android.os.Bundle
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
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.Adapter.SelectAreaAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.databinding.FragmentAddBoardBinding
import com.embehome.embehome.viewModel.AddBoardViewModel
import kotlinx.android.synthetic.main._select_area.*

/**
 * A simple [Fragment] subclass.
 */

class AddBoard : Fragment() {

    val viewModel : AddBoardViewModel by viewModels()
    private val args : AddBoardArgs by navArgs()

    lateinit var dialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.macID.value = args.macID
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentAddBoardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_board, container, false)

        if (FakeDB.areaData.size > 0) viewModel.area.value = FakeDB.areaData[0]

        viewModel.addBoardBack.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigateUp()
            }
        })

        viewModel.area.observe(viewLifecycleOwner, Observer {
         if (::dialog.isInitialized)   if (dialog.isShowing) dialog.dismiss()
            viewModel.areaName.value = it.area_name
            Toast.makeText(requireContext(), it.area_name, Toast.LENGTH_SHORT).show()
        })

        viewModel.selectBoard.observe(viewLifecycleOwner, Observer {
            if  (it) {
                dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)

                dialog.setContentView(R.layout._select_area)
                dialog.show()
                dialog.list_area_select.adapter = SelectAreaAdapter(requireContext(), viewModel.area)
                dialog.setCanceledOnTouchOutside(true)
                dialog.select_area_parent.setOnClickListener{

                }
                dialog.select_area_root.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.goto_create_area.setOnClickListener {
                    dialog.dismiss()
                    findNavController().navigate(AddBoardDirections.actionAddBoardToCreateArea())
                }
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                AppServices.startLoadScreen(requireContext())
            }
            else {
                AppServices.stopLoadScreen()
            }
        })

        viewModel.createArea.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(AddBoardDirections.actionAddBoardToCreateArea())
            }
        })

        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            if (it) {
                Toast.makeText(requireContext(), viewModel.toastMessage.value, Toast.LENGTH_SHORT).show()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigateUp()
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }


}
