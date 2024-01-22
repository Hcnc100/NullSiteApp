package com.nullpointer.nullsiteadmin.data.email.local

import androidx.room.*
import com.nullpointer.nullsiteadmin.models.email.EmailData
import com.nullpointer.nullsiteadmin.models.email.EmailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDAO {

    @Query("SELECT * FROM emails ORDER by createAt DESC")
    fun getListEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE id = :idEmail LIMIT 1")
    suspend fun getEmailById(idEmail: String): EmailEntity?

    @Query("SELECT * FROM emails ORDER by createAt DESC LIMIT 1")
    suspend fun getMoreRecentEmail(): EmailEntity?

    @Query("SELECT * FROM emails ORDER by createAt ASC LIMIT 1")
    suspend fun getLastEmail(): EmailEntity?

    @Query("DELETE FROM emails WHERE id = :idEmail")
    suspend fun deleterEmailById(idEmail: String)

    @Query("DELETE FROM emails WHERE id in (:listIdEmails)")
    suspend fun deleterListEmails(listIdEmails: List<String>)

    @Query("DELETE FROM emails")
    suspend fun deleterAllEmails()

    @Update
    suspend fun updateEmail(emailContact: EmailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListEmails(listEmails: List<EmailEntity>)

    @Transaction
    suspend fun updateAllEmails(listEmails: List<EmailEntity>) {
        deleterAllEmails()
        insertListEmails(listEmails)
    }
}