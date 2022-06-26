package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.presentation.ProjectViewModel
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen

@Composable
fun ProjectScreen(
    projectVM: ProjectViewModel = hiltViewModel()
) {
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
                textEmpty = "Error load project"
            )
            Resource.Loading -> {}
            is Resource.Success -> ListProjects(listProject = listProject.data)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListProjects(
    listProject: List<Project>
) {
    LazyColumn {
        items(
            count = listProject.size,
            key = { listProject[it].id }
        ) { index ->
            ProjectItem(
                project = listProject[index],
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}