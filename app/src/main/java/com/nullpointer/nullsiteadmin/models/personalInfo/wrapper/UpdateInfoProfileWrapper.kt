package com.nullpointer.nullsiteadmin.models.personalInfo.wrapper

import android.net.Uri

data class UpdateInfoProfileWrapper(
    val name:String?,
    val profession:String?,
    val description:String?,
    val uriFileImgProfile:Uri?,
)
