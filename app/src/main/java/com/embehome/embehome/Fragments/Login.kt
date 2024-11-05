package com.embehome.embehome.Fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.HttpErrorModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.databinding.FragmentLoginBinding
import com.embehome.embehome.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@ExperimentalStdlibApi
class Login : Fragment() {

    lateinit var progressBar : ProgressBar
    val viewModel : LoginViewModel by viewModels()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentLoginBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_login, container, false)
        val root  = binding.root

        viewModel._email.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.isEmailValid.value = Validators.email(it.toString())
                viewModel.isEmailValid()
            }
        })



        viewModel._paswd.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.isPasswordValid.value = Validators.password(it.toCharArray())
                viewModel.isPasswordValid()
            }
        })

        viewModel.registration.observe(viewLifecycleOwner, Observer {
            if (it == true)
            findNavController().navigate(LoginDirections.actionLogin2ToRegistration())
        })

        viewModel.errorEmail.observe(viewLifecycleOwner, Observer {
            if (it)
                root.login_email_layout.error = "Invalid Email"
        })

        viewModel.errorPassword.observe(viewLifecycleOwner, Observer {
            if (it)
                root.login_paswd_layout.error = "Invalid Password"
        })

        viewModel.submit.observe(viewLifecycleOwner, Observer {
            if (it) {
                GlobalScope.launch(Dispatchers.Main) {
                    AppServices.fragHideSoftKeyBoard(activity, view)
                    AppServices.startLoadScreen(requireContext())
                    val result =
                        HttpManager.login(viewModel.email.value!!, viewModel._paswd.value!!)
                    if (result.status) {
                        Log.d("TronX", "Login Successful")
                        viewModel.btnEnable.value = true
                        AppServices.tempSaveToken(requireContext(), FakeDB.ApplicationToken)
                        AppServices.saveToken(requireContext().applicationContext, "email",viewModel.email.value!!)
                        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(object : OnCompleteListener <InstanceIdResult> {
                            override fun onComplete(task: Task<InstanceIdResult>) {
                                if (!task.isSuccessful)
                                    return

                                task.result?.token?.let {
                                    GlobalScope.launch (Main) {
                                        AppServices.saveToken(requireContext(), "fcm", it)
                                        Log.d("TronX", it)
                                        val res = HttpManager.addFCMToken(it)
                                        if (res.status) {
                                            Log.d("TronX", "FCM Registered")
                                        } else {
                                            Log.d("TronX", res.body.toString())
                                        }
                                    }
//                                    val data = workDataOf(it to it)
//                                    val req = OneTimeWorkRequestBuilder<FCMRegistration>().setInputData(data).build()
//                                    WorkManager.getInstance(requireContext().applicationContext).enqueue(req)
                                }
                            }

                        })

                        findNavController().navigate(R.id.action_login2_to_home2)
                        requireActivity().finish()
                    } else {
                        try {
                            val error = result.body as HttpErrorModel
                            if (error.error_code == 400016L) {
                                viewModel.email.value?.let {
                                    val res = HttpManager.resend(it)
                                    if (res.status) {
                                        Toast.makeText(activity, "Please verify your account", Toast.LENGTH_SHORT).show()
                                        val d = LoginDirections.actionLogin2ToOTPVerification(it)
                                        findNavController().navigate(d).also {
                                            AppDialogs.stopLoadScreen()
                                        }
                                    }
                                }
                                /*regViewModel.regEmail.value = viewModel.email.value
                                val res = HttpManager.resend(it)
                                if (res.status) {
                                    toast.value = "Resend OTP Successfully"
                                }
                                findNavController().navigate(R.id.OTPVerification).also {
                                    AppDialogs.stopLoadScreen()
                                }*/
                            }else  Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                        }
                    }
                    AppServices.stopLoadScreen()
                    viewModel.btnEnable.value = true
                }
            }

        })

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (viewModel.action) {
                    "ForgotPassword" -> {
                        /*if (viewModel.isEmailValid.value == true) {
                            viewModel.requestForgotPassword(requireContext(), viewModel.email.value.toString())
                        }
                        else AppDialogs.openMessageDialog(requireContext(), "Please enter your email in the email field to sent a password reset link to your email address")//Toast.makeText(requireContext(), "Please enter valid email", Toast.LENGTH_SHORT).show()
                   */
                    findNavController().navigate(R.id.action_login2_to_forgotPassword)
                    }

                    "Toast" -> {
                        Toast.makeText(requireContext(), viewModel.toastString, Toast.LENGTH_LONG).show()
                    }
                    "reset" -> {
                        AppDialogs.openMessageDialog(requireContext(), viewModel.toastString)
                    }
                }
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this){
            requireActivity().finish()
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()

        return root
    }

}
