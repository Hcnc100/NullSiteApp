package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.presentation.ProjectViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditProjectScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel.EditProjectViewModel
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists.ListEmptyProject
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists.ListErrorProject
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists.ListLoadProject
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists.ListProjectSuccess
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun ProjectScreen(
    actionRootDestinations: ActionRootDestinations,
    projectVM: ProjectViewModel = shareViewModel(),
    editProjectVM: EditProjectViewModel = shareViewModel(),
    projectScreenState: SimpleScreenState = rememberSimpleScreenState()
) {

    val stateListProject by projectVM.listProject.collectAsState()

    LaunchedEffect(key1 = Unit) {
        projectVM.messageErrorProject.collect(projectScreenState::showSnackMessage)
    }

    ProjectScreen(
        stateListProject = stateListProject,
        isConcatenate = projectVM.isConcatenateProjects,
        scaffoldState = projectScreenState.scaffoldState,
        actionConcatenateProject = projectVM::concatenateProject,
        actionEditProject = { project ->
            editProjectVM.initVM(project)
            actionRootDestinations.changeRoot(EditProjectScreenDestination)
        }
    )
}

@Composable
private fun ProjectScreen(
    isConcatenate: Boolean,
    scaffoldState: ScaffoldState,
    actionConcatenateProject: () -> Unit,
    stateListProject: Resource<List<Project>>,
    actionEditProject: (project: Project) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState
    ) {
        when (stateListProject) {
            Resource.Loading -> ListLoadProject(modifier = Modifier.padding(it))
            Resource.Failure -> ListErrorProject(modifier = Modifier.padding(it))
            is Resource.Success -> {
                if (stateListProject.data.isEmpty()) {
                    ListEmptyProject(modifier = Modifier.padding(it))
                } else {
                    ListProjectSuccess(
                        isConcatenate = isConcatenate,
                        modifier = Modifier.padding(it),
                        listProject = stateListProject.data,
                        actionEditProject = actionEditProject,
                        concatenateProject = actionConcatenateProject
                    )
                }
            }
        }
    }
}
