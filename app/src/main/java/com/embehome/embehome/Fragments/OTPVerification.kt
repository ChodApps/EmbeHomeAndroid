package com.embehome.embehome.Fragments


import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.CloudAPIRequestReceiver
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.databinding.FragmentOtpverificationBinding
import com.embehome.embehome.viewModel.OTPVerificationViewModel
import kotlinx.android.synthetic.main.fragment_otpverification.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalStdlibApi
class OTPVerification : Fragment() {


    val viewModel : OTPVerificationViewModel by viewModels()
    val args : OTPVerificationArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.email.length > 5) {
            viewModel.email.value = args.email
        }
        else {
            findNavController().navigateUp()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentOtpverificationBinding  =  DataBindingUtil.inflate(inflater,R.layout.fragment_otpverification, container, false)
        val root = binding.root

        binding.otpBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.field_1.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty())binding.root.otp_field_2.requestFocus()
            viewModel.updateBtn()
        })

        viewModel.field_2.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty())binding.root.otp_field_3.requestFocus()
            viewModel.updateBtn()
        })

        viewModel.field_3.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty())binding.root.otp_field_4.requestFocus()
            viewModel.updateBtn()
        })

        viewModel.field_4.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                AppServices.fragHideSoftKeyBoard(activity, view)
            }
            viewModel.updateBtn()
        })

        binding.root.otp_field_2.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN ) {
                if (viewModel.field_2.value == null || viewModel.field_2.value == "") {
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_DEL) {
                        viewModel.field_1.value = ""
                        binding.root.otp_field_1.requestFocus()
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }

        binding.root.otp_field_3.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN ) {
                if (viewModel.field_3.value == null || viewModel.field_3.value == "") {
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_DEL) {
                        viewModel.field_2.value = ""
                        binding.root.otp_field_2.requestFocus()
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }

        binding.root.otp_field_4.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN ) {
                if (viewModel.field_4.value == null || viewModel.field_4.value == "") {
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_DEL) {
                        viewModel.field_3.value = ""
                        binding.root.otp_field_3.requestFocus()
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }
        viewModel.verify.observe(viewLifecycleOwner, Observer {
            if (it) {
                GlobalScope.launch(Dispatchers.Main) {
                    val result = HttpManager.verifyEmail(args.email, viewModel.getOTP())
                    if (result.status) {
                        Log.d("TronX","Otp Verified Successfully")
                        AppServices.saveToken(requireContext(), "email", args.email)
                        (result.body as CloudAPIRequestReceiver).also {
                            if (it.auth_token != null && it.auth_token.length > 5) {
                                FakeDB.ApplicationToken = it.auth_token
                                AppServices.tempSaveToken(requireContext(), it.auth_token)
                            }
                        }
                        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(object :
                            OnCompleteListener<InstanceIdResult> {
                            override fun onComplete(task: Task<InstanceIdResult>) {
                                if (!task.isSuccessful)
                                    return

                                task.result?.token?.let {
                                    AppServices.saveToken(requireContext(), "fcm", it)
                                    GlobalScope.launch (Dispatchers.Main) {
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
                        findNavController().navigate(R.id.action_OTPVerification_to_home2)
                        requireActivity().finish()
                    }
                    else {
                        viewModel.btnEnable.value = true
                        Log.d("TronX","Otp Verification Failed : ${result.body as String}")
                        Toast.makeText(activity, result.body , Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it?.let {
                AppServices.toastShort(requireContext(), it)
                viewModel.toast.value = null
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigateUp()
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return root
    }


}
