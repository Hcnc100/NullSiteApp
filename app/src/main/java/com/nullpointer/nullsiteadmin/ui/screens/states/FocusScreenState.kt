package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager

open class FocusScreenState(
    context: Context,
    scaffoldState: ScaffoldState,
    private val focusManager: FocusManager
) : SimpleScreenState(context, scaffoldState) {

    fun hiddenKeyBoard() = focusManager.clearFocus()

    fun moveNextFocus() = focusManager.moveFocus(FocusDirection.Next)
}


@Composable
fun rememberFocusScreenState(
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) = remember(scaffoldState) {
    FocusScreenState(
        context = context,
        focusManager = focusManager,
        scaffoldState = scaffoldState
    )
}