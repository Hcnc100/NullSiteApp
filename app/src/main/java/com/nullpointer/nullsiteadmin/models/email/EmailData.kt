package com.nullpointer.nullsiteadmin.models.email

import com.nullpointer.nullsiteadmin.database.DateAsLongSerializer
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class EmailData(
    val idEmail: String = "",
    val name: String = "",
    val message: String = "",
    val email: String = "",
    val subject: String = "",
    @Serializable(with = DateAsLongSerializer::class)
    val createdAt: Date?= null,
    @Serializable(with = DateAsLongSerializer::class)
    val updatedAt: Date?= null,
    val isOpen: Boolean = false
){
    companion object{
        fun fromEmailEntity(emailEntity: EmailEntity):EmailData{
            return EmailData(
                idEmail = emailEntity.id,
                name = emailEntity.name,
                message = emailEntity.message,
                email = emailEntity.email,
                subject = emailEntity.subject,
                isOpen = emailEntity.isOpen,
                createdAt = emailEntity.createAt,
                updatedAt = emailEntity.updateAt
            )
        }
    }
}
