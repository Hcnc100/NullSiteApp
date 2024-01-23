package com.nullpointer.nullsiteadmin.data.project.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nullpointer.nullsiteadmin.models.project.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDAO {

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getListProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(projectEntity: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListProjects(projectData: List<ProjectEntity>)

    @Update
    suspend fun updateProject(projectEntity: ProjectEntity)

    @Query("DELETE FROM projects WHERE idProject = :idProject")
    suspend fun deleterProjectById(idProject: String)

    @Query("DELETE  FROM projects WHERE idProject IN (:listIds)")
    suspend fun deleterListProjectById(listIds: List<String>)

    @Query("SELECT * FROM projects ORDER BY createdAt DESC LIMIT 1")
    suspend fun getMoreRecentProject(): ProjectEntity?

    @Query("SELECT * FROM projects ORDER BY createdAt ASC LIMIT 1")
    suspend fun getLastProject(): ProjectEntity?

    @Query("DELETE FROM projects")
    suspend fun deleterAllProjects()

    @Transaction
    suspend fun updateAllProjects(projectEntityList: List<ProjectEntity>) {
        deleterAllProjects()
        insertListProjects(projectEntityList)
    }
}