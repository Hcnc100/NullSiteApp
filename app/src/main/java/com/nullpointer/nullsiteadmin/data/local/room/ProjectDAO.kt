package com.nullpointer.nullsiteadmin.data.local.room

import androidx.room.*
import com.nullpointer.nullsiteadmin.models.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDAO {

    @Query("SELECT * FROM projects ORDER BY lastUpdate DESC")
    fun getListProjects(): Flow<List<Project>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)
}