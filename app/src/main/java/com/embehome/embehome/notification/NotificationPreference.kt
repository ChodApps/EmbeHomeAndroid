package com.embehome.embehome.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.notification.model.NotificationPrefCloudModel
import kotlinx.android.synthetic.main.fragment_notification_prefrence.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NotificationPreference : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_notification_prefrence, container, false)

        GlobalScope.launch (Main) {
            try {
                AppDialogs.startLoadScreen(requireContext())
                val res = HttpManager.getUserSettings()
                if (res.status) {
                    (res.body as NotificationPrefCloudModel).let { d ->
                        if (d.data != null) {
                            v.switch4.isChecked = d.data.fcm_enabled.also {
                                v.switchMaterial.isEnabled = it
                                v.switchMaterial2.isEnabled = it
                            }
                            v.switchMaterial.isChecked = d.data.fcm_thing_status
                            v.switchMaterial2.isChecked = d.data.fcm_alert_status
                        }
                        else {
                            v.switch4.isChecked = true
                            v.switchMaterial.isChecked = true
                            v.switchMaterial2.isChecked = true
                        }
                    }

                } else {
                    AppServices.toastShort(
                        requireContext(),
                        "Unable to fetch you current preference"
                    )
                    v.switch4.isChecked = true
                    v.switchMaterial.isChecked = true
                    v.switchMaterial2.isChecked = true
                }
            }
            catch (e : Exception) {
                AppServices.log("TronX",e.message.toString())
            }
            AppDialogs.stopLoadScreen()

        }

        v.switch4.setOnClickListener {
            if (!v.switch4.isChecked) {
                v.switchMaterial.isEnabled = false
                v.switchMaterial2.isEnabled = false
                v.switchMaterial.isChecked = false
                v.switchMaterial2.isChecked = false
            }
            else {
                v.switchMaterial.isEnabled = true
                v.switchMaterial2.isEnabled = true
                v.switchMaterial.isChecked = true
                v.switchMaterial2.isChecked = true
            }
        }

        v.button50.setOnClickListener {
            AppDialogs.startMsgLoadScreen(requireContext(), "Saving notification settings...")
            val all = v.switch4.isChecked
            val s = v.switchMaterial.isChecked
            val a = v.switchMaterial2.isChecked
            GlobalScope.launch (Main) {
                val res = HttpManager.setUserSettings(all, s, a)
                if (res.status) {
                    AppServices.toastShort(requireContext(), "Settings updated successfully ")
                }
                else {
                    AppServices.toastShort(requireContext(),"Failed to update the settings")
                }
                AppDialogs.stopLoadScreen()
            }
        }

        v.imageView113.setOnClickListener {
            findNavController().navigateUp()
        }

        return v
    }


}