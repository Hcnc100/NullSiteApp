package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.nullpointer.nullsiteadmin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Stable
class EditInfoProfileState(
    context: Context,
    val scope: CoroutineScope,
    focusManager: FocusManager,
    scaffoldState: ScaffoldState,
    val modalState: ModalBottomSheetState,
    private val launcherCropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>
) : FocusScreenState(context, scaffoldState, focusManager) {


    val isModalVisible
        get() = modalState.isVisible

    fun hiddenBottomSheet() = scope.launch {
        modalState.hide()
    }


    fun showBottomSheet() {
        hiddenKeyBoard()
        scope.launch { modalState.show() }
    }

    fun launchCropImage(imgUri: Uri) {
        val cropImageContractOptions = CropImageContractOptions(
            imgUri,
            CropImageOptions(
                allowRotation = true,
                allowFlipping = false,
                fixAspectRatio = true,
                outputCompressQuality = 100,
                minCropResultHeight = 500,
                minCropResultWidth = 500,
                maxCropResultHeight = 1500,
                maxCropResultWidth = 1500,
                imageSourceIncludeGallery = false,
                imageSourceIncludeCamera = false,
                guidelines = CropImageView.Guidelines.ON,
                backgroundColor = Color.Black.value.toInt(),
                activityMenuIconColor = Color.Black.value.toInt(),
                activityBackgroundColor = Color.Black.value.toInt(),
                outputCompressFormat = Bitmap.CompressFormat.JPEG,
                activityTitle = context.getString(R.string.title_crop_image),
            )
        );
        launcherCropImage.launch(cropImageContractOptions)
    }
}


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