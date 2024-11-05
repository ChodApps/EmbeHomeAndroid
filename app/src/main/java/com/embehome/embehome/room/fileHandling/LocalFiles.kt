package com.embehome.embehome.room.fileHandling

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


suspend fun ttGetImageFile (context: Context, imageName : String) = withContext(IO){
    var image = BitmapFactory.decodeResource(context.resources, R.drawable.hub_in_lan)
    try {
       /* val location = imageName.split('/')
        if (location.size != 3) return@withContext null.also {
            AppServices.log("TronX _ saveImage", "the directory of image is not correct ${location.size}")
        }
        val dir = if (location.size == 3) context.applicationContext.getDir("${location[0]}${File.pathSeparator}${location[1]}", Context.MODE_PRIVATE)
        else context.applicationContext.getDir(location[0], Context.MODE_PRIVATE)*/
        val location = imageName.split('/')

        var dir = context.applicationContext.getDir(location[0], Context.MODE_PRIVATE)
        if (!dir.exists()) dir.mkdirs()
        if (location.size == 3) {
            dir = context.applicationContext.getDir(
                "${location[0]}${File.pathSeparator}${location[1]}",
                Context.MODE_PRIVATE
            )
            if (!dir.exists()) dir.mkdirs()
        }
        val f = File (dir, location[location.size - 1])
        if (f.exists()) {
//            val file = File (f, location[location.size - 1])
            val fileStream = FileInputStream(f)
            image = BitmapFactory.decodeStream(fileStream)
            return@withContext image.also {
                Log.e("TronX _ getImage","Image fetch from storage successful")
            }
        }
    } catch (e: Exception) {
        AppServices.log("TronX _ getImage", e.message.toString())
    }
    null.also {
        Log.e("TronX _ getImage","Image fetch from storage Failed")
    }
}

suspend fun ttSaveImage (context: Context, imageName: String, image : Bitmap) = withContext(IO) {
    try {
        val location = imageName.split('/')
        if (location.size < 2) return@withContext false.also {
            Log.e("TronX _ saveImage", "the directory of image is not correct ${location.size}")
        }
        var dir = context.applicationContext.getDir(location[0], Context.MODE_PRIVATE)
        if (!dir.exists()) dir.mkdirs()
        if (location.size == 3) {
            dir = context.applicationContext.getDir(
                "${location[0]}${File.pathSeparator}${location[1]}",
                Context.MODE_PRIVATE
            )
            if (!dir.exists()) dir.mkdirs()
        }
        val file = File (dir, location[location.size - 1])
        if (!file.exists()) {
            file.createNewFile()
            val out = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            return@withContext true.also {
                Log.e("TronX _ saveImage", "Image saved in storage successful")
            }
        }
        else {
            Log.e("TronX_imgStorage", "${file.exists()} ${file.totalSpace}")
        }
    }
    catch (e : Exception) {
        Log.e("TronX _ saveImage", e.message.toString())
    }
    false.also {
        Log.e("TronX _ saveImage", "Image Saved in storage Failed")
    }
}