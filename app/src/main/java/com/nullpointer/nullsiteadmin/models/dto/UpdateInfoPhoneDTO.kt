package com.nullpointer.nullsiteadmin.models.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.data.InfoPhoneData
import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfoPhoneDTO(
    val tokenGCM:String,
    val uuidPhone:String,
    val modelPhone:String,
    val versionNameApp:String,
    val operativeSystem:String,
    val versionNumberApp:String,
): MappableFirebase {
    companion object {
        fun fromInfoPhoneData(
            infoPhoneData: InfoPhoneData,
        ): UpdateInfoPhoneDTO {



            return UpdateInfoPhoneDTO(
                tokenGCM = infoPhoneData.tokenGCM,
                uuidPhone = infoPhoneData.uuidPhone,
                modelPhone = infoPhoneData.modelPhone,
                versionNameApp = infoPhoneData.versionNameApp,
                operativeSystem = infoPhoneData.operativeSystem,
                versionNumberApp = infoPhoneData.versionNumberApp,
            )
        }
    }

}


