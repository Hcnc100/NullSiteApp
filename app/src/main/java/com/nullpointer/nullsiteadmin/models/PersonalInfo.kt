package com.nullpointer.nullsiteadmin.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonalInfo(
    val name: String = "",
    val profession: String = "",
    val description: String = "",
    val urlImg: String = "",
) : Parcelable