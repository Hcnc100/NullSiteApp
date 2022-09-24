package com.nullpointer.nullsiteadmin.data.local.room

import androidx.room.*
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDAO {

    @Query("SELECT * FROM emails ORDER by timestamp DESC")
    fun getListEmails(): Flow<List<EmailContact>>

    @Query("SELECT * FROM emails WHERE idEmail = :idEmail LIMIT 1")
    suspend fun getEmailById(idEmail: String): EmailContact?

    @Query("SELECT * FROM emails ORDER by timestamp DESC LIMIT 1")
    suspend fun getMoreRecentEmail(): EmailContact?

    @Query("SELECT * FROM emails ORDER by timestamp ASC LIMIT 1")
    suspend fun getLastEmail(): EmailContact?

    @Query("DELETE FROM emails WHERE idEmail = :idEmail")
    suspend fun deleterEmailById(idEmail: String)

    @Query("DELETE FROM emails WHERE idEmail in (:listIdEmails)")
    suspend fun deleterListEmails(listIdEmails: List<String>)

    @Query("DELETE FROM emails")
    suspend fun deleterAllEmails()

    @Update
    suspend fun updateEmail(emailContact: EmailContact)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListEmails(listEmails: List<EmailContact>)

    suspend fun updateAllEmails(listEmails: List<EmailContact>) {
        deleterAllEmails()
        insertListEmails(listEmails)
    }
}