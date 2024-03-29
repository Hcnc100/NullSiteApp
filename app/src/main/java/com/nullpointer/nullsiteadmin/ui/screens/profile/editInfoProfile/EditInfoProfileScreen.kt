package com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableImg
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.actions.EditInfoProfileActions
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.actions.EditInfoProfileActions.BACK_SCREEN
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.actions.EditInfoProfileActions.HIDDEN_BOTTOM_SHEET
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.actions.EditInfoProfileActions.HIDDEN_KEYBOARD
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.actions.EditInfoProfileActions.SAVE_INFO_PROFILE
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.actions.EditInfoProfileActions.SHOW_BOTTOM_SHEET
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.EditPhotoProfile
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.formInfoProfile.FormInfoProfile
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel.EditInfoViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.EditInfoProfileState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberEditInfoProfileState
import com.nullpointer.nullsiteadmin.ui.share.BlockProcessing
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.nullpointer.nullsiteadmin.ui.share.bottomSheetSelectImage.BottomSheetSelectImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import timber.log.Timber


@RootNavGraph
@Destination
@Composable
fun EditInfoProfileScreen(
    personalInfoData: PersonalInfoData?,
    actionRootDestinations: ActionRootDestinations,
    editInfoVM: EditInfoViewModel = hiltViewModel(),
    stateEditInfo: EditInfoProfileState = rememberEditInfoProfileState(
        actionCropFailure = { Timber.e("Error al cortar la imagen $it") },
        actionCropSuccess = { editInfoVM.imageProfile.changeValue(it) }
    )
) {

    LaunchedEffect(key1 = personalInfoData) {
        editInfoVM.initInfoProfile(personalInfoData)
    }

    LaunchedEffect(key1 = Unit) {
        editInfoVM.messageError.collect(stateEditInfo::showSnackMessage)
    }



    EditInfoProfileScreen(
        nameAdmin = editInfoVM.name,
        isDataValid = editInfoVM.isDataValid,
        sheetState = stateEditInfo.modalState,
        imageProfile = editInfoVM.imageProfile,
        professionAdmin = editInfoVM.profession,
        scaffoldState = stateEditInfo.scaffoldState,
        descriptionAdmin = editInfoVM.description,
        isLoading = editInfoVM.isUpdatedData || editInfoVM.imageProfile.isLoading,
        actionBeforeSelect = { imageSelect ->
            // * if get image or not, so hidden bottom sheet
            stateEditInfo.hiddenBottomSheet()
            // * only if get image so compress
            // * after compress, launch crop image for a square image
            imageSelect?.let {
                editInfoVM.changeImageSelected(
                    currentImage = it,
                    actionSuccessCompress = stateEditInfo::launchCropImage
                )
            }
        },
        onEditInfoProfileActions = { action ->
            when (action) {
                BACK_SCREEN -> actionRootDestinations.backDestination()
                SHOW_BOTTOM_SHEET -> stateEditInfo.showBottomSheet()
                HIDDEN_BOTTOM_SHEET -> stateEditInfo.hiddenBottomSheet()
                HIDDEN_KEYBOARD -> stateEditInfo.hiddenKeyBoard()
                SAVE_INFO_PROFILE -> {
                    stateEditInfo.hiddenKeyBoard()
                    editInfoVM.validateInfoProfile()?.let {
                        editInfoVM.updatePersonalInfo(
                            updateInfoProfileWrapper = it,
                            actionComplete = actionRootDestinations::backDestination
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun EditInfoProfileScreen(
    isLoading: Boolean,
    isDataValid: Boolean,
    scaffoldState: ScaffoldState,
    imageProfile: PropertySavableImg,
    sheetState: ModalBottomSheetState,
    nameAdmin: PropertySavableString,
    actionBeforeSelect: (Uri?) -> Unit,
    professionAdmin: PropertySavableString,
    descriptionAdmin: PropertySavableString,
    onEditInfoProfileActions: (EditInfoProfileActions) -> Unit,
    orientation: Int = LocalConfiguration.current.orientation
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            BottomSheetSelectImage(
                isVisible = sheetState.isVisible,
                actionBeforeSelect = actionBeforeSelect,
                actionHidden = { onEditInfoProfileActions(HIDDEN_BOTTOM_SHEET) }
            )
        }) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                ToolbarBack(
                    title = stringResource(R.string.title_edit_info_personal),
                    actionBack = { onEditInfoProfileActions(EditInfoProfileActions.BACK_SCREEN) }
                )
            }) { padding ->

            when (orientation) {
                ORIENTATION_PORTRAIT -> {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .padding(padding)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        EditPhotoProfile(
                            urlImg = imageProfile.value,
                            actionClick = { onEditInfoProfileActions(SHOW_BOTTOM_SHEET) },
                            editEnabled = !isLoading
                        )
                        FormInfoProfile(
                            nameAdmin = nameAdmin,
                            professionAdmin = professionAdmin,
                            descriptionAdmin = descriptionAdmin,
                            hiddenKeyBoard = { onEditInfoProfileActions(HIDDEN_KEYBOARD) },
                            isEnable = !isLoading,
                            isDataValid = isDataValid,
                            actionSave = {
                                onEditInfoProfileActions(SAVE_INFO_PROFILE)


                            }
                        )
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .padding(padding)
                            .verticalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EditPhotoProfile(
                            urlImg = imageProfile.value,
                            actionClick = { onEditInfoProfileActions(SHOW_BOTTOM_SHEET) },
                            editEnabled = !isLoading,
                            modifier = Modifier.weight(0.3f)
                        )
                        FormInfoProfile(
                            modifier = Modifier.weight(0.7f),
                            nameAdmin = nameAdmin,
                            professionAdmin = professionAdmin,
                            descriptionAdmin = descriptionAdmin,
                            hiddenKeyBoard = { onEditInfoProfileActions(HIDDEN_KEYBOARD) },
                            isEnable = !isLoading,
                            isDataValid = isDataValid,
                            actionSave = {
                                onEditInfoProfileActions(SAVE_INFO_PROFILE)
                            },
                        )
                    }
                }
            }
            if (isLoading) BlockProcessing()
        }
    }
}


@OrientationPreviews
@Composable
private fun EditInfoProfileScreenPreview() {
    EditInfoProfileScreen(
        isLoading = false,
        isDataValid = true,
        scaffoldState = rememberScaffoldState(),
        imageProfile = PropertySavableImg.example,
        sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
        nameAdmin = PropertySavableString.example,
        actionBeforeSelect = {},
        professionAdmin = PropertySavableString.example,
        descriptionAdmin = PropertySavableString.example,
        onEditInfoProfileActions = {}

    )
}

