package com.nullpointer.nullsiteadmin.models.email.data

import com.nullpointer.nullsiteadmin.database.DateAsLongSerializer
import com.nullpointer.nullsiteadmin.models.email.entity.EmailEntity
import kotlinx.serialization.Serializable
import java.util.Date


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

        val exampleClose = EmailData(
            idEmail = "1",
            name = "Name",
            message = "Message",
            email = "example@email.com",
            subject = "Subject",
            createdAt = Date(),
            updatedAt = Date(),
            isOpen = false
        )
        val exampleOpen = exampleClose.copy(
            isOpen = true,
            idEmail = "2"
        )

        val exampleList = listOf(
            exampleClose,
            exampleOpen,
            exampleClose.copy(idEmail = "3"),
            exampleOpen.copy(idEmail = "4"),
            exampleClose.copy(idEmail = "5"),
            exampleOpen.copy(idEmail = "6"),
            exampleClose.copy(idEmail = "7"),
            exampleOpen.copy(idEmail = "8"),
            exampleClose.copy(idEmail = "9"),
            exampleOpen.copy(idEmail = "10"),
        )

        fun fromEmailEntity(emailEntity: EmailEntity): EmailData {
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
