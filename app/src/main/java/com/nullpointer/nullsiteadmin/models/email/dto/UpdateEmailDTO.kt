package com.nullpointer.nullsiteadmin.models.email.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase
import com.nullpointer.nullsiteadmin.models.email.data.EmailData

data class UpdateEmailDTO(
    val isOpen:Boolean,
): MappableFirebase {
    companion object{
        fun fromEmailData(
            emailData: EmailData
        ): UpdateEmailDTO {
            return UpdateEmailDTO(
                isOpen = true
            )
        }
    }
}
