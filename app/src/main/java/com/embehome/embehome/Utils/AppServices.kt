package com.embehome.embehome.Utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.embehome.embehome.BuildConfig
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.NetworkInterfaceType
import com.embehome.embehome.R
import com.embehome.embehome.room.fileHandling.ttGetImageFile
import com.embehome.embehome.room.fileHandling.ttSaveImage
import kotlinx.android.synthetic.main._load_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.provider.Settings

/** com.tronx.tt_things_app.Constants
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 09-01-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object AppServices {

    private var logFile : File? = null

    private val appPermission = ArrayList <String>().apply {
        add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        add(android.Manifest.permission.RECORD_AUDIO)
        add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        add(android.Manifest.permission.INTERNET)
        add(android.Manifest.permission.BLUETOOTH_ADMIN)
        add(android.Manifest.permission.CAMERA)
        add(android.Manifest.permission.CALL_PHONE)
    }
    val INITIAL_PERMISSION = 1001

    fun hideSoftInput(context : Activity) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((context.currentFocus?:View(context)).windowToken,0)
    }

    fun fragHideSoftKeyBoard(context: Context?, view: View?) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
    }

    fun wifiSSID (context : Context) : String {

        val wifi : WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
         if (wifi.isWifiEnabled && wifi.connectionInfo.ssid != "<unknown ssid>") {
                return wifi.connectionInfo.ssid.removeSurrounding("\"","\"")
             }
        else {
             val info = wifi.connectionInfo
             if (info.ssid != "<unknown ssid>")
                 return info.ssid.removeSurrounding("\"","\"")
         }
        return  "WIFI_NOT_CONNECTED"
    }

    fun wifiBSSID (context : Context?) : String? {
        val wifi : WifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifi.connectionInfo.bssid ?: null
    }

    fun isInternetConnected(context : Context?) : Boolean {
        return try {
            val connection = context!!.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val capability = connection.getNetworkCapabilities(connection.activeNetwork)
                capability!!.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            } else {
                val network = connection.activeNetworkInfo
                network!!.isConnected
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getNetworkType(context: Context?): NetworkInterfaceType {
        try {
            val connection = context!!.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val capabilities = connection.getNetworkCapabilities(connection.activeNetwork)
                if (capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    return NetworkInterfaceType.CELL
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    return NetworkInterfaceType.WIFI
            }
            return NetworkInterfaceType.NO_INTERNET
        } catch (e: Exception) {
            return NetworkInterfaceType.NO_INTERNET
        }
    }

    fun getUDPDiscoveryData () = SimpleDateFormat("ddMMyyyy HHmmss").format(Date())


    fun tempSaveToken(context : Context, token : String) {
        GlobalScope.launch (Dispatchers.Main) {
            val editor = context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit()
            editor.putString("token", token)
            editor.commit()
        }
    }


    fun tempGetToken (context : Context) : String {
        return try {
            val editor = context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE)
            editor.getString("token", "").toString()
        } catch (e : Exception) {
            Log.e("TronX","splash - ${e.message}")
            ""
        }
    }

    fun saveToken(context : Context, name : String, token : String) {
        GlobalScope.launch (Dispatchers.Main) {
            val editor = context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE).edit()
            editor.putString(name, token)
            editor.commit()
        }
    }

    fun getToken (context : Context, name : String) : String {
        val editor = context.getSharedPreferences("Auth_Token", Context.MODE_PRIVATE)
        return editor.getString(name,"").toString()
    }

    lateinit var d : Dialog

    fun startLoadScreen (context: Context) {
        if (::d.isInitialized) d.dismiss()
        d = Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
        d.setContentView(R.layout._load_screen)
        d.loading_screen_cancel.setOnClickListener{
            Log.d("Testing", "Dialog Cancel")
            d.dismiss()
        }
        d.setCancelable(false)
        d.loading_layout.setBackgroundColor(Color.WHITE)
        d.setCanceledOnTouchOutside(false)
        d.show()
    }

    fun stopLoadScreen() {
        if (::d.isInitialized){
            d.isShowing.also {
                if (it) d.dismiss()
            }
        }
    }

    fun actionDialogWithYesAndNo (context: Context, title : String, yes : () -> Unit, no : () -> Unit) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, android.R.style.Theme_Material_Light_DialogWhenLarge_NoActionBar)).apply {
            setMessage("I am Your Saviour")
            setTitle(title)
            setPositiveButton("True") { dialog, which ->
                Log.d("Testing", "which")
                yes()
            }
            setNegativeButton("False") { dialog, which ->
                no()
            }

        }
        builder.create().show()


    }

    fun toastShort (context: Context, msg : String)  = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    fun toastLong (context: Context, msg : String)  = Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

    fun log (data : String) {
        log("TronX", data)
    }

    fun log (tag : String, data: String) {
        Log.d(tag, data)
        if (BuildConfig.logs) {
            logFile?.let {
                if (it.exists()) {
                    GlobalScope.launch(IO) {
                        try {
                            val stream = FileOutputStream(it, true)
                            val txt = "$tag  ---  $data\n"
                            stream.write(txt.toByteArray())
                            stream.close()
                        } catch (e: Exception) {

                        }
                    }
                }
            }
        }
    }

    fun openLogFile (context: Context) {
        if (BuildConfig.logs) {
            val dir = context.getExternalFilesDir("logs")
            dir?.let {
                if (!it.exists()) {
                    it.mkdirs()
                }
                logFile = File(context.getExternalFilesDir("logs"), "logs.txt")
                logFile?.let {
                    if (!it.exists()) it.createNewFile() else log(
                        "\n\n\nSession ${
                            SimpleDateFormat(
                                "ddMMyyyy HHmmss"
                            ).format(Date())
                        }\n\n"
                    )
                }
            }
        }
    }


    suspend fun imageSave (context: Context, imageAddress : String) = withContext(
        Dispatchers.Main
    ) {
        val a = ttGetImageFile(context, imageAddress) as Bitmap?
        if (a != null) {
            return@withContext a
        } else {
            val res = HttpManager.getCloudImage(imageAddress)
            if (res.status) {
                val image = res.body as Bitmap?
                val save = image?.let { ttSaveImage(context, imageAddress, it) }
//                Toast.makeText(context, save.toString(), Toast.LENGTH_LONG).show()
                return@withContext image
            }
        }
        null
    }

    fun permissionFragment (obj : Fragment) {
        obj.requestPermissions(Array(appPermission.size) {
            appPermission[it]
        }, INITIAL_PERMISSION)
    }
    fun permissionActivity (obj : Activity) {
        obj.requestPermissions(Array(appPermission.size) {
            appPermission[it]
        }, INITIAL_PERMISSION)
    }
    fun exactAlarmPermissionActivity(obj : Activity){
        if (isExactAlarmPermissionGranted(obj)) {
            //Toast.makeText(obj, "Exact alarm permission is already granted", Toast.LENGTH_SHORT).show()
        } else {
            requestExactAlarmPermission(obj)
        }
    }
    fun isExactAlarmPermissionGranted(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            context.getSystemService(Context.ALARM_SERVICE)?.let {
                val alarmManager = it as android.app.AlarmManager
                alarmManager.canScheduleExactAlarms()
            } ?: false
        } else {
            true
        }
    }

    fun requestExactAlarmPermission(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(intent)
        }
    }

}