package com.embehome.embehome.Fragments

import android.os.Bundle
import android.util.Log
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
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.databinding.FragmentChangePasswordBinding
import com.embehome.embehome.viewModel.ChangePasswordViewModel
import kotlinx.android.synthetic.main.fragment_change_password.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalStdlibApi
class ChangePassword : Fragment() {

    val viewModel : ChangePasswordViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentChangePasswordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)

        viewModel.changePasswordBack.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigateUp()
            }
        })

        viewModel.oldPassword.observe(viewLifecycleOwner, Observer {
            viewModel.validatePasswords()
        })

        viewModel.newPassword.observe(viewLifecycleOwner, Observer {
            viewModel.isPasswordValid(Validators.password(it.toCharArray()))
        })

        viewModel.cnfPassword.observe(viewLifecycleOwner, Observer {
            viewModel.isCnfPasswordValid(Validators.cnfPassword(viewModel.newPassword.value.toString(), it))
        })

        viewModel.regErrorPassword.observe(viewLifecycleOwner, Observer {
            binding.root.textInputLayout4.error = "Password should contain at least 8 digits, one upper case character and one numeric digit."
        })

        viewModel.regErrorCnfPassword.observe(viewLifecycleOwner, Observer {
            binding.root.textInputLayout5.error = "Password Mismatch"
        })


        viewModel.changePassword.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("TronX","Change Password Code") //TODO Code to change password
                GlobalScope.launch(Dispatchers.Main) {
                    if (viewModel.validatePasswords()) {
                        val result = HttpManager.changePassword(viewModel.oldPassword.value.toString(), viewModel.newPassword.value.toString())
                        if (result.status)
                        {
                            Toast.makeText(requireContext(), "Password Change Successful", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                        else {
                            Toast.makeText(requireContext(), " ${result.body as String}", Toast.LENGTH_SHORT).show()
                        }

                    }
                    else {
                        AppServices.toastShort(requireContext(), "Incorrect details")
                    }
                }
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

    fun validatePasswords () : Boolean {
        return true
    }

}
