package com.nullpointer.nullsiteadmin.core.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.format.DateFormat.is24HourFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImagePainter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager.NO_INTERNET_CONNECTION
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager.SERVER_TIME_OUT
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

val Context.correctFlag: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }

fun Date?.toFormat(context: Context): String {
    val pattern = "EEEE dd/MM/yyyy HH:mm:ss".let {
        if (is24HourFormat(context)) it else it.plus(" aa")
    }
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    val dateTimeNow = this ?: Date()
    return format.format(dateTimeNow)
}

fun Long?.toFormat(context: Context): String {
    val pattern = if (is24HourFormat(context)) {
        "EEEE dd/MM/yyyy HH:mm:ss"
    } else {
        "EEEE dd/MM/yyyy hh:mm:ss aa"
    }
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    val dateTimeNow = Date(this ?: 0L)
    return format.format(dateTimeNow)
}

fun Context.getNotifyServices(): NotificationManager {
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

fun Context.showToastMessage(@StringRes stringRes: Int) {
    Toast.makeText(this, getString(stringRes), Toast.LENGTH_SHORT).show()
}

fun Context.sendEmail(email: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$email"))
    startActivity(intent)
}

@Composable
inline fun <reified VM : ViewModel> shareViewModel(): VM {
    val activity = LocalContext.current as ComponentActivity
    return hiltViewModel(activity)
}

val AsyncImagePainter.isSuccess get() = state is AsyncImagePainter.State.Success

@Composable
fun getGrayColor(): Color {
    return if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
}

fun Modifier.myShimmer(
    shimmer: Shimmer,
): Modifier = composed {
    shimmer(shimmer).background(getGrayColor())
}

fun ViewModel.launchSafeIO(
    isEnabled: Boolean = true,
    blockBefore: suspend CoroutineScope.() -> Unit = {},
    blockAfter: suspend CoroutineScope.(Boolean) -> Unit = {},
    blockException: suspend CoroutineScope.(Exception) -> Unit = {},
    blockIO: suspend CoroutineScope.() -> Unit,
): Job? {
    return if (isEnabled) {
        var isForCancelled = false
        viewModelScope.launch {
            try {
                blockBefore()
                withContext(Dispatchers.IO) { blockIO() }
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> {
                        isForCancelled = true
                        throw e
                    }
                    else -> blockException(e)
                }
            } finally {
                blockAfter(isForCancelled)
            }
        }
    } else {
        null
    }
}

fun CoroutineScope.launchSafeIO(
    isEnabled: Boolean = true,
    blockBefore: suspend CoroutineScope.() -> Unit = {},
    blockAfter: suspend CoroutineScope.(Boolean) -> Unit = {},
    blockException: suspend CoroutineScope.(Exception) -> Unit = {},
    blockIO: suspend CoroutineScope.() -> Unit,
): Job? {
    return if (isEnabled) {
        var isForCancelled = false
        launch {
            try {
                blockBefore()
                withContext(Dispatchers.IO) { blockIO() }
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> {
                        isForCancelled = true
                        throw e
                    }
                    else -> blockException(e)
                }
            } finally {
                blockAfter(isForCancelled)
            }
        }
    } else {
        null
    }
}

suspend fun <T> CollectionReference.getConcatenateObjects(
    includeStart: Boolean,
    fieldTimestamp: String,
    startWithId: String? = null,
    nResults: Long = Long.MAX_VALUE,
    transform: (document: DocumentSnapshot) -> T?,
): List<T> {
    // * base query
    var query = orderBy(fieldTimestamp, Query.Direction.DESCENDING)
    if (startWithId != null) {
        val refDocument = document(startWithId).get().await()
        if (refDocument.exists()) {
            query = if (includeStart)
                query.startAt(refDocument) else query.startAfter(refDocument)
        }
    }
    // * limit result or for default all
    if (nResults != Long.MAX_VALUE) query = query.limit(nResults)
    return query.get().await().documents.mapNotNull { transform(it) }
}

suspend fun <T> CollectionReference.getNewObject(
    timestamp: Date?,
    fieldTimestamp: String,
    transform: (document: DocumentSnapshot) -> T?
): T? {
    val baseRequest = orderBy(fieldTimestamp, Query.Direction.DESCENDING)
    var query = baseRequest
    if (timestamp != null) {
        query = baseRequest.whereGreaterThan(fieldTimestamp, timestamp)
    }
    val response = query.limit(1).get().await().documents
    return if (response.isEmpty()) {
        null
    } else {
        transform(response.first())
    }
}

suspend fun <T> CollectionReference.getNewObjects(
    includeEnd: Boolean,
    fieldTimestamp: String,
    endWithId: String? = null,
    fieldQuery: Boolean = true,
    nResults: Long = Long.MAX_VALUE,
    transform: (document: DocumentSnapshot) -> T?
): List<T> {
    // * base query
    val baseRequest = orderBy(fieldTimestamp, Query.Direction.DESCENDING)
    var query = baseRequest
    if (endWithId != null) {
        val refDocument = document(endWithId).get().await()
        if (refDocument.exists()) {
            query = if (includeEnd)
                query.endAt(refDocument) else query.endBefore(refDocument)
        }
    }
    // * limit result or for default all
    if (nResults != Long.MAX_VALUE) query = query.limit(nResults)

    val previewDocuments = query.get().await().documents

    return if (fieldQuery && previewDocuments.isNotEmpty() && previewDocuments.size < nResults) {
        val newQuery = getConcatenateObjects(
            includeStart = false,
            fieldTimestamp = fieldTimestamp,
            startWithId = previewDocuments.last().id,
            nResults = nResults - previewDocuments.size,
            transform = transform
        )

        previewDocuments.mapNotNull { transform(it) } + newQuery

    } else {
        previewDocuments.mapNotNull { transform(it) }
    }
}

suspend fun List<Task<Void>>.awaitAll() {
    this.forEach { it.await() }
}

fun DocumentSnapshot.getTimeEstimate(
    timestampField: String
): Date? {
    return getTimestamp(
        /* field = */ timestampField,
        /* serverTimestampBehavior = */ DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
    )?.toDate()
}

suspend fun <T> callApiTimeOut(
    timeOut: Long = 3000,
    callApi: suspend () -> T
): T {
    if (!InternetCheck.isNetworkAvailable()) throw Exception(NO_INTERNET_CONNECTION)
    return try {
        withTimeout(timeOut) {
            callApi()
        }
    } catch (e: TimeoutCancellationException) {
        throw Exception(SERVER_TIME_OUT)
    }
}