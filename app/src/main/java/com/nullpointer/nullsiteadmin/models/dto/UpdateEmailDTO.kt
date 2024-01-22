package com.nullpointer.nullsiteadmin.models.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.email.EmailData

data class UpdateEmailDTO(
    val idEmail:String,
    val isOpen:Boolean,
): MappableFirebase {
    companion object{
        fun fromEmailData(
            emailData: EmailData
        ): UpdateEmailDTO {
            return UpdateEmailDTO(
                idEmail = emailData.idEmail,
                isOpen = true
            )
        }
    }
}
