package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.saveable.rememberSaveable
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
    snackbarHostState: SnackbarHostState,
    val modalState: SheetState,
    private val sheetVisibility: MutableState<Boolean>,
    private val launcherCropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>
) : FocusScreenState(context, snackbarHostState, focusManager) {


    var isModalVisible by sheetVisibility
        private set

    fun hiddenBottomSheet() = scope.launch {
        modalState.hide()
        isModalVisible = false
    }


    fun showBottomSheet() {
        hiddenKeyBoard()
        isModalVisible = true
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
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    modalState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    sheetVisibility: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
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
) = remember(context, snackbarHostState, coroutineScope, modalState, sheetVisibility, launcherCropImage) {
    EditInfoProfileState(
        context = context,
        scope = coroutineScope,
        modalState = modalState,
        sheetVisibility = sheetVisibility,
        focusManager = focusManager,
        snackbarHostState = snackbarHostState,
        launcherCropImage = launcherCropImage
    )
}
