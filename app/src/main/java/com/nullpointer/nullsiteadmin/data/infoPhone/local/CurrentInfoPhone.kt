package com.nullpointer.nullsiteadmin.data.infoPhone.local

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.nullpointer.nullsiteadmin.models.phoneInfo.data.InfoPhoneData
import kotlinx.coroutines.tasks.await

class CurrentInfoPhone(
    private val context:Context
) {
    private suspend fun getMessagingToken(): String =
        Firebase.messaging.token.await()

    @SuppressLint("HardwareIds")
    private fun getUUIDPhone(): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    private fun getModelPhone(): String =
        android.os.Build.MODEL

    private fun getVersionNameApp(): String? =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName

    private fun getOperativeSystem(): String =
        "Android ${android.os.Build.VERSION.SDK_INT}"

    private fun getVersionNumberApp(): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return androidx.core.content.pm.PackageInfoCompat
            .getLongVersionCode(packageInfo)
            .toString()
    }


    suspend fun getCurrentInfoPhone(): InfoPhoneData {
        return InfoPhoneData(
            uuidPhone = getUUIDPhone(),
            modelPhone = getModelPhone(),
            tokenGCM = getMessagingToken(),
            versionNameApp = getVersionNameApp() ?: "Unknown",
            operativeSystem = getOperativeSystem(),
            versionNumberApp = getVersionNumberApp(),
        )
    }
}
