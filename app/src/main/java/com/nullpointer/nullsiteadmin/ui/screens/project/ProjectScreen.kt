package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
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

    val stateListProject by projectVM.listProjectData.collectAsState()

    LaunchedEffect(key1 = Unit) {
        projectVM.messageErrorProject.collect(projectScreenState::showSnackMessage)
    }

    ProjectScreen(stateListProjectData = stateListProject,
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
    stateListProjectData: Resource<List<ProjectData>>,
    actionEditProject: (projectData: ProjectData) -> Unit,
    actionRefreshProject: () -> Unit,
    swipeRefreshState: SwipeRefreshState
) {
    ScaffoldSwipeRefresh(
        actionOnRefresh = actionRefreshProject,
        scaffoldState = scaffoldState,
        swipeRefreshState = swipeRefreshState
    ) {
        when (stateListProjectData) {
            Resource.Loading -> ListLoadProject()
            Resource.Failure -> ListErrorProject()
            is Resource.Success -> {
                if (stateListProjectData.data.isEmpty()) {
                    ListEmptyProject()
                } else {
                    ListProjectSuccess(
                        isConcatenate = isConcatenate,
                        listProjectData = stateListProjectData.data,
                        actionEditProject = actionEditProject,
                        concatenateProject = actionConcatenateProject
                    )
                }
            }
        }
    }
}
