package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.actions.EditProjectAction
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ButtonUpdateProject
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ImageProjectEdit
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ListInfoProject
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel.EditProjectViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.FocusScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberFocusScreenState
import com.nullpointer.nullsiteadmin.ui.share.BlockProcessing
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph
@Destination
@Composable
fun EditProjectScreen(
    projectDataEdit: ProjectData,
    actionRootDestinations: ActionRootDestinations,
    editProjectVM: EditProjectViewModel = hiltViewModel(),
    editProjectState: FocusScreenState = rememberFocusScreenState()
) {

    LaunchedEffect(key1 = Unit) {
        editProjectVM.initVM(projectDataEdit)
    }

    LaunchedEffect(key1 = Unit) {
        editProjectVM.messageError.collect(editProjectState::showSnackMessage)
    }

    EditProjectScreen(
        scaffoldState = editProjectState.scaffoldState,
        isUpdatedProject = editProjectVM.isUpdatedProject,
        isDataValid = editProjectVM.isDataValid,
        urlImgProject = editProjectVM.urlImgProject,
        nameProject = editProjectVM.nameProject,
        urlRepositoryProject = editProjectVM.urlRepositoryProject,
        descriptionProject = editProjectVM.descriptionProject,
        onEditProjectAction = { action ->
            when (action) {
                EditProjectAction.HIDDEN_KEYBOARD -> editProjectState.hiddenKeyBoard()
                EditProjectAction.BACK_SCREEN -> actionRootDestinations.backDestination()
                EditProjectAction.UPDATE_PROJECT -> {
                    editProjectState.hiddenKeyBoard()
                    editProjectVM.updatedProject(
                        currentProjectData = projectDataEdit,
                        actionSuccess = actionRootDestinations::backDestination
                    )
                }
            }
        }
    )


}


@Composable
fun EditProjectScreen(
    scaffoldState: ScaffoldState,
    isUpdatedProject: Boolean,
    isDataValid: Boolean,
    urlImgProject: PropertySavableString,
    nameProject: PropertySavableString,
    urlRepositoryProject: PropertySavableString,
    descriptionProject: PropertySavableString,
    onEditProjectAction: (EditProjectAction) -> Unit,
    orientation: Int = LocalConfiguration.current.orientation
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ToolbarBack(
                title = stringResource(id = R.string.title_edit_project),
                actionBack = { onEditProjectAction(EditProjectAction.BACK_SCREEN) }
            )
        }
    ) { paddingValues ->

        when (orientation) {
            ORIENTATION_PORTRAIT -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageProjectEdit(
                        urlImgProject = urlImgProject.currentValue
                    )
                    ListInfoProject(
                        urlImgProject = urlImgProject,
                        nameProject = nameProject,
                        urlRepositoryProject = urlRepositoryProject,
                        descriptionProject = descriptionProject,
                        modifier = Modifier.padding(10.dp),
                        hiddenKeyBoard = { onEditProjectAction(EditProjectAction.HIDDEN_KEYBOARD) },
                        isEnable = !isUpdatedProject
                    )
                    ButtonUpdateProject(
                        isEnable = isDataValid && !isUpdatedProject,
                        actionClick = { onEditProjectAction(EditProjectAction.UPDATE_PROJECT) },
                    )
                }
            }

            else -> {
                Row(
                    modifier = Modifier
                        .padding(paddingValues),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(0.3f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ImageProjectEdit(
                            urlImgProject = urlImgProject.currentValue,
                            modifier = Modifier.weight(0.3f)
                        )
                        ButtonUpdateProject(
                            isEnable = isDataValid && !isUpdatedProject,
                            actionClick = { onEditProjectAction(EditProjectAction.UPDATE_PROJECT) },
                        )
                    }

                    ListInfoProject(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.7f)
                            .verticalScroll(rememberScrollState()),
                        urlImgProject = urlImgProject,
                        nameProject = nameProject,
                        urlRepositoryProject = urlRepositoryProject,
                        descriptionProject = descriptionProject,
                        hiddenKeyBoard = { onEditProjectAction(EditProjectAction.HIDDEN_KEYBOARD) },
                        isEnable = !isUpdatedProject
                    )
                }
            }
        }
        if (isUpdatedProject) BlockProcessing()
    }
}

