package com.nullpointer.nullsiteadmin.ui.screens.editProject

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import com.nullpointer.nullsiteadmin.presentation.ProjectViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel.EditProjectViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph
@Destination
@Composable
fun EditProjectScreen(
    project: Project,
    actionRootDestinations: ActionRootDestinations,
    editProjectState: SimpleScreenState = rememberSimpleScreenState(),
    editProjectVM: EditProjectViewModel = hiltViewModel(),
    projectVM: ProjectViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {

    LaunchedEffect(key1 = Unit, editProjectState) {
        editProjectVM.messageError.collect(editProjectState::showSnackMessage)
    }
    LaunchedEffect(key1 = Unit) {
        editProjectVM.initVM(project)
    }
    Scaffold(
        scaffoldState = editProjectState.scaffoldState,
        topBar = {
            ToolbarBack(
                title = stringResource(id = R.string.title_edit_project),
                actionBack = actionRootDestinations::backDestination
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
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
                    editProjectState.hiddenKeyBoard()
                    projectVM.editProject(it)
                    actionRootDestinations.backDestination()
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
        contentDescription = stringResource(R.string.description_current_img_project),
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