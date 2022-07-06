package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.presentation.ProjectViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditProjectScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel.EditProjectViewModel
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
    Scaffold(
        scaffoldState = projectScreenState.scaffoldState
    ) {
        when (val listProject = stateListProject) {
            Resource.Failure -> AnimationScreen(
                animation = R.raw.error,
                textEmpty = stringResource(R.string.message_error_project),
                modifier = Modifier.padding(it)
            )
            Resource.Loading -> {}
            is Resource.Success -> ListProjects(
                listProject = listProject.data,
                modifier = Modifier.padding(it),
                actionEditProject = { project ->
                    editProjectVM.initVM(project)
                    actionRootDestinations.changeRoot(EditProjectScreenDestination)
                }
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListProjects(
    listProject: List<Project>,
    actionEditProject: (Project) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {
        items(
            count = listProject.size,
            key = { listProject[it].id }
        ) { index ->
            ProjectItem(
                project = listProject[index],
                modifier = Modifier.animateItemPlacement(),
                actionEditProject = actionEditProject
            )
        }
    }
}