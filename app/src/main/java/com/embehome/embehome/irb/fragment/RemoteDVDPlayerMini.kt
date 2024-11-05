package com.embehome.embehome.irb.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.embehome.embehome.R

class RemoteDVDPlayerMini : Fragment() {

    companion object {
        fun newInstance() = RemoteDVDPlayerMini()
    }

    private lateinit var viewModel: RemoteDVDPlayerMiniViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.remote_d_v_d_player_mini_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RemoteDVDPlayerMiniViewModel::class.java)
        // TODO: Use the ViewModel
    }

}