package com.embehome.embehome.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.embehome.embehome.R
import kotlinx.android.synthetic.main.fragment_hub_instructions.view.*


class HubInstructions : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_hub_instructions, container, false)

        v.button45.setOnClickListener {
            dismiss()
        }

        return v
    }

}