package com.nullpointer.nullsiteadmin.domain.compress

import android.net.Uri

interface CompressImgRepository {
    suspend fun compressImg(newImg: Uri): Uri
}