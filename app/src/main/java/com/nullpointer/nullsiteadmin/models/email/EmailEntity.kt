package com.nullpointer.nullsiteadmin.models.email

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val message: String = "",
    val email: String = "",
    val subject: String = "",
    val timestamp: Long = 0,
    val isOpen: Boolean = false
){
    companion object{
        fun fromEmailData(emailData: EmailData):EmailEntity{
            return EmailEntity(
                id = emailData.idEmail,
                name = emailData.name,
                message = emailData.message,
                email = emailData.email,
                subject = emailData.subject,
                isOpen = emailData.isOpen,
                timestamp = emailData.timestamp?.time ?: 0
            )
        }
    }
}
