package com.nullpointer.nullsiteadmin.models.email.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import java.util.Date


@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val message: String,
    val email: String,
    val subject: String,
    val createAt:Date,
    val updateAt:Date,
    val isOpen: Boolean
){
    companion object{
        fun fromEmailData(emailData: EmailData): EmailEntity {
            return EmailEntity(
                id = emailData.idEmail,
                name = emailData.name,
                message = emailData.message,
                email = emailData.email,
                subject = emailData.subject,
                isOpen = emailData.isOpen,
                createAt = Date(emailData.createdAt!!.time),
                updateAt = Date(emailData.updatedAt!!.time)
            )
        }
    }
}
