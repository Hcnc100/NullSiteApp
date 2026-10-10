package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager

@Stable
open class FocusScreenState(
    context: Context,
    snackbarHostState: SnackbarHostState,
    private val focusManager: FocusManager
) : SimpleScreenState(context, snackbarHostState) {

    fun hiddenKeyBoard() = focusManager.clearFocus()

    fun moveNextFocus() = focusManager.moveFocus(FocusDirection.Next)
}


@Composable
fun rememberFocusScreenState(
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) = remember(snackbarHostState) {
    FocusScreenState(
        context = context,
        focusManager = focusManager,
        snackbarHostState = snackbarHostState
    )
}
