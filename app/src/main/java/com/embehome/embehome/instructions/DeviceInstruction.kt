package com.embehome.embehome.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.embehome.embehome.R
import kotlinx.android.synthetic.main.fragment_device_instruction.view.*

class DeviceInstruction : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_device_instruction, container, false)



        v.button46.setOnClickListener {
            dismiss()
        }

        return v
    }


}

