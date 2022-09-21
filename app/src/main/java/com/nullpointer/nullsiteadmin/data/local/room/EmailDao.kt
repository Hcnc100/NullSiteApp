package com.nullpointer.nullsiteadmin.data.local.room

import androidx.room.*
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {

    @Query("SELECT * FROM emails")
    fun getListEmails(): Flow<List<EmailContact>>

    @Delete
    suspend fun deleterEmail(emailContact: EmailContact)

    @Delete
    suspend fun deleterListEmails(listEmails: List<EmailContact>)

    @Query("DELETE FROM emails")
    suspend fun deleterAllEmails()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmail(emailContact: EmailContact)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListEmails(listEmails: List<EmailContact>)

    suspend fun updateAllEmails(listEmails: List<EmailContact>) {
        deleterAllEmails()
        insertListEmails(listEmails)
    }
}