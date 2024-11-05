package com.embehome.embehome.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.R
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.databinding.FragmentRegistrationBinding
import com.embehome.embehome.viewModel.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_registration.view.*

/**
 * A simple [Fragment] subclass.
 */
class Registration  : Fragment() {


    val viewModel : RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchTerms(requireContext())
        viewModel.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val binding : FragmentRegistrationBinding =   DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false)


        viewModel.regEmail.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
            viewModel.isEmailValid(Validators.email(it))
            }
        })

        viewModel.regMobile.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.isMobileValid(Validators.mobile(it))
            }
        })

        viewModel.regPassword.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.isPasswordValid(Validators.password(it.toCharArray()))
                viewModel.regCnfPassword.value?.let {
                    viewModel.isCnfPasswordValid(it == viewModel.regPassword.value.toString())
                }
            }
        })

        viewModel.regCnfPassword.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                try {

                    viewModel.isCnfPasswordValid(it.toString() == viewModel.regPassword.value.toString())
                }catch (e : Exception) {

                }
            }
        })

        viewModel.regTermsAndCond.observe(viewLifecycleOwner, Observer {
            viewModel.termAndCondition()
        })

        viewModel.regErrorEmail.observe(viewLifecycleOwner, Observer {
            binding.root.reg_email_layout.error = "Invalid Email"
        })

        viewModel.regErrorMobile.observe(viewLifecycleOwner, Observer {
            binding.root.reg_mobile_layout.error = "Invalid Mobile Number"
        })

        viewModel.regErrorPassword.observe(viewLifecycleOwner, Observer {
            binding.root.reg_password_layout.error = "Password should contain at least 8 digits, one upper case character and one numeric digit."
        })

        viewModel.regErrorCnfPassword.observe(viewLifecycleOwner, Observer {
            binding.root.reg_cnf_password_layout.error = "Password Mismatch"
        })

        viewModel.register.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.registerUser(requireContext(), findNavController())
            }
                /*GlobalScope.launch(Dispatchers.Main) {
                    AppDialogs.startLoadScreen(requireContext())
                    val result = HttpManager.userRegister(viewModel.regEmail.value!!, viewModel.regMobile.value!!, viewModel.regPassword.value!!, true)

                    if (result.status) {
                        Log.d("TronX", "User Registration Successful")
                        findNavController().navigate(RegistrationDirections.actionRegistrationToOTPVerification()).also {
                            AppDialogs.stopLoadScreen()
                        }
                    }
                    else {
                        Log.d("TronX", "User Registration Failed : ${result.body as String}")
                        Toast.makeText(activity, "${result.body}", Toast.LENGTH_SHORT).show()
                        AppDialogs.stopLoadScreen()
                    }

                }
            }*/
        })


        viewModel.regBack.observe(viewLifecycleOwner, Observer {
            if(it)
                findNavController().navigateUp()
        })

        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigateUp()
        }


        binding.openTerms.setOnClickListener {
            viewModel.terms(it)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }


/*
    private fun View.onClick(action: suspend () -> Unit) {
        setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                action()
            }
        }
    }

    private fun MaterialButton.onClick(action : suspend () -> Unit) {
        setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                action()
            }
        }
    }
*/

}
