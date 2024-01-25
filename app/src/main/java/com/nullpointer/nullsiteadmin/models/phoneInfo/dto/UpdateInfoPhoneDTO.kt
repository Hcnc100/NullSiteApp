package com.nullpointer.nullsiteadmin.models.phoneInfo.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.phoneInfo.data.InfoPhoneData
import kotlinx.serialization.Serializable

@Serializable
data class UpdateInfoPhoneDTO(
    val idUser: String,
    val tokenGCM: String,
    val uuidPhone: String,
    val modelPhone: String,
    val versionNameApp: String,
    val operativeSystem: String,
    val versionNumberApp: String,
): MappableFirebase {
    companion object {
        fun fromInfoPhoneData(
            infoPhoneData: InfoPhoneData,
            idUser: String
        ): UpdateInfoPhoneDTO {



            return UpdateInfoPhoneDTO(
                idUser = idUser,
                tokenGCM = infoPhoneData.tokenGCM,
                uuidPhone = infoPhoneData.uuidPhone,
                modelPhone = infoPhoneData.modelPhone,
                versionNameApp = infoPhoneData.versionNameApp,
                operativeSystem = infoPhoneData.operativeSystem,
                versionNumberApp = infoPhoneData.versionNumberApp
            )
        }
    }

}


