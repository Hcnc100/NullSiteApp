package com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile.viewModel.EditInfoViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.EditInfoProfileState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberEditInfoProfileState
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.SelectImgButtonSheet
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination


@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph
@Destination
@Composable
fun EditInfoProfile(
    personalInfo: PersonalInfo,
    actionRootDestinations: ActionRootDestinations,
    editInfoVM: EditInfoViewModel = hiltViewModel(),
    stateEditInfo: EditInfoProfileState = rememberEditInfoProfileState()
) {

    LaunchedEffect(key1 = personalInfo) {
        editInfoVM.initInfoProfile(personalInfo)
    }

    LaunchedEffect(key1 = Unit) {
        editInfoVM.messageError.collect(stateEditInfo::showSnackMessage)
    }


    val isUpdatedData = editInfoVM.isUpdatedData

    ModalBottomSheetLayout(
        sheetState = stateEditInfo.modalState,
        sheetContent = {
            SelectImgButtonSheet(
                isVisible = stateEditInfo.isModalVisible,
                actionHidden = stateEditInfo::hideModal,
                actionBeforeSelect = { uri ->
                    stateEditInfo.hideModal()
                    uri?.let {
                        editInfoVM.imageProfile.changeValue(it)
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EditPhotoProfile(
                    urlImg = editInfoVM.imageProfile.value,
                    modifier = Modifier.padding(10.dp),
                    actionClick = stateEditInfo::showModal
                )
                EditableInformation(
                    nameAdmin = editInfoVM.name,
                    professionAdmin = editInfoVM.profession,
                    descriptionAdmin = editInfoVM.description,
                    modifier = Modifier.padding(10.dp),
                    hiddenKeyBoard = stateEditInfo::hiddenKeyBoard
                )
                ButtonUpdateInfoProfile(
                    isEnable = editInfoVM.isDataValid,
                    modifier = Modifier.padding(10.dp)
                ) {
                    stateEditInfo.hiddenKeyBoard()
                    editInfoVM.updatePersonalInfo(personalInfo) {
                        actionRootDestinations.backDestination()
                    }
                }
            }

            if (isUpdatedData) {
                Box(
                    modifier = Modifier
                        .background(Color.Gray.copy(alpha = 0.3f))
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ButtonUpdateInfoProfile(
    modifier: Modifier = Modifier,
    isEnable: Boolean,
    actionClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = actionClick,
        enabled = isEnable
    ) {
        Text(text = stringResource(R.string.text_save_personal_info))
    }
}

@Composable
private fun EditableInformation(
    hiddenKeyBoard: () -> Unit,
    modifier: Modifier = Modifier,
    nameAdmin: PropertySavableString,
    professionAdmin: PropertySavableString,
    descriptionAdmin: PropertySavableString
) {
    Column(modifier = modifier) {
        EditableTextSavable(
            valueProperty = nameAdmin,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() })
        )
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(
            valueProperty = professionAdmin,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() })
        )
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = descriptionAdmin)
    }
}

@Composable
private fun EditPhotoProfile(
    urlImg: Uri,
    modifier: Modifier = Modifier,
    actionClick: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box {
            AsyncImage(
                model = urlImg,
                contentDescription = stringResource(R.string.description_current_img_profile),
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            FloatingActionButton(
                onClick = actionClick,
                modifier = Modifier
                    .padding(15.dp)
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.description_edit_img_profile)
                )
            }
        }
    }
}

