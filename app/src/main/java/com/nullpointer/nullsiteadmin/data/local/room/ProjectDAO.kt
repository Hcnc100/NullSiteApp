package com.nullpointer.nullsiteadmin.data.local.room

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

interface ProjectDAO {

    @Query("SELECT * FROM projects ORDER BY lastUpdate DESC")
    fun getListProjects(): Flow<List<Project>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)
}