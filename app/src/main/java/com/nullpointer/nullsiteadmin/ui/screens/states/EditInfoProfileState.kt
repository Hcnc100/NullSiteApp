package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
class EditInfoProfileState(
    context: Context,
    val scope: CoroutineScope,
    focusManager: FocusManager,
    scaffoldState: ScaffoldState,
    val modalState: ModalBottomSheetState,
    private val launcherCropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>
) : FocusScreenState(context, scaffoldState, focusManager) {

    @OptIn(ExperimentalMaterialApi::class)
    val isModalVisible
        get() = modalState.isVisible

    fun hideModal() {
        scope.launch { modalState.hide() }
    }

    fun showModal() {
        hiddenKeyBoard()
        scope.launch { modalState.show() }
    }

    fun launchCropImage(imgUri: Uri) {
        launcherCropImage.launch(
            options(uri = imgUri) {
                setFixAspectRatio(true)
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                setBackgroundColor(Color.Black.value.toInt())
                setActivityBackgroundColor(Color.Black.value.toInt())
                setMinCropResultSize(200, 200)
                setMaxCropResultSize(1500, 1500)
            }
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberEditInfoProfileState(
    actionCropSuccess: (Uri) -> Unit,
    actionCropFailure: (Exception?) -> Unit,
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    modalState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    launcherCropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult> =
        rememberLauncherForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                try {
                    actionCropSuccess(result.uriContent!!)
                } catch (e: Exception) {
                    actionCropFailure(e)
                }
            } else {
                actionCropFailure(result.error)
            }
        }
) = remember(scaffoldState, coroutineScope, modalState) {
    EditInfoProfileState(
        context = context,
        scope = coroutineScope,
        modalState = modalState,
        focusManager = focusManager,
        scaffoldState = scaffoldState,
        launcherCropImage = launcherCropImage
    )
}