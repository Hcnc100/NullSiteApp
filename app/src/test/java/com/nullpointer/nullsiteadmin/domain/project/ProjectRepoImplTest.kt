package com.nullpointer.nullsiteadmin.domain.project

import com.nullpointer.nullsiteadmin.datasource.project.local.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.project.remote.ProjectRemoteDataSource
import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.dto.CreateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.dto.UpdateProjectDTO
import com.nullpointer.nullsiteadmin.models.project.wrapper.UpdateProjectWrapper
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class ProjectRepoImplTest {
    private val original = ProjectData(idProject = "id", name = "Original")
    private val wrapper get() = UpdateProjectWrapper("id", "Edited", null, null, null, null, original)

    @Test fun original_baseline_is_forwarded_and_cache_updates_only_after_success() = runTest {
        val local = FakeLocal()
        val remote = FakeRemote()
        ProjectRepoImpl(local, remote).editProject(wrapper)
        assertSame(original, remote.receivedBaseline)
        assertEquals("Edited", remote.receivedDTO?.name)
        assertEquals(remote.saved, local.updated)
    }

    @Test fun conflict_preserves_cache_and_propagates_to_the_editor() = runTest {
        val local = FakeLocal()
        val remote = FakeRemote().apply { conflict = true }
        try {
            ProjectRepoImpl(local, remote).editProject(wrapper)
            fail("Expected conflict")
        } catch (error: NullAppException.ContentConflict) {
            assertEquals(null, local.updated)
            assertSame(original, remote.receivedBaseline)
        }
    }

    private class FakeRemote : ProjectRemoteDataSource {
        var receivedBaseline: ProjectData? = null
        var receivedDTO: UpdateProjectDTO? = null
        var conflict = false
        val saved = ProjectData(idProject = "id", name = "Edited")
        override suspend fun editProject(idProject: String, updateProjectDTO: UpdateProjectDTO, baseline: ProjectData): ProjectData {
            receivedBaseline = baseline
            receivedDTO = updateProjectDTO
            if (conflict) throw NullAppException.ContentConflict
            return saved
        }
        override suspend fun addNewProject(createProjectDTO: CreateProjectDTO) = saved
        override suspend fun deleterProject(idProject: String) = Unit
        override suspend fun deleterListProjectById(listIdsProject: List<String>) = Unit
        override suspend fun getLastProjects(numberResult: Long) = emptyList<ProjectData>()
        override suspend fun concatenateProject(startWithId: String, numberResult: Long) = emptyList<ProjectData>()
    }

    private class FakeLocal : ProjectLocalDataSource {
        var updated: ProjectData? = null
        override val listProjectData = flowOf(emptyList<ProjectData>())
        override suspend fun updateProject(projectData: ProjectData) { updated = projectData }
        override suspend fun insertProject(projectData: ProjectData) = Unit
        override suspend fun insertListProjects(listProjectData: List<ProjectData>) = Unit
        override suspend fun getMoreRecentProject(): ProjectData? = null
        override suspend fun getLastProject(): ProjectData? = null
        override suspend fun updateAllProjects(listProjectData: List<ProjectData>) = Unit
        override suspend fun deleteProjectById(idProject: String) = Unit
        override suspend fun deleteListProjectById(listIdsProject: List<String>) = Unit
        override suspend fun deleterAllProjects() = Unit
    }
}
