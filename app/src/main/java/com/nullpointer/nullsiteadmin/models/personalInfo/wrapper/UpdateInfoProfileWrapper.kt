package com.nullpointer.nullsiteadmin.models.personalInfo.wrapper

import android.net.Uri
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileSocialLink

data class UpdateInfoProfileWrapper(
    val name:String?,
    val profession:String?,
    val description:String?,
    val uriFileImgProfile:Uri?,
    val baseline: PersonalInfoData?,
    val socialLinks: List<ProfileSocialLink>? = null,
    val translations: Map<String, Map<String, String>>? = null,
)
