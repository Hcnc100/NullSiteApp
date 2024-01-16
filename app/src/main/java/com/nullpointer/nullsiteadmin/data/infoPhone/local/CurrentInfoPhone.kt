package com.nullpointer.nullsiteadmin.data.infoPhone.local

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.nullpointer.nullsiteadmin.models.data.InfoPhoneData
import kotlinx.coroutines.tasks.await
import android.provider.Settings
class CurrentInfoPhone(
    private val context:Context
) {

    private val messaging= Firebase.messaging
    suspend fun getCurrentInfoPhone(): InfoPhoneData {

        val token= messaging.token.await()
        val uuidPhone = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)


        return InfoPhoneData(
            tokenGCM = token,
            uuidPhone = uuidPhone,
            modelPhone = "",
            versionOperativeSystem = "",
            operativeSystem = ""
        )
    }
}