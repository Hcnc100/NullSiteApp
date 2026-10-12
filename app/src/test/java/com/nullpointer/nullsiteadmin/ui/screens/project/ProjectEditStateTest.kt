package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.lifecycle.SavedStateHandle
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.wrapper.CreateProjectWrapper
import com.nullpointer.nullsiteadmin.models.project.wrapper.UpdateProjectWrapper
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel.EditProjectViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProjectEditStateTest {
    private val original = ProjectData(idProject = "id", name = "Original", description = "Description",
        urlImg = "https://image", urlRepo = "https://repo")

    @Test fun later_initialization_does_not_reset_an_open_draft() {
        val state = SavedStateHandle()
        val vm = EditProjectViewModel(state, UnusedRepository)
        vm.initVM(original)
        vm.nameProject.changeValue("Draft")
        val originalBaseline: String? = state["PROJECT_EDIT_BASELINE"]

        vm.initVM(original.copy(name = "Remote"))

        assertEquals("Draft", vm.nameProject.currentValue)
        assertEquals(originalBaseline, state.get<String>("PROJECT_EDIT_BASELINE"))
        assertTrue(vm.nameProject.hasChanged)
    }

    @Test fun restored_draft_keeps_its_original_baseline_and_changed_flags() {
        val state = SavedStateHandle()
        val first = EditProjectViewModel(state, UnusedRepository)
        first.initVM(original)
        first.nameProject.changeValue("Draft")
        val baseline: String? = state["PROJECT_EDIT_BASELINE"]
        val keys = state.keys().associateWith { state.get<Any?>(it) }

        val restoredState = SavedStateHandle(keys)
        val restored = EditProjectViewModel(restoredState, UnusedRepository)
        restored.initVM(original.copy(name = "Remote"))

        assertEquals("Draft", restored.nameProject.currentValue)
        assertEquals(baseline, restoredState.get<String>("PROJECT_EDIT_BASELINE"))
        assertTrue(restored.nameProject.hasChanged)
        assertFalse(restored.descriptionProject.hasChanged)
    }

    @Test fun web_sized_project_and_empty_optional_repository_validate() {
        val vm = EditProjectViewModel(SavedStateHandle(), UnusedRepository)
        vm.initVM(original)
        vm.nameProject.changeValue("n".repeat(120))
        vm.descriptionProject.changeValue("d".repeat(10_000))
        vm.urlRepositoryProject.changeValue("")
        assertTrue(vm.isDataValid)

        vm.nameProject.changeValue("n".repeat(121))
        assertFalse(vm.isDataValid)
        vm.nameProject.changeValue("Valid")
        vm.descriptionProject.changeValue("d".repeat(10_001))
        assertFalse(vm.isDataValid)
    }

    private object UnusedRepository : ProjectRepository {
        override val listProjects = flowOf(emptyList<ProjectData>())
        override suspend fun editProject(updateProjectWrapper: UpdateProjectWrapper) = error("Not used")
        override suspend fun insertProject(createProjectWrapper: CreateProjectWrapper) = error("Not used")
        override suspend fun deleterListProjectById(listIdProject: List<String>) = error("Not used")
        override suspend fun deleterProjectById(idProject: String) = error("Not used")
        override suspend fun requestLastProject(): Int = error("Not used")
        override suspend fun concatenateProjects(): Int = error("Not used")
    }
}
