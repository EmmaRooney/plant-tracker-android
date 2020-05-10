package com.example.planttracker.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.io.File
import java.lang.ref.WeakReference

class BitmapLoader(private val file: File, private val imageViewReference: WeakReference<ImageView>) : AsyncTask<Void, Void, Bitmap?>() {

    override fun doInBackground(vararg params: Void?): Bitmap? {
        var bitmap = BitmapCache.getInstance().getBitmapFromCache(file.name)
        if (bitmap == null) {
            val options = BitmapFactory.Options()
            options.inSampleSize = 8
            bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
            BitmapCache.getInstance().addBitmapToCache(file.name, bitmap)
        }
        return bitmap
    }

    override fun onPostExecute(result: Bitmap?) {
        val imageView = imageViewReference.get();
        if (imageView != null && result != null) {
            imageView.setImageBitmap(result);
        }
        super.onPostExecute(result)
    }

}