package com.example.planttracker.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class ImageUtil {

    companion object {

        fun createImageFile(context: Context): File {
            val timeStamp = SimpleDateFormat("HHmmss_ddMMyyyy").format(Date())
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        }

        fun savePhoto(bitmap: Bitmap, file: File) {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 65, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        }

        fun loadPhoto(filepath: String, imageViewReference: WeakReference<ImageView>) : Bitmap? {
            val file = File(filepath)
            if (file.exists()) {
                val bitmapLoaderTask = BitmapLoader(file, imageViewReference)
                bitmapLoaderTask.execute()
            }
            return null
        }

        fun deletePhoto(filepath: String?) {
            if (filepath == null) {
                return
            }
            val file = File(filepath)
            if (file.exists()) {
                file.delete()
            }
            return
        }
    }
}