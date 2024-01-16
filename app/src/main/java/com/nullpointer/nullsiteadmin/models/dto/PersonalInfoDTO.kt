package com.nullpointer.nullsiteadmin.models.dto

import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.nullsiteadmin.interfaces.MappableFirebase
import com.nullpointer.nullsiteadmin.models.wrapper.InfoProfileWrapper
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import java.util.Date

data class PersonalInfoDTO(
    val name: String?,
    val urlImg: String?,
    val profession: String?,
    val description: String?,
):MappableFirebase{

   companion object{
       fun fromPersonalInfoWrapper(infoProfileWrapper: InfoProfileWrapper): PersonalInfoDTO {
            return PersonalInfoDTO(
                name = infoProfileWrapper.name,
                urlImg = infoProfileWrapper.description,
                profession = infoProfileWrapper.profession,
                description = infoProfileWrapper.description
            )
       }
   }
}
