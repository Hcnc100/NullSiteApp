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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListProjects(projects: List<Project>)

    @Update
    suspend fun updateProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :idProject")
    suspend fun deleterProjectById(idProject: String)

    @Query("DELETE  FROM projects WHERE id IN (:listIds)")
    suspend fun deleterListProjectById(listIds: List<String>)

    @Query("SELECT * FROM projects ORDER BY lastUpdate DESC LIMIT 1")
    suspend fun getMoreRecentProject(): Project?

    @Query("SELECT * FROM projects ORDER BY lastUpdate ASC LIMIT 1")
    suspend fun getLastProject(): Project?

}