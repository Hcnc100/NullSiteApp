package com.nullpointer.nullsiteadmin.models.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.wrapper.InfoProfileWrapper
import kotlinx.serialization.Serializable

@Serializable
data class PersonalInfoDTO(
    val name: String?,
    val urlImg: String?,
    val profession: String?,
    val description: String?,
): MappableFirebase {

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
