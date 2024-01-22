package com.nullpointer.nullsiteadmin.database

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

    @Query("DELETE FROM projects WHERE idProject = :idProject")
    suspend fun deleterProjectById(idProject: String)

    @Query("DELETE  FROM projects WHERE idProject IN (:listIds)")
    suspend fun deleterListProjectById(listIds: List<String>)

    @Query("SELECT * FROM projects ORDER BY lastUpdate DESC LIMIT 1")
    suspend fun getMoreRecentProject(): Project?

    @Query("SELECT * FROM projects ORDER BY lastUpdate ASC LIMIT 1")
    suspend fun getLastProject(): Project?

    @Query("DELETE FROM projects")
    suspend fun deleterAllProjects()

    @Transaction
    suspend fun updateAllProjects(listProjects: List<Project>) {
        deleterAllProjects()
        insertListProjects(listProjects)
    }
}