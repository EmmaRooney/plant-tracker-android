package com.example.planttracker.util

import android.graphics.Bitmap
import android.util.LruCache

class BitmapCache(private val lruCache: LruCache<String, Bitmap>) {

    companion object {

        @Volatile
        private var INSTANCE: BitmapCache? = null

        fun getInstance(): BitmapCache {
            if (INSTANCE == null) {
                // Get max available VM memory, exceeding this amount will throw an
                // OutOfMemory exception. Stored in kilobytes as LruCache takes an
                // int in its constructor.
                val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

                // Use 5/8th of the available memory for this memory cache.
                val cacheSize = maxMemory / 8

                val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {

                    override fun sizeOf(key: String, bitmap: Bitmap): Int {
                        // The cache size will be measured in kilobytes rather than
                        // number of items.
                        return bitmap.byteCount / 1024
                    }
                }

                INSTANCE = BitmapCache(memoryCache)
            }

            return INSTANCE as BitmapCache
        }
    }

        fun addBitmapToCache(key: String, bitmap: Bitmap) {
            if (getBitmapFromCache(key) == null) {
                INSTANCE!!.lruCache.put(key, bitmap)
            }
        }

        fun getBitmapFromCache(key: String) : Bitmap? {
            return INSTANCE!!.lruCache.get(key)
        }



}