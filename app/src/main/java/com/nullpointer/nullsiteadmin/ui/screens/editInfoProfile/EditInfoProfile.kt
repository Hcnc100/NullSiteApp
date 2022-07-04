package com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import com.nullpointer.nullsiteadmin.presentation.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile.viewModel.EditInfoViewModel
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.SelectImgButtonSheet
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun EditInfoProfile(
    editProjectViewModel: EditInfoViewModel = hiltViewModel(),
    infoViewModel: InfoUserViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    stateEditInfo: EditInfoState = rememberEditInfoState(),
    personalInfo: PersonalInfo,
    actionRootDestinations: ActionRootDestinations
) {
    // * init info in view model
    LaunchedEffect(key1 = Unit) {
        editProjectViewModel.initInfoProfile(personalInfo)
    }

    LaunchedEffect(key1 = Unit) {
        editProjectViewModel.messageError.collect(stateEditInfo::showMessage)
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
                        editProjectViewModel.updateImg(it, stateEditInfo.context)
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
                    urlImg = editProjectViewModel.imageProfile.value,
                    modifier = Modifier.padding(10.dp),
                    actionClick = stateEditInfo::showModal
                )
                EditableInformation(
                    nameAdmin = editProjectViewModel.name,
                    professionAdmin = editProjectViewModel.profession,
                    descriptionAdmin = editProjectViewModel.description,
                    modifier = Modifier.padding(10.dp)
                )
                ButtonUpdateInfoProfile(
                    isEnable = editProjectViewModel.isDataValid,
                    modifier = Modifier.padding(10.dp)
                ) {
                    stateEditInfo.hiddenKeyBoard()
                    editProjectViewModel.getUpdatedPersonalInfo(stateEditInfo.context)?.let {
                        infoViewModel.updatePersonalInfo(it)
                    }
                    if (editProjectViewModel.hasAnyChange) actionRootDestinations.backDestination()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
class EditInfoState constructor(
    val scaffoldState: ScaffoldState,
    val modalState: ModalBottomSheetState,
    val scope: CoroutineScope,
    val context: Context,
    private val focusManager: FocusManager
) {

    val isModalVisible get() = modalState.isVisible

    fun hideModal() {
        scope.launch { modalState.hide() }
    }

    fun showModal() {
        hiddenKeyBoard()
        scope.launch { modalState.show() }
    }

    fun hiddenKeyBoard()= focusManager.clearFocus()



    suspend fun showMessage(@StringRes resource: Int) {
        scaffoldState.snackbarHostState.showSnackbar(
            context.getString(resource)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun rememberEditInfoState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    modalState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current
) = remember {
    EditInfoState(
        scaffoldState,
        modalState,
        coroutineScope,
        context,
        focusManager
    )
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

