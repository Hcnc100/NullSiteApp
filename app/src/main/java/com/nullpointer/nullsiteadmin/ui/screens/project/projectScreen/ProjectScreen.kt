package com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.preview.provider.ListProjectProvider
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditProjectScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.componets.lists.ListEmptyProject
import com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.componets.lists.ListErrorProject
import com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.componets.lists.ListProjectSuccess
import com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.viewModel.ProjectViewModel
import com.nullpointer.nullsiteadmin.ui.screens.shared.BlockProgress
import com.nullpointer.nullsiteadmin.ui.screens.states.LazyGridSwipeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberLazyGridSwipeScreenState
import com.ramcosta.composedestinations.annotation.Destination


@Destination
@Composable
fun ProjectScreen(
    actionRootDestinations: ActionRootDestinations,
    projectViewModel: ProjectViewModel = hiltViewModel(),
    projectScreenState: LazyGridSwipeScreenState = rememberLazyGridSwipeScreenState(
        sizeScroll = 250F,
        isRefreshing = projectViewModel.isRequestProject,
        onRefresh = projectViewModel::requestNewProjects,
    )
) {

    val shouldLoadMore by remember {
        derivedStateOf {
            val visibleItems = projectScreenState.lazyGridState.layoutInfo.visibleItemsInfo
            val lastVisibleItemIndex =
                visibleItems.lastOrNull()?.index ?: return@derivedStateOf false
            lastVisibleItemIndex >= projectScreenState.lazyGridState.layoutInfo.totalItemsCount - 1
        }
    }


    LaunchedEffect(key1 = shouldLoadMore) {
        if (shouldLoadMore) {
            projectViewModel.concatenateProject()
        }
    }

    LaunchedEffect(key1 = Unit) {
        projectViewModel.messageErrorProject.collect(projectScreenState::showSnackMessage)
    }

    val stateListProject by projectViewModel.listProjectData.collectAsState()


    ProjectScreen(
        stateListProjectData = stateListProject,
        isLoading = projectViewModel.isRequestProject,
        isConcatenate = projectViewModel.isConcatenateProjects,
        scaffoldState = projectScreenState.scaffoldState,
        lazyGridState = projectScreenState.lazyGridState,
        pullRefreshState = projectScreenState.pullRefreshState,
        actionEditProject = { project ->
            actionRootDestinations.changeRoot(EditProjectScreenDestination(project))
        }
    )

}


@Composable
private fun ProjectScreen(
    isLoading: Boolean,
    isConcatenate: Boolean,
    scaffoldState: ScaffoldState,
    lazyGridState: LazyGridState,
    pullRefreshState: PullRefreshState,
    stateListProjectData: Resource<List<ProjectData>>,
    actionEditProject: (projectData: ProjectData) -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .then(
                    when (stateListProjectData) {
                        is Resource.Loading -> Modifier
                        else -> Modifier.pullRefresh(pullRefreshState)
                    }
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            when (stateListProjectData) {
                Resource.Loading -> BlockProgress()
                Resource.Failure -> ListErrorProject()
                is Resource.Success -> {
                    if (stateListProjectData.data.isEmpty()) {
                        ListEmptyProject()
                    } else {
                        ListProjectSuccess(
                            isConcatenate = isConcatenate,
                            lazyGridState = lazyGridState,
                            actionEditProject = actionEditProject,
                            listProjectData = stateListProjectData.data
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isLoading && stateListProjectData !is Resource.Loading,
                state = pullRefreshState
            )
        }
    }
}


@OrientationPreviews
@Composable
private fun ProjectScreenPreview(
    @PreviewParameter(ListProjectProvider::class)
    listProjectState: Resource<List<ProjectData>>
) {
    ProjectScreen(
        isLoading = false,
        isConcatenate = false,
        scaffoldState = rememberScaffoldState(),
        lazyGridState = rememberLazyGridState(),
        pullRefreshState = rememberPullRefreshState(refreshing = false, onRefresh = { /*TODO*/ }),
        stateListProjectData = listProjectState,
        actionEditProject = { /*TODO*/ }
    )
}