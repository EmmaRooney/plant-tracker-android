package com.example.planttracker.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageUtil {

    companion object {

        fun createImageFile(context: Context): File {
            val timeStamp = SimpleDateFormat("HHmmss_ddMMyyyy").format(Date())
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            Log.d("PATH", storageDir!!.absolutePath)

            return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        }

        fun savePhoto(bitmap: Bitmap, file: File) {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        }

        fun loadPhoto(filepath: String) : Bitmap? {
            val file = File(filepath)
            if (file.exists()) {
                return BitmapFactory.decodeFile(file.absolutePath)
            }
            return null
        }

        fun deletePhoto(filepath: String) {

        }
    }
}