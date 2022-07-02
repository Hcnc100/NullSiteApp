package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.presentation.ProjectViewModel
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.nullpointer.nullsiteadmin.ui.screens.animation.DetailsTransition
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditProjectScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient

@Destination(
    style = DetailsTransition::class
)
@Composable
fun ProjectScreen(
    projectVM: ProjectViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<EditProjectScreenDestination, Project>
) {
    resultRecipient.onResult(listener = projectVM::editProject)
    val stateListProject by projectVM.listProject.collectAsState()
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = Unit) {
        projectVM.messageErrorProject.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    Scaffold(
        scaffoldState = scaffoldState
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
                    navigator.navigate(EditProjectScreenDestination.invoke(project))
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