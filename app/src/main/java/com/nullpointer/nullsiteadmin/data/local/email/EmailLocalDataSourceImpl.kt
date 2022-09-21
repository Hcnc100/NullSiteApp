package com.nullpointer.nullsiteadmin.data.local.email

import com.nullpointer.nullsiteadmin.data.local.room.EmailDAO
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

class EmailLocalDataSourceImpl(
    private val emailDao: EmailDAO
) : EmailLocalDataSource {
    override val listEmail: Flow<List<EmailContact>> = emailDao.getListEmails()

    override suspend fun updateAllEmails(listIdEmails: List<EmailContact>) =
        emailDao.updateAllEmails(listIdEmails)

    override suspend fun deleteListEmails(listIdEmails: List<String>) =
        emailDao.deleterListEmails(listIdEmails)

    override suspend fun getEmailById(idEmail: String) =
        emailDao.getEmailById(idEmail)

    override suspend fun deleteEmail(idEmail: String) =
        emailDao.deleterEmailById(idEmail)

    override suspend fun updateEmail(email: EmailContact) =
        emailDao.updateEmail(email)

    override suspend fun deleterAllEmails() =
        emailDao.deleterAllEmails()

    override suspend fun getMoreRecentEmail() =
        emailDao.getMoreRecentEmail()

    override suspend fun getLastEmail(): EmailContact? =
        emailDao.getLastEmail()

    override suspend fun insertListEmails(listEmails: List<EmailContact>) =
        emailDao.insertListEmails(listEmails)
}