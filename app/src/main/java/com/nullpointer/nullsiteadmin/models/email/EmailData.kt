package com.nullpointer.nullsiteadmin.models.email

import java.util.*


data class EmailData(
    val idEmail: String = "",
    val name: String = "",
    val message: String = "",
    val email: String = "",
    val subject: String = "",
    val createdAt: Date?= null,
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
                // ! TODO : Fix this
                createdAt = Date(),
                updatedAt = Date()
            )
        }
    }
}
