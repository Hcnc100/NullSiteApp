package com.nullpointer.nullsiteadmin.data.local.compress

import android.net.Uri

interface ImageCompressDataSource {
    suspend fun compressImg(newImg: Uri): Uri
}