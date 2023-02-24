package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.presentation.ProjectViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditProjectScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists.ListEmptyProject
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists.ListErrorProject
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists.ListLoadProject
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists.ListProjectSuccess
import com.nullpointer.nullsiteadmin.ui.screens.states.LazySwipeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberLazySwipeScreenState
import com.nullpointer.nullsiteadmin.ui.share.ScaffoldSwipeRefresh
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun ProjectScreen(
    actionRootDestinations: ActionRootDestinations,
    projectVM: ProjectViewModel = hiltViewModel(),
    projectScreenState: LazySwipeScreenState = rememberLazySwipeScreenState(
        sizeScroll = 250F, isRefreshing = projectVM.isRequestProject
    )
) {

    val stateListProject by projectVM.listProject.collectAsState()

    LaunchedEffect(key1 = Unit) {
        projectVM.messageErrorProject.collect(projectScreenState::showSnackMessage)
    }

    ProjectScreen(stateListProject = stateListProject,
        isConcatenate = projectVM.isConcatenateProjects,
        scaffoldState = projectScreenState.scaffoldState,
        actionRefreshProject = projectVM::requestNewProjects,
        swipeRefreshState = projectScreenState.swipeRefreshState,
        actionConcatenateProject = { projectVM.concatenateProject(projectScreenState::scrollToMore) },
        actionEditProject = { project ->
            actionRootDestinations.changeRoot(EditProjectScreenDestination(project))
        })
}

@Composable
private fun ProjectScreen(
    isConcatenate: Boolean,
    scaffoldState: ScaffoldState,
    actionConcatenateProject: () -> Unit,
    stateListProject: Resource<List<Project>>,
    actionEditProject: (project: Project) -> Unit,
    actionRefreshProject: () -> Unit,
    swipeRefreshState: SwipeRefreshState
) {
    ScaffoldSwipeRefresh(
        actionOnRefresh = actionRefreshProject,
        scaffoldState = scaffoldState,
        swipeRefreshState = swipeRefreshState
    ) {
        when (stateListProject) {
            Resource.Loading -> ListLoadProject()
            Resource.Failure -> ListErrorProject()
            is Resource.Success -> {
                if (stateListProject.data.isEmpty()) {
                    ListEmptyProject()
                } else {
                    ListProjectSuccess(
                        isConcatenate = isConcatenate,
                        listProject = stateListProject.data,
                        actionEditProject = actionEditProject,
                        concatenateProject = actionConcatenateProject
                    )
                }
            }
        }
    }
}
