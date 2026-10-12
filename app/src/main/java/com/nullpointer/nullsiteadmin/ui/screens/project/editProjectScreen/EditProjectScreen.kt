package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ProjectContentEditor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.actions.EditProjectAction
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ButtonUpdateProject
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ImageProjectEdit
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components.ListInfoProject
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel.EditProjectViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.FocusScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberFocusScreenState
import com.nullpointer.nullsiteadmin.ui.share.BlockProcessing
import com.nullpointer.nullsiteadmin.ui.share.EditorToolbarBack
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph
@Destination
@Composable
fun EditProjectScreen(
    projectDataEdit: ProjectData,
    actionRootDestinations: ActionRootDestinations,
    editProjectViewModel: EditProjectViewModel = hiltViewModel(),
    editProjectState: FocusScreenState = rememberFocusScreenState()
) {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { uri -> runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } }
        editProjectViewModel.enqueueImages(uris)
    }
    var discard by rememberSaveable { mutableStateOf(false) }
    val requestBack = {
        if (!editProjectViewModel.isUpdatedProject && !editProjectViewModel.isUploading) {
            if (editProjectViewModel.hasUnsavedChanges) discard = true else actionRootDestinations.backDestination()
        }
        Unit
    }
    BackHandler(editProjectViewModel.hasUnsavedChanges || editProjectViewModel.isUpdatedProject || editProjectViewModel.isUploading) { requestBack() }
    val discardTitle = stringResource(R.string.project_discard_title)
    val keepEditing = stringResource(R.string.profile_keep_editing)
    val discardConfirm = stringResource(R.string.profile_discard_confirm)
    if (discard) AlertDialog(onDismissRequest = { discard = false }, title = { Text(discardTitle) },
        confirmButton = { TextButton({ discard = false; actionRootDestinations.backDestination() }) { Text(discardConfirm) } },
        dismissButton = { TextButton({ discard = false }) { Text(keepEditing) } })

    LaunchedEffect(key1 = projectDataEdit) {
        editProjectViewModel.initVM(projectDataEdit)
    }

    LaunchedEffect(key1 = Unit) {
        editProjectViewModel.messageError.collect(editProjectState::showSnackMessage)
    }

    EditProjectScreen(
        snackbarHostState = editProjectState.snackbarHostState,
        isUpdatedProject = editProjectViewModel.isUpdatedProject,
        isUploading = editProjectViewModel.isUploading,
        isDataValid = editProjectViewModel.isDataValid,
        urlImgProject = editProjectViewModel.urlImgProject,
        nameProject = editProjectViewModel.nameProject,
        urlRepositoryProject = editProjectViewModel.urlRepositoryProject,
        descriptionProject = editProjectViewModel.descriptionProject,
        extraContent = {
            ProjectContentEditor(editProjectViewModel.contentEditor,
                enabled = !editProjectViewModel.isUpdatedProject && !editProjectViewModel.isUploading,
                published = editProjectViewModel.published, onPublished = { editProjectViewModel.published = it },
                onUpload = { picker.launch(arrayOf("image/jpeg", "image/png", "image/webp", "image/gif")) },
                pending = editProjectViewModel.pendingUploads.size, uploading = editProjectViewModel.isUploading,
                progress = editProjectViewModel.uploadProgress, onRetry = editProjectViewModel::retryUploads,
                onCancel = editProjectViewModel::cancelUploads, onDiscardPending = editProjectViewModel::discardPendingUploads)
        },
        onEditProjectAction = { action ->
            when (action) {
                EditProjectAction.HIDDEN_KEYBOARD -> editProjectState.hiddenKeyBoard()
                EditProjectAction.BACK_SCREEN -> requestBack()
                EditProjectAction.UPDATE_PROJECT -> {
                    editProjectState.hiddenKeyBoard()
                    editProjectViewModel.updatedProject(
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
    snackbarHostState: SnackbarHostState,
    isUpdatedProject: Boolean,
    isDataValid: Boolean,
    urlImgProject: PropertySavableString,
    nameProject: PropertySavableString,
    urlRepositoryProject: PropertySavableString,
    descriptionProject: PropertySavableString,
    onEditProjectAction: (EditProjectAction) -> Unit,
    orientation: Int = LocalConfiguration.current.orientation,
    extraContent: @Composable () -> Unit = {},
    isUploading: Boolean = false,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.imePadding(),
        topBar = {
            EditorToolbarBack(
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
                        extraContent = extraContent,
                        urlImgProject = urlImgProject,
                        nameProject = nameProject,
                        urlRepositoryProject = urlRepositoryProject,
                        descriptionProject = descriptionProject,
                        modifier = Modifier.padding(10.dp),
                        hiddenKeyBoard = { onEditProjectAction(EditProjectAction.HIDDEN_KEYBOARD) },
                        isEnable = !isUpdatedProject && !isUploading
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
                        extraContent = extraContent,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(0.7f)
                            .verticalScroll(rememberScrollState()),
                        urlImgProject = urlImgProject,
                        nameProject = nameProject,
                        urlRepositoryProject = urlRepositoryProject,
                        descriptionProject = descriptionProject,
                        hiddenKeyBoard = { onEditProjectAction(EditProjectAction.HIDDEN_KEYBOARD) },
                        isEnable = !isUpdatedProject && !isUploading
                    )
                }
            }
        }
        if (isUpdatedProject) BlockProcessing()
    }
}

