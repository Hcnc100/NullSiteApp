package com.nullpointer.nullsiteadmin.models.dto

import com.nullpointer.nullsiteadmin.interfaces.MappableFirebase
import com.nullpointer.nullsiteadmin.models.data.InfoPhoneData

data class UpdateInfoPhoneDTO(
    val tokenGCM:String,
    val modelPhone:String,
    val operativeSystem:String,
    val versionOperativeSystem: String,
):MappableFirebase {
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
