package com.nullpointer.nullsiteadmin.models.dto

import com.nullpointer.nullsiteadmin.core.utils.MappableFirebase

data class UpdateEmailDTO(
    val idEmail:String,
    val isOpen:Boolean,
): MappableFirebase {
    companion object{
        fun fromEmailData(
            idEmail:String,
        ): UpdateEmailDTO {
            return UpdateEmailDTO(
                idEmail = idEmail,
                isOpen = true
            )
        }
    }
}
