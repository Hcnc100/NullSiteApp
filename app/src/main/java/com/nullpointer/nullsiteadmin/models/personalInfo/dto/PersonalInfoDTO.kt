package com.nullpointer.nullsiteadmin.models.personalInfo.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.core.utils.Constants
import com.google.firebase.firestore.FieldValue
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileSocialLink
import com.nullpointer.nullsiteadmin.models.personalInfo.data.ProfileContentContract
import com.nullpointer.nullsiteadmin.models.personalInfo.wrapper.UpdateInfoProfileWrapper
import kotlinx.serialization.Serializable

@Serializable
data class PersonalInfoDTO(
    val name: String?,
    val urlImg: String?,
    val profession: String?,
    val description: String?,
    val socialLinks: List<ProfileSocialLink>? = null,
    val translations: Map<String, Map<String, String>>? = null,
): MappableFirebase {

    override fun toUpdateMap(): Map<String, Any> = buildMap {
        name?.let { put("name", it) }
        urlImg?.let { put("urlImg", it) }
        profession?.let { put("profession", it) }
        description?.let { put("description", it) }
        putAll(ProfileContentContract.patch(socialLinks, translations))
        put(Constants.UPDATED_AT, FieldValue.serverTimestamp())
    }

    override fun toCreateMap(): Map<String, Any> = toUpdateMap().toMutableMap().apply {
        // set() needs nested maps; dotted paths only have update() semantics.
        keys.filter { it.startsWith("translations.") }.toList().forEach(::remove)
        translations?.let { data ->
            put("translations", ProfileContentContract.locales.associateWith { locale ->
                ProfileContentContract.translatedFields.associateWith { field -> data[locale]?.get(field).orEmpty().trim() }
            })
        }
        put(Constants.CREATED_AT, FieldValue.serverTimestamp())
    }

   companion object{
       fun fromPersonalInfoWrapper(
           urlImg:String?,
           infoProfileWrapper: UpdateInfoProfileWrapper,
       ): PersonalInfoDTO {
            return PersonalInfoDTO(
                urlImg = urlImg,
                name = infoProfileWrapper.name,
                profession = infoProfileWrapper.profession,
                description = infoProfileWrapper.description,
                socialLinks = infoProfileWrapper.socialLinks,
                translations = infoProfileWrapper.translations,
            )
       }
   }
}
