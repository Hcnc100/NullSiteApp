package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
class EditInfoProfileState(
    context: Context,
    val scope: CoroutineScope,
    focusManager: FocusManager,
    scaffoldState: ScaffoldState,
    val modalState: ModalBottomSheetState,
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
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberEditInfoProfileState(
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    modalState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
) = remember(scaffoldState, coroutineScope, modalState) {
    EditInfoProfileState(
        context = context,
        scope = coroutineScope,
        modalState = modalState,
        focusManager = focusManager,
        scaffoldState = scaffoldState
    )
}