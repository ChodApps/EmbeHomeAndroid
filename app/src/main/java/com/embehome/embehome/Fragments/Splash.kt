package com.embehome.embehome.Fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.embehome.embehome.CHANNEL_ID
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.getDaoFromDevice
import com.embehome.embehome.irb.RemoteCategoryEnum
import com.embehome.embehome.irb.viewmodel.RemoteCmnViewModel
import com.embehome.embehome.room.dao.AreaDao
import com.embehome.embehome.room.dao.RDSSBDetails
import com.embehome.embehome.room.db.dao.TronXDB
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass.
 */
class Splash : Fragment() {

    val viewModel : RemoteCmnViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_splash, container, false)

        val notificationManager = ContextCompat.getSystemService(
            requireContext().applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager1 = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager1.createNotificationChannel(mChannel)
/*
            val soundUri: Uri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + requireContext()
                    .packageName + "/" + R.raw.bell
            )

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val bell = NotificationChannel("Bell", "Door Bell", NotificationManager.IMPORTANCE_HIGH)
            bell.description = "Door bell alert"
            bell.setSound(soundUri, audioAttributes)
            notificationManager1.createNotificationChannel(bell)*/
        }

//        AppServices.fragHideSoftKeyBoard(requireContext(), requireView())
//        GlobalScope.launch (Main) {
//            delay(1000)
//            val token = getUserToken(requireContext())
//            if (token.length > 5)
//                findNavController().navigate(R.id.action_splash_to_home)
//            else
//                findNavController().navigate(R.id.action_splash_to_login2)
//        }
        AppServices.permissionFragment(this)

        Handler().postDelayed(Runnable {
            val res = AppServices.tempGetToken(requireContext())
            val w =
                requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            w.getNetworkCapabilities(w.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED).also {
                    if (it == true) {
                        if (res.length > 1) {
                            FakeDB.ApplicationToken = res
                            findNavController().navigate(R.id.action_splash_to_home2).also {
                                requireActivity().finish()
                            }
                        } else {
                            findNavController().navigate(R.id.action_splash_to_login2)
                        }
//                    val d = SplashDirections.actionSplashToCreateArea2("Rule", "Create")
//                    findNavController().navigate(d)
                    } else {
                        AppDialogs.openMessageDialog(requireContext(), "Internet not available") {
                            requireActivity().finish()
                        }
                    }
                }
        }, 2000)
/*

        GlobalScope.launch (Main) {
            delay(2000)
            val res = AppServices.tempGetToken(requireContext())
            val w = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            w.getNetworkCapabilities(w.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).also {
                if (it == true) {
                    if (res.length > 1) {
                        FakeDB.ApplicationToken = res
                        findNavController().navigate(R.id.action_splash_to_home2).also {
                            requireActivity().finish()
                        }
                    } else {
                        findNavController().navigate(R.id.action_splash_to_login2)
                    }
//                    val d = SplashDirections.actionSplashToCreateArea2("Rule", "Create")
//                    findNavController().navigate(d)
                }
                else {
                    AppDialogs.openMessageDialog(requireContext(), "Internet not available") {
                        requireActivity().finish()
                    }
                }
            }
        }

*/

//        val p = v.imageView236.layoutParams as ConstraintLayout.LayoutParams
//        p.circleAngle = 0f
//
//        v.imageView234.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
//
//                val p = v.imageView236.layoutParams as ConstraintLayout.LayoutParams
//                getAngle(p0?.width ?: 0, p0?.height ?: 0, p1?.x ?: 0.0f, p1?.y ?: 0f)?.let {
//                    AppServices.log(it.toString())
//                    p.circleAngle = it.toFloat()
//                }
//                v.imageView236.layoutParams = p
//
//                return true
//           }
//
//        })
        /* { view, motionEvent ->

            val p = v.imageView236.layoutParams as ConstraintLayout.LayoutParams
            p.circleAngle = getAngle(view.width, view.x)
            v.imageView236.layoutParams = p

            false
        }*/

//        val res = AppServices.tempGetToken(requireContext())
//      if (res.length > 1 ) {
//            FakeDB.ApplicationToken = res
//            findNavController().navigate(R.id.roomMenu)
//            findNavController().navigate(R.id.action_splash_to_home2).also {
//                requireActivity().finish()
//            }
//          initRemote()
//          findNavController().navigate(R.id.action_splash_to_IRRemote2)



//            GlobalScope.launch (Main) {
//                Log.d("TronX _ Image", "Fetching The Image")
////                val a = ttGetImageFile(requireContext(),"images/areas_icons/icons_area_bedroom_80_px.jpg") as Bitmap
////                Log.d("TronX _ Image", "Fetch Image Success")
////                imageView40.setImageBitmap(a)
//                val db = getDb(requireContext())
////              //  if (db != null) deleteAll(db.areaDao())
//                val res = HttpManager.getAllBoards("24:0a:c4:9b:52:70")
//                if (res.status) {
//                    val data = res.body as CloudBoardData
//                    data.data.forEach {
//                        if (db != null) insertDB(it, db.ssbDao())
//                    }
//                }
//                if (db != null ) {
//                    displayDB(db.ssbDao())
////                    imageSave(db.areaDao(), imageView40)
//                }
////
//
//            }
//        }
//        else
//            findNavController().navigate(R.id.action_splash_to_login2)
//


        return v
    }

    fun getAngle(w: Int, h: Int, x: Float, y: Float) : Int? {
        val p : Float = (65 / 300f * w).toFloat()
        val aa = w/2f - p
        val bb = w/2f + p
        val cc = (bb - aa) / 11f
        if (x in aa..bb && y in aa..w/2f) {
            val ff = ((x - aa) / cc).toInt()
            val angle = ((180 / 10 * ff) + 270) % 360
            return angle
        }
//        val f = x / w * 130
////        if (p > 85 && p < 215) {
////           p = p - 85
////            p = 270
////        }
//        val a = 270 + (f / p * 180) % 360
        return  null// if (a > 90 && a < 270) 90f else if( a > 180 && a < 270) 270f else a
     }

    fun initRemote () {
        viewModel.remoteCategory.value = RemoteCategoryEnum.FAN
        viewModel.macID = "24:0a:c4:9b:52:70"
        viewModel.irb.value = BoardDetails(
            "0013A20040D4D7F1",
            "IRB",
            "c",
            1,
            "pretzel",
            "Image Data",
            3,
            ArrayList()
        )
    }

    private suspend fun getUserToken(context: Context) = withContext(IO) {
        AppServices.tempGetToken(requireContext())
    }

    suspend fun imageSave(context: Context, imageAddress: String, imageView40: ImageView) = withContext(
        Main
    ) {

//        if (imageAddress != null) {
//            val res = HttpManager.getCloudImage(imageAddress)
//            if (res.status) {
//                val image = res.body as Bitmap
//                imageView40.setImageBitmap(image)
//                val save = ttSaveImage(requireContext(), area.area_image, image)
//                Toast.makeText(requireContext(), save.toString(), Toast.LENGTH_LONG).show()
//            }
//            val a = ttGetImageFile(requireContext(), area.area_image) as Bitmap
//            imageView40.setImageBitmap(a)
//        }
    }

    suspend fun displayDB(dao: RDSSBDetails) = withContext(IO) {
//        dao.getAlphabetizedWords().also {
//            it?.forEach {
//                Log.d("TronX _ Room", "${it.area_id} ${it.area_name} ${it.area_image} ")
//            }
//        }
//        dao.getArea(2).also {
//            if (it != null )Log.d("TronX _ Room", "${it.area_id} ${it.area_name} ${it.area_image} ${it.areaDefault}")
//        }

        val b = dao.getAllBoardData()
        b

    }

    suspend fun deleteAll(dao: AreaDao) {
        dao.deleteAll()
    }

    suspend fun insertDB(area: BoardDetails, dao: RDSSBDetails, macId: String) = withContext(IO) {
        val b = getDaoFromDevice(area, macId)
        dao.insertBoardData(b)
    }

    suspend fun getDb(context: Context) = withContext(IO) {
        TronXDB.getDB(context)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AppServices.log(
            "TronX_Per",
            "$requestCode ${permissions.toList()}} ${grantResults.toList()}"
        )
    }
}
