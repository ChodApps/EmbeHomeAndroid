package com.embehome.embehome.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.Validators
import kotlinx.android.synthetic.main.fragment_forgot_password.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ForgotPassword : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_forgot_password, container, false)

        v.button47.isEnabled = false

        v.login_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (Validators.email(it.toString())) {
                        v.login_email_layout.isErrorEnabled = false
                        v.button47.isEnabled = true
                    }
                    else {
                        v.login_email_layout.error = "Invalid Email"
                        v.login_email_layout.isErrorEnabled = true
                        v.button47.isEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })


        val result = MutableLiveData(false)
        result.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                AppDialogs.openTitleDialog(requireContext(), "Forgot Password", "We have sent a link to your email address ${v.login_email.text.toString()}. please use it to reset the password.","",{dialog, which ->

                },"", {dialog, which ->

                },"Close",{dialog, which ->

                }){
                    findNavController().navigateUp()
                }
                result.value = false
            }
        })

        v.imageView264.setOnClickListener {
            findNavController().navigateUp()
        }

        v.button47.setOnClickListener {
            AppDialogs.startLoadScreen(requireContext())
            val email = v.login_email.text.toString()
            if (Validators.email(email)) {
                GlobalScope.launch (Main){
                    val res = HttpManager.forgotPassword(email)
                    AppDialogs.stopLoadScreen()
                    if (res.status) {
                        result.value = true
                    }
                    else AppServices.toastShort(it.context, res.body as String)
                }
            }
        }

        return v
    }

}