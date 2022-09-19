package com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import com.nullpointer.nullsiteadmin.presentation.InfoUserViewModel
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
    actionRootDestinations: ActionRootDestinations,
    editInfoVM: EditInfoViewModel = shareViewModel(),
    infoViewModel: InfoUserViewModel = shareViewModel(),
    stateEditInfo: EditInfoProfileState = rememberEditInfoProfileState()
) {

    LaunchedEffect(key1 = Unit) {
        editInfoVM.messageError.collect(stateEditInfo::showSnackMessage)
    }

    ModalBottomSheetLayout(
        sheetState = stateEditInfo.modalState,
        sheetContent = {
            SelectImgButtonSheet(
                isVisible = stateEditInfo.isModalVisible,
                actionHidden = stateEditInfo::hideModal,
                actionBeforeSelect = { uri ->
                    stateEditInfo.hideModal()
                    uri?.let {
                        editInfoVM.updateImg(it, stateEditInfo.context)
                    }
                }
            )
        }) {
        Scaffold(
            scaffoldState = stateEditInfo.scaffoldState,
            topBar = {
                ToolbarBack(
                    title = "Edit Personl Info",
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
                    modifier = Modifier.padding(10.dp)
                )
                ButtonUpdateInfoProfile(
                    isEnable = editInfoVM.isDataValid,
                    modifier = Modifier.padding(10.dp)
                ) {
                    stateEditInfo.hiddenKeyBoard()
                    editInfoVM.getUpdatedPersonalInfo(stateEditInfo.context)?.let {
                        infoViewModel.updatePersonalInfo(it)
                    }
                    if (editInfoVM.hasAnyChange) actionRootDestinations.backDestination()
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
    nameAdmin: PropertySavableString,
    professionAdmin: PropertySavableString,
    descriptionAdmin: PropertySavableString,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        EditableTextSavable(valueProperty = nameAdmin)
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = professionAdmin)
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

