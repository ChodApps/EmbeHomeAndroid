package com.embehome.embehome.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.CloudHubDataReceiver
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.rules.utils.RuleDataRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/** com.tronx.tt_things_app.viewModel
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 25-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class CreateAreaViewModel : ViewModel() {

    val createAreaBack = MutableLiveData<Boolean>()
    val areaName = MutableLiveData<String>()
    val areaImage = MutableLiveData<Bitmap>()
    val saveArea = MutableLiveData<Boolean>()
    val importImage = MutableLiveData<Boolean>()
    val saveButtonEnable = MutableLiveData<Boolean>()
    init {
        saveButtonEnable.value = true
    }

    fun createAreaBack() {
        createAreaBack.value = true
        createAreaBack.value = false
    }

    fun importImage () {
        importImage.value = true
        importImage.value = false
    }

    fun saveArea() {
        saveArea.value = true
        saveArea.value = false
    }


    fun validateImage (context: Context) : Boolean {
        if (::currentPhotoPath.isInitialized) {
            return true
        }
        else {
            areaImage.value?.let {
                createImageFile(context)
                val out = FileOutputStream(currentPhotoPath)
                it.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.close()
                return true
            }
        }
        return false
    }


    fun createSceneItem (context: Context) {
        if (areaName.value.isNullOrEmpty()) {
            AppServices.toastShort(context, "Please enter valid name")
        }
        else {
            if (areaImage.value != null) {
                areaImage.value?.let {
                    compressImage(context, it)
                    val f = File(currentPhotoPath)
                    val m = RequestBody.create(MediaType.parse("image/png"), f)
                    val c = MultipartBody.Part.createFormData("scene_image","android_upload.JPEG", m)
                    createSceneItem(context, areaName.value ?: "Rule", c, f)
                }
            }
            else {
                AppServices.toastShort(context, "Please select image")
            }
        }
    }

    private fun createSceneItem (context : Context, name : String, image : MultipartBody.Part, f : File) = viewModelScope.launch {
        AppDialogs.startMsgLoadScreen(context, "Saving Scene Name")
        val res = HttpManager.createSceneItem(name, image)
        if (res.status) {
            val b = res.body as CloudHubDataReceiver
            b.data["scenename_id"]?.let {
                val id = it.toInt()
                val img = b.data["scene_image"]
                CacheSceneTwoWay.addSceneItem(id, name, img ?: "", areaImage.value)
            }
            f.delete()
            createAreaBack()
        }
        else AppServices.toastShort(context, "Unable to upload the image.")
        AppDialogs.stopLoadScreen()
    }

    fun updateSceneItem (context: Context, id : Int) {
        if (areaName.value.isNullOrEmpty()) {
            AppServices.toastShort(context, "Please enter valid name")
        }
        else {
            if (areaImage.value != null) {
                areaImage.value?.let {
                    compressImage(context, it)
                    val f = File(currentPhotoPath)
                    val m = RequestBody.create(MediaType.parse("image/png"), f)
                    val c = MultipartBody.Part.createFormData("scene_image","android_upload.JPEG", m)
                    updateSceneItem(context, id,areaName.value ?: "Rule", c, f)
                }
            }
            else {
                AppServices.toastShort(context, "Please select image")
            }
        }
    }

    fun updateSceneItem (context : Context, id : Int, name : String, image : MultipartBody.Part, f : File) = viewModelScope.launch {
        AppDialogs.startMsgLoadScreen(context, "Updating Scene name")
        val res = HttpManager.updateSceneItem(id, name, image)
        if (res.status) {
            val a = CacheSceneTwoWay.getSceneItem(id)
            if (a.scenename_id == id) {
                a.scene_name = name
                a.image = areaImage.value
            }
            f.delete()
            createAreaBack()
        }
        else AppServices.toastShort(context, "Unable to upload the image.")
        AppDialogs.stopLoadScreen()
    }


    fun createRuleItem (context: Context) {
        if (areaName.value.isNullOrEmpty()) {
            AppServices.toastShort(context, "Please enter valid name")
        }
        else {
            if (areaImage.value != null) {
                areaImage.value?.let {
                    compressImage(context, it)
                    val f = File(currentPhotoPath)
                    val m = RequestBody.create(MediaType.parse("image/png"), f)
                    val c = MultipartBody.Part.createFormData("rule_image","android_upload.JPEG", m)
                    createRuleItem(context, areaName.value ?: "Rule", c, f)
                }
            }
            else {
                AppServices.toastShort(context, "Please select image")
            }
        }
    }

    private fun createRuleItem (context : Context, name : String, image : MultipartBody.Part, f : File) = viewModelScope.launch {
        AppDialogs.startMsgLoadScreen(context, "Saving Rule Name")
        val res = HttpManager.createRuleItem(name, image)
        if (res.status) {
            val b = res.body as CloudHubDataReceiver
            b.data["rulename_id"]?.let {
                val id = it.toInt()
                val img = b.data["rule_image"]
                RuleDataRepository.addRuleItem(id, name, img ?: "", areaImage.value)
            }
            f.delete()
            createAreaBack()
        }else AppServices.toastShort(context, "Unable to upload the image.")
        AppDialogs.stopLoadScreen()
    }

    fun updateRule (context: Context, id: Int) {
        if (areaName.value.isNullOrEmpty()) {
            AppServices.toastShort(context, "Please enter valid name")
        }
        else {
            if (areaImage.value != null) {
                areaImage.value?.let {
                    compressImage(context, it)
                    val f = File(currentPhotoPath)
                    val m = RequestBody.create(MediaType.parse("image/png"), f)
                    val c = MultipartBody.Part.createFormData("rule_image","android_upload.JPEG", m)
                    updateRule(context, id,areaName.value ?: "Rule", c, f)
                }
            }
            else {
                AppServices.toastShort(context, "Please select image")
            }
        }
    }

    fun updateRule (context : Context, id : Int, name : String, image : MultipartBody.Part, f : File) = viewModelScope.launch {
        AppDialogs.startMsgLoadScreen(context, "Updating Rule name")
        val res = HttpManager.updateRuleItem(id, name, image)
        if (res.status) {
            val a = RuleDataRepository.getRuleItem(id)
            if (a.rulename_id == id) {
                a.rule_name = name
                a.image = areaImage.value
            }
            f.delete()
            createAreaBack()
        }
        else AppServices.toastShort(context, "Unable to upload the image.")
        AppDialogs.stopLoadScreen()
    }


    fun createArea (context: Context) {
        if (areaName.value.isNullOrEmpty()) {
            AppServices.toastShort(context, "Please enter valid name")
        }
        else {
            if (areaImage.value != null) {
                areaImage.value?.let {
                    compressImage(context, it)
                    val f = File(currentPhotoPath)
                    val m = RequestBody.create(MediaType.parse("image/png"), f)
                    val c = MultipartBody.Part.createFormData("area_image","android_upload.JPEG", m)
                    createArea(context, areaName.value ?: "Area", c, f)
                }
            }
            else {
                AppServices.toastShort(context, "Please select image")
            }
        }
    }

    fun createArea (context : Context, name : String, image : MultipartBody.Part, f : File) = viewModelScope.launch {
        AppDialogs.startMsgLoadScreen(context, "Saving Area name")
        val res = HttpManager.createArea(name, image)
        if (res.status) {
            try {
                val b = res.body as CloudHubDataReceiver
                b.data["area_id"]?.let {
                    val id = it.toInt()
                    val img = b.data["area_image"]
                    CacheHubData.addArea(id, name, img ?: "", areaImage.value)
                }
            }
            catch (e : Exception) {

            }
            f.delete()
            createAreaBack()
        }else AppServices.toastShort(context, "Unable to upload the image.")
        AppDialogs.stopLoadScreen()
    }

    fun updateArea (context: Context, id : Int) {
        if (areaName.value.isNullOrEmpty()) {
            AppServices.toastShort(context, "Please enter valid name")
        }
        else {
            if (areaImage.value != null) {
                areaImage.value?.let {
                    compressImage(context, it)
                    val f = File(currentPhotoPath)
                    val m = RequestBody.create(MediaType.parse("image/png"), f)
                    val c = MultipartBody.Part.createFormData("area_image","android_upload.JPEG", m)
                    updateArea(context, id, areaName.value ?: "Area", c, f)
                }
            }
            else {
                AppServices.toastShort(context, "Please select image")
            }
        }
    }

    fun updateArea (context : Context, id : Int, name : String, image : MultipartBody.Part, f : File) = viewModelScope.launch {
        AppDialogs.startMsgLoadScreen(context, "Updating Area name")
        val res = HttpManager.updateArea(id, name, image)
        if (res.status) {
            val a = CacheHubData.getArea(id)
            if (a.area_id == id) {
                a.area_name = name
                a.image = areaImage.value
            }
            f.delete()
            createAreaBack()
        }
        else AppServices.toastShort(context, "Unable to upload the image.")
        AppDialogs.stopLoadScreen()
    }



    fun readImage (context: Context, activity : Activity, frag : Fragment) {
        Log.d("TronX", "Open Camera or Gallery")
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE ),
                3
            )

        } else {
            AppDialogs.openTitleDialog(context,"Choose Image","From where do you want to select the image from","", { dialog, which ->

            }, "Camera", {dialog, which ->
                openCamera(context, activity, frag)
            }, "Gallery", {dialog, which ->
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                frag.startActivityForResult(pickPhoto, 2)
            })

        }
    }

    private fun openCamera (context: Context, activity : Activity, frag: Fragment) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicture ->
            takePicture.resolveActivity(activity.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile(context)
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    try {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "com.tronx.android.fileprovider",
                            it
                        )
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        frag.startActivityForResult(takePicture, 1)
                    }
                    catch (e : Exception) {
                        Log.e("TronX_camera", "Starting intent fot camera failed - ${e.message}")
                    }
                }
            }
        }

    }


    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".JPEG", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (::currentPhotoPath.isInitialized && currentPhotoPath.isNotEmpty()) File(currentPhotoPath).delete()
            currentPhotoPath = absolutePath
        }
    }

    fun processCameraImage () {
        try {
            if (::currentPhotoPath.isInitialized && !currentPhotoPath.isEmpty()) {
                val f = File(currentPhotoPath)
                val b = BitmapFactory.decodeFile(currentPhotoPath)?.let {
                    val o = ExifInterface(currentPhotoPath).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )
                    val b = rotateImage(it, o)
                    areaImage.value = b!!
                    b?.let { bit ->
                        f.delete()
                        f.createNewFile()
                        val out = FileOutputStream(f)
                        bit.compress(Bitmap.CompressFormat.JPEG, 75, out)
                        out.close()
                    }

                }
            }
        }
        catch (e : Exception) {
            Log.e("TronX","Create item camera - ${e.message}")
        }
//        areaImage.value = BitmapFactory.decodeFile(currentPhotoPath)
    }

    fun processGalleryImage (data : Intent?, context: Context, activity: Activity) {
        if (data != null) {
            createImageFile(context)
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    data.data?.let {
                        activity.contentResolver.openFileDescriptor(it, "r").use { pfd ->
                            if (pfd != null) {
                                val bitmap = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                                if (bitmap != null) {
                                    val t = rotateImage(bitmap, ExifInterface(pfd.fileDescriptor).getAttributeInt(
                                        ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_UNDEFINED
                                    ))
                                    areaImage.value = t ?: bitmap
                                    val out = FileOutputStream(currentPhotoPath)
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
                                    out.close()
                                }
                            }
                        }
                    }
                } catch (ex: IOException) {
                    AppServices.log("TronX","Gallery read image A10 - ${ex.message}")
                }
            }

            else {
                val selectedImage = data.data
                val filePath: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
                val c =
                    selectedImage?.let {
                        activity.applicationContext.contentResolver.query(
                            it, filePath,
                            null, null, null
                        )
                    }
                c?.apply {
                    moveToFirst()
                    getColumnIndex(filePath[0]).let {
                        c.getString(it)?.also {
                            val bitmap = BitmapFactory.decodeFile(it)
                            if (bitmap != null) {
                                val t = rotateImage(bitmap, ExifInterface(it).getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED
                                ))
                                areaImage.value = t ?: bitmap
                                val out = FileOutputStream(currentPhotoPath)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
                                out.close()
                            }
                        }
                    }
                }

            }
        }
    }

    private fun rotateImage (bitmap: Bitmap, orientation : Int) : Bitmap? {
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270F)
            else -> bitmap
        }
    }

    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun compressImage (context: Context, img : Bitmap) {
        val imageConstant = 8000000f
        /*val ei = ExifInterface(currentPhotoPath)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        Log.d("Tronx","${ ExifInterface.ORIENTATION_ROTATE_90} ${ ExifInterface.ORIENTATION_ROTATE_180}" +
                " ${ ExifInterface.ORIENTATION_ROTATE_270}  ${ ExifInterface.ORIENTATION_NORMAL}   $orientation")*/

        Log.d("TronX", "original w = ${img.width} and h = ${img.height}   density = ${img.density}")
        val size = img.height * img.width
        if (size > imageConstant) {
            val r = (imageConstant / size) * 100
            val file = createTempImageFile(context)
            val op = FileOutputStream(file)
            val t = Bitmap.createScaledBitmap(img, 960, 540, false)
            t.compress(Bitmap.CompressFormat.JPEG, 100, op)
            val compresedPic = BitmapFactory.decodeFile(file.absolutePath)
            Log.d("TronX", "compressed w = ${compresedPic.width} and h = ${compresedPic.height} ${file.totalSpace}  ${file.freeSpace}  ${compresedPic.density}")
            currentPhotoPath = file.absolutePath
        }
        else {

        }

    }

    private fun createTempImageFile (context: Context) : File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".JPEG", /* suffix */
            storageDir /* directory */
        )
    }

}