package com.embehome.embehome.curtain.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.embehome.embehome.R

/**
 * A simple [Fragment] subclass.
 */
class CurtainConfiguration : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_curtain_configuration, container, false)
    }

}
