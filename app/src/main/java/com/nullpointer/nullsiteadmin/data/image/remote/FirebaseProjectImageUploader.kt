package com.nullpointer.nullsiteadmin.data.image.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.storage
import com.nullpointer.nullsiteadmin.domain.image.ProjectImageUploader
import com.nullpointer.nullsiteadmin.models.project.data.ProjectImage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

class FirebaseProjectImageUploader @Inject constructor(@ApplicationContext private val context: Context) : ProjectImageUploader {
    override suspend fun upload(projectId: String, uri: Uri, progress: (Float) -> Unit): ProjectImage = withContext(Dispatchers.IO) {
        require(projectId.isNotBlank() && '/' !in projectId)
        val mime = context.contentResolver.getType(uri)
        require(mime in listOf("image/jpeg", "image/png", "image/webp", "image/gif"))
        val bytes = context.contentResolver.openInputStream(uri)!!.use { input ->
            ByteArrayOutputStream().use { out ->
                val buffer = ByteArray(8192)
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    require(out.size() + count <= 10 * 1024 * 1024)
                    out.write(buffer, 0, count)
                }
                out.toByteArray()
            }
        }
        require(bytes.isNotEmpty() && bytes.size <= 10 * 1024 * 1024)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        require(bounds.outWidth > 0 && bounds.outHeight > 0 && bounds.outMimeType == mime)
        val options = BitmapFactory.Options().apply {
            while (bounds.outWidth / inSampleSize.coerceAtLeast(1) > 1024 || bounds.outHeight / inSampleSize.coerceAtLeast(1) > 1024) {
                inSampleSize = inSampleSize.coerceAtLeast(1) * 2
            }
        }
        val bitmap = checkNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options))
        val thumbnail = try { ByteArrayOutputStream().use { out ->
            @Suppress("DEPRECATION")
            check(bitmap.compress(Bitmap.CompressFormat.WEBP, 80, out)); out.toByteArray()
        } } finally { bitmap.recycle() }
        val id = UUID.randomUUID().toString()
        val root = Firebase.storage.getReference("projects/$projectId")
        suspend fun put(name: String, data: ByteArray, type: String, start: Float, range: Float): String {
            val ref = root.child(name)
            val task = ref.putBytes(data, StorageMetadata.Builder().setContentType(type).build())
            task.addOnProgressListener { progress(start + range * it.bytesTransferred / it.totalByteCount.coerceAtLeast(1)) }
            try { task.await() } finally { if (!task.isComplete) task.cancel() }
            return ref.downloadUrl.await().toString()
        }
        val url = put("$id.${if (mime == "image/jpeg") "jpeg" else mime!!.substringAfter('/')}", bytes, mime!!, 0f, 85f)
        val thumb = put("$id-thumb.webp", thumbnail, "image/webp", 85f, 15f)
        ProjectImage(url = url, thumbnailUrl = thumb)
    }
}
