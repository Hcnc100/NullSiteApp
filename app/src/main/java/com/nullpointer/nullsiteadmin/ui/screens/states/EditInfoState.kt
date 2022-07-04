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
class EditInfoState constructor(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val scope: CoroutineScope,
     val modalState: ModalBottomSheetState,
) : SimpleScreenState(scaffoldState, context, focusManager) {

    val isModalVisible get() = modalState.isVisible

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
 fun rememberEditInfoState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    modalState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current
) = remember(scaffoldState, coroutineScope,modalState) {
    EditInfoState(
        scaffoldState, context, focusManager, coroutineScope, modalState
    )
}