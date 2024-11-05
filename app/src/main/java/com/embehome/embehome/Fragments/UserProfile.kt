package com.embehome.embehome.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.NetworkHosts
import com.embehome.embehome.databinding.FragmentUserProfileBinding
import com.embehome.embehome.viewModel.UserProfileViewModel

/**
 * A simple [Fragment] subclass.
 */
class UserProfile : Fragment() {

    val viewModel : UserProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentUserProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false)
        val name =  AppServices.getToken(requireContext(),"email")
        if (name.length > 4) binding.textView192.text = name
        viewModel.userProfileBack.observe(viewLifecycleOwner, Observer {
            if (it) {

                findNavController().navigateUp()
            }
        })

        viewModel.logOut.observe(viewLifecycleOwner, Observer {
            if (it) {
                AppDialogs.openTitleDialog(requireContext(), "Log out", "Do you want to Log out ? ","No", "Yes") {d, e ->
                    Log.d("TronX","reDirect to Login Page")
                    viewModel.logout(requireActivity())
                    // findNavController().navigate(UserProfileDirections.actionUserProfileToAuthentication2())
//                    requireActivity().finish()
//                    requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
                }
//                Log.d("TronX","reDirect to Login Page")
//                logout(requireContext())
//               // findNavController().navigate(UserProfileDirections.actionUserProfileToAuthentication2())
//                requireActivity().finish()
//                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        })
        viewModel.deleteUserAlert.observe(viewLifecycleOwner, Observer {
            if (it) {
                AppDialogs.openMessageDialogOk(requireContext(),"Your request to delete the user has been received. It will be reviewed and processed by the administrator "){
                    viewModel.logout(requireActivity())
                }
//                AppDialogs.openTitleDialog(requireContext(), "Delete user", "Your request to delete the user has been received. It will be reviewed and processed by the administrator ","", "OK") {d, e ->
//
//                    Log.d("TronX","reDirect to Login Page")
//                    viewModel.logout(requireActivity())
//                    // findNavController().navigate(UserProfileDirections.actionUserProfileToAuthentication2())
////                    requireActivity().finish()
////                    requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
//                }
//                Log.d("TronX","reDirect to Login Page")
//                logout(requireContext())
//               // findNavController().navigate(UserProfileDirections.actionUserProfileToAuthentication2())
//                requireActivity().finish()
//                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        })
        viewModel.deleteUser.observe(viewLifecycleOwner, Observer {
            if (it) {
                AppDialogs.openTitleDialog(requireContext(), "Delete Account", "Are you sure you want to delete your account?\n" +
                        "All of the user's data, including history of results, will be deleted ","Cancel", "Delete") {d, e ->
                    Log.d("TronX","reDirect to Login Page")
                    viewModel.deleteUser(requireActivity())
                    // findNavController().navigate(UserProfileDirections.actionUserProfileToAuthentication2())
//                    requireActivity().finish()
//                    requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
                }
//                Log.d("TronX","reDirect to Login Page")
//                logout(requireContext())
//               // findNavController().navigate(UserProfileDirections.actionUserProfileToAuthentication2())
//                requireActivity().finish()
//                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        })
        viewModel.changePassword.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("TronX","open Change Password Page")
                findNavController().navigate(UserProfileDirections.actionUserProfileToChangePassword2())
            }
        })

        viewModel.performAction.observe(viewLifecycleOwner, Observer {
            if (it) {
                when(viewModel.action) {

                    "ContactUs" -> {
                        val d = UserProfileDirections.actionUserProfileToWebViewDisplay(NetworkHosts.ContactUs, "Contact Us")
                        findNavController().navigate(d)
                    }

                    "AboutUs" -> {
                        val d = UserProfileDirections.actionUserProfileToWebViewDisplay(NetworkHosts.AboutUs, "About Us")
                        findNavController().navigate(d)
                    }

                    "Notification" -> {
                        findNavController().navigate(R.id.action_userProfile_to_notificationPreference)
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

    fun logout (context : Context)  {
        Log.d("TronX","removeUserDetails")
//        val res = HttpManager.logout("FakeDB")
        context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit().clear().commit()
    }

}
