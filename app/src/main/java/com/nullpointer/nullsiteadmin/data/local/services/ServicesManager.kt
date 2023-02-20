package com.nullpointer.nullsiteadmin.data.local.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.services.imageProfile.UploadImageServices
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ServicesManager(
    private val context: Context
) {

    companion object {
        const val START_COMMAND = "START_COMMAND"
        const val STOP_COMMAND = "STOP_COMMAND"
        const val KEY_INFO_PROFILE = "KEY_INFO_PROFILE"
        const val KEY_URI_PROFILE = "KEY_URI_PROFILE"
    }


    fun uploadImageServices(
        personalInfo: PersonalInfo,
        uriImg: Uri
    ) {
        Intent(context, UploadImageServices::class.java).apply {
            putExtra(KEY_INFO_PROFILE, Json.encodeToString(personalInfo))
            putExtra(KEY_URI_PROFILE, uriImg)
            action = START_COMMAND
        }.also {
            context.startService(it)
        }
    }

}