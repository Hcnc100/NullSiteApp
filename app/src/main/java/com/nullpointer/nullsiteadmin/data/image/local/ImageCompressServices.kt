package com.nullpointer.nullsiteadmin.data.image.local

import android.content.Context

class ImageCompressServices(
    private val context: Context
){
    suspend fun compressImage(imagePath: String): String {
        return imagePath
    }
}