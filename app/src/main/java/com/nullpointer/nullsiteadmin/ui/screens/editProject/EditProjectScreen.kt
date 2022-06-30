package com.nullpointer.nullsiteadmin.ui.screens.editProject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel.EditProjectViewModel
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun EditProjectScreen(
    project: Project,
    editProjectVM: EditProjectViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<Project>
) {
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = Unit) {
        editProjectVM.messageError.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    LaunchedEffect(key1 = Unit) {
        editProjectVM.initVM(project)
    }
    Scaffold(scaffoldState = scaffoldState, topBar = {
        ToolbarBack(
            title = stringResource(R.string.title_edit_project),
            actionBack = resultNavigator::navigateBack
        )
    }) { it ->
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageProject(urlImgProject = editProjectVM.urlImgProject.value)
            ListInfoProject(
                urlImgProject = editProjectVM.urlImgProject,
                nameProject = editProjectVM.nameProject,
                urlRepositoryProject = editProjectVM.urlRepositoryProject,
                descriptionProject = editProjectVM.descriptionProject,
                modifier = Modifier.padding(10.dp)
            )
            ButtonUpdateProject(isEnable = editProjectVM.isDataValid) {
                editProjectVM.getUpdatedProject()?.let {
                    resultNavigator.navigateBack(it)
                }
            }
        }
    }
}

@Composable
private fun ButtonUpdateProject(
    modifier: Modifier = Modifier,
    isEnable: Boolean,
    actionClick: () -> Unit
) {
    Button(
        onClick = actionClick,
        enabled = isEnable,
        modifier = modifier
    ) {
        Text(stringResource(R.string.text_update_project))
    }
}

@Composable
private fun ImageProject(
    urlImgProject: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = urlImgProject,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.8f)
    )
}

@Composable
private fun ListInfoProject(
    urlImgProject: PropertySavableString,
    nameProject: PropertySavableString,
    urlRepositoryProject: PropertySavableString,
    descriptionProject: PropertySavableString,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        EditableTextSavable(valueProperty = urlImgProject)
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = nameProject)
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = urlRepositoryProject)
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = descriptionProject)
    }
}