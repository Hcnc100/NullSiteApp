package com.nullpointer.nullsiteadmin.datasource.image.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.nullpointer.nullsiteadmin.data.image.local.ImageCompressServices
import com.nullpointer.nullsiteadmin.datasource.image.remote.ImageRemoteDataSource
import kotlinx.coroutines.Dispatchers
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode

class ImageLocalDataSourceImpl(
    private val context: Context
):ImageLocalDataSource {
    override suspend fun compressImage(imageUri: Uri): Uri {
        val result = Compress.with(
            context,
            imageUri
        ).concrete {
            withMaxWidth(100f)
            withMaxHeight(100f)
            withScaleMode(ScaleMode.SCALE_HEIGHT)
            withIgnoreIfSmaller(true)
        }.get(Dispatchers.IO)

        return result.toUri()
    }

}