package com.nullpointer.nullsiteadmin.models.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.data.InfoPhoneData
import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfoPhoneDTO(
    val tokenGCM:String,
    val modelPhone:String,
    val operativeSystem:String,
    val versionOperativeSystem: String,
): MappableFirebase {
    companion object {
        fun fromInfoPhoneData(
            infoPhoneData: InfoPhoneData,
        ): UpdateInfoPhoneDTO {



            return UpdateInfoPhoneDTO(
                modelPhone = infoPhoneData.modelPhone,
                tokenGCM = infoPhoneData.tokenGCM,
                operativeSystem = infoPhoneData.operativeSystem,
                versionOperativeSystem = infoPhoneData.versionOperativeSystem
            )
        }
    }

}


