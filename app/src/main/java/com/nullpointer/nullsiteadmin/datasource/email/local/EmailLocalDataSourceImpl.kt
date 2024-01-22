package com.nullpointer.nullsiteadmin.datasource.email.local

import com.nullpointer.nullsiteadmin.data.email.local.EmailDAO
import com.nullpointer.nullsiteadmin.models.email.EmailData
import com.nullpointer.nullsiteadmin.models.email.EmailEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmailLocalDataSourceImpl(
    private val emailDao: EmailDAO
) : EmailLocalDataSource {
    override val listEmail: Flow<List<EmailData>> = emailDao.getListEmails().map { listEmail ->
        listEmail.map(
            EmailData::fromEmailEntity
        )
    }

    override suspend fun updateAllEmails(listIdEmails: List<EmailData>){

        val listEmailEntity = listIdEmails.map(EmailEntity::fromEmailData)

        emailDao.updateAllEmails(listEmailEntity)
    }


    override suspend fun deleteListEmails(listIdEmails: List<String>) =
        emailDao.deleterListEmails(listIdEmails)

    override suspend fun getEmailById(idEmail: String): EmailData?{
        return emailDao.getEmailById(idEmail)?.let {
            EmailData.fromEmailEntity(it)
        }
    }

    override suspend fun deleteEmail(idEmail: String) =
        emailDao.deleterEmailById(idEmail)

    override suspend fun updateEmail(email: EmailData){
        val emailEntity = EmailEntity.fromEmailData(email)
        emailDao.updateEmail(emailEntity)
    }

    override suspend fun deleterAllEmails() =
        emailDao.deleterAllEmails()

    override suspend fun getMoreRecentEmail(): EmailData?{
        return emailDao.getMoreRecentEmail()?.let {
            EmailData.fromEmailEntity(it)
        }
    }

    override suspend fun getLastEmail(): EmailData? {
        return emailDao.getLastEmail()?.let {
            EmailData.fromEmailEntity(it)
        }
    }

    override suspend fun insertListEmails(listEmails: List<EmailData>){
        val listEmailEntity = listEmails.map(EmailEntity::fromEmailData)
        emailDao.insertListEmails(listEmailEntity)
    }
}