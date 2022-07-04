package com.nullpointer.nullsiteadmin.services.imageProfile

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.nullpointer.nullsiteadmin.models.PersonalInfo

object UploadImageServicesControl {

         const val START_COMMAND = "START_COMMAND"
         const val STOP_COMMAND = "STOP_COMMAND"
         const val KEY_INFO_PROFILE = "KEY_INFO_PROFILE"
         const val KEY_URI_PROFILE = "KEY_URI_PROFILE"

        fun init(context: Context, personalInfo: PersonalInfo, uriImg: Uri) =
            sendCommand(START_COMMAND, context, personalInfo, uriImg)

        fun stop(context: Context) =
            sendCommand(STOP_COMMAND, context)

        private fun sendCommand(
            command: String,
            context: Context,
            personalInfo: PersonalInfo? = null,
            uriImg: Uri? = null
        ) = Intent(context,UploadImageServices::class.java).apply {
            putExtra(KEY_INFO_PROFILE, personalInfo)
            putExtra(KEY_URI_PROFILE, uriImg)
            action = command
        }.also {
            context.startService(it)
        }


}