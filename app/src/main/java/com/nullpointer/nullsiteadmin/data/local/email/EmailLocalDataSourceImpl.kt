package com.nullpointer.nullsiteadmin.data.local.email

import com.nullpointer.nullsiteadmin.data.local.room.EmailDao
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

class EmailLocalDataSourceImpl(
    private val emailDao: EmailDao
) : EmailLocalDataSource {
    override val listEmail: Flow<List<EmailContact>> = emailDao.getListEmails()

    override suspend fun updateAllEmails(listEmails: List<EmailContact>) =
        emailDao.updateAllEmails(listEmails)

    override suspend fun deleteListEmails(listEmails: List<EmailContact>) =
        emailDao.deleterListEmails(listEmails)

    override suspend fun deleteEmail(email: EmailContact) =
        emailDao.deleterEmail(email)

    override suspend fun deleterAllEmails() =
        emailDao.deleterAllEmails()
}