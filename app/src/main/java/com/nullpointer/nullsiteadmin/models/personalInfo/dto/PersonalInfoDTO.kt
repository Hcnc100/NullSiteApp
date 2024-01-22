package com.nullpointer.nullsiteadmin.models.personalInfo.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.personalInfo.wrapper.UpdateInfoProfileWrapper
import kotlinx.serialization.Serializable

@Serializable
data class PersonalInfoDTO(
    val name: String?,
    val urlImg: String?,
    val profession: String?,
    val description: String?,
): MappableFirebase {

   companion object{
       fun fromPersonalInfoWrapper(
           urlImg:String?,
           infoProfileWrapper: UpdateInfoProfileWrapper,
       ): PersonalInfoDTO {
            return PersonalInfoDTO(
                urlImg = urlImg,
                name = infoProfileWrapper.name,
                profession = infoProfileWrapper.profession,
                description = infoProfileWrapper.description
            )
       }
   }
}
