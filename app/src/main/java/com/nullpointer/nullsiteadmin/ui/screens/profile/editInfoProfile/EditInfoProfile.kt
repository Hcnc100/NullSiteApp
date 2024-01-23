package com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.ButtonUpdateInfoProfile
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.EditPhotoProfile
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.viewModel.EditInfoViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.EditInfoProfileState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberEditInfoProfileState
import com.nullpointer.nullsiteadmin.ui.share.BlockProcessing
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.nullpointer.nullsiteadmin.ui.share.bottomSheetSelectImage.BottomSheetSelectImage
import com.ramcosta.composedestinations.annotation.Destination
import timber.log.Timber


@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph
@Destination
@Composable
fun EditInfoProfile(
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


    val enableFields = !editInfoVM.isUpdatedData && !editInfoVM.imageProfile.isLoading

    ModalBottomSheetLayout(
        sheetState = stateEditInfo.modalState,
        sheetContent = {
            BottomSheetSelectImage(
                isVisible = stateEditInfo.isModalVisible,
                actionHidden = stateEditInfo::hideModal,
                actionBeforeSelect = { uri ->
                    stateEditInfo.hideModal()
                    uri?.let {
                        editInfoVM.changeImageSelected(it, stateEditInfo::launchCropImage)
                    }
                }
            )
        }) {
        Scaffold(
            scaffoldState = stateEditInfo.scaffoldState,
            topBar = {
                ToolbarBack(
                    title = stringResource(R.string.title_edit_info_personal),
                    actionBack = actionRootDestinations::backDestination
                )
            }) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EditPhotoProfile(
                    urlImg = editInfoVM.imageProfile.value,
                    actionClick = stateEditInfo::showModal,
                    editEnabled = enableFields
                )
                EditableInformation(
                    nameAdmin = editInfoVM.name,
                    professionAdmin = editInfoVM.profession,
                    descriptionAdmin = editInfoVM.description,
                    hiddenKeyBoard = stateEditInfo::hiddenKeyBoard,
                    isEnable = enableFields
                )
                ButtonUpdateInfoProfile(
                    isEnable = editInfoVM.isDataValid,
                ) {
                    stateEditInfo.hiddenKeyBoard()
                    editInfoVM.validateInfoProfile()?.let {
                        editInfoVM.updatePersonalInfo(
                            updateInfoProfileWrapper = it,
                            actionComplete = actionRootDestinations::backDestination
                        )
                    }

                }
            }

            if (!enableFields) BlockProcessing()
        }
    }
}


@Composable
private fun EditableInformation(
    hiddenKeyBoard: () -> Unit,
    modifier: Modifier = Modifier,
    nameAdmin: PropertySavableString,
    professionAdmin: PropertySavableString,
    descriptionAdmin: PropertySavableString,
    isEnable: Boolean
) {
    Column(modifier = modifier) {
        EditableTextSavable(
            valueProperty = nameAdmin,
            singleLine = true,
            isEnabled = isEnable,
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(
            valueProperty = professionAdmin,
            singleLine = true,
            isEnabled = isEnable,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() })
        )
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = descriptionAdmin)
    }
}


