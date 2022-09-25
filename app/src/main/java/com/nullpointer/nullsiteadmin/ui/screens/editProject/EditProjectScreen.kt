package com.nullpointer.nullsiteadmin.ui.screens.editProject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.presentation.ProjectViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel.EditProjectViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.FocusScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberFocusScreenState
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph
@Destination
@Composable
fun EditProjectScreen(
    actionRootDestinations: ActionRootDestinations,
    projectVM: ProjectViewModel = shareViewModel(),
    editProjectVM: EditProjectViewModel = shareViewModel(),
    editProjectState: FocusScreenState = rememberFocusScreenState()
) {

    LaunchedEffect(key1 = Unit) {
        editProjectVM.messageError.collect(editProjectState::showSnackMessage)
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
            ImageProject(urlImgProject = editProjectVM.urlImgProject.currentValue)
            ListInfoProject(
                urlImgProject = editProjectVM.urlImgProject,
                nameProject = editProjectVM.nameProject,
                urlRepositoryProject = editProjectVM.urlRepositoryProject,
                descriptionProject = editProjectVM.descriptionProject,
                modifier = Modifier.padding(10.dp),
                hiddenKeyBoard = editProjectState::hiddenKeyBoard
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
    hiddenKeyBoard: () -> Unit,
    modifier: Modifier = Modifier,
    nameProject: PropertySavableString,
    urlImgProject: PropertySavableString,
    descriptionProject: PropertySavableString,
    urlRepositoryProject: PropertySavableString
) {
    Column(modifier = modifier) {
        EditableTextSavable(
            valueProperty = urlImgProject,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() })
        )
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = nameProject,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() })
        )
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = urlRepositoryProject,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() })
        )
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = descriptionProject)
    }
}