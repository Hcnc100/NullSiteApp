package com.nullpointer.nullsiteadmin.datasource.image.local

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
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
            withMaxWidth(500f)
            withMaxHeight(500f)
            withScaleMode(ScaleMode.SCALE_HEIGHT)
            withIgnoreIfSmaller(true)
        }.get(Dispatchers.IO)

        return result.toUri()
    }

}