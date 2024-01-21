package com.nullpointer.nullsiteadmin.datasource.image.local

import android.net.Uri

interface ImageLocalDataSource {
    suspend fun compressImage(imageUri: Uri): Uri
}