package com.nullpointer.nullsiteadmin.data.local.compress

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode

class ImageCompressDataSourceImpl(
    private val context: Context
) : ImageCompressDataSource {
    override suspend fun compressImg(newImg: Uri): Uri {
        val imageCompress = Compress.with(context, newImg)
            .setQuality(80)
            .concrete {
                withMaxWidth(500f)
                withMaxHeight(500f)
                withScaleMode(ScaleMode.SCALE_HEIGHT)
                withIgnoreIfSmaller(true)
            }.get(Dispatchers.IO)

        return imageCompress.toUri()
    }
}