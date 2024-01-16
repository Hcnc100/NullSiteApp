package com.nullpointer.nullsiteadmin.models.wrapper

import android.net.Uri
import java.io.File

data class InfoProfileWrapper(
    val name:String?,
    val imageFile:File?,
    val profession:String?,
    val description:String?,
    val uriImageProfile:Uri?
)
