package com.example.planttracker.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.io.File
import java.lang.ref.WeakReference

class BitmapLoader(private val file: File, private val imageViewReference: WeakReference<ImageView>) : AsyncTask<Void, Void, Bitmap>() {

    override fun doInBackground(vararg params: Void?): Bitmap {
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    override fun onPostExecute(result: Bitmap) {
        val imageView = imageViewReference.get();
        if (imageView != null) {
            imageView.setImageBitmap(result);
        }
        super.onPostExecute(result)
    }

}