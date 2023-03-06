package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

open class SimpleScreenState(
    val context: Context,
    val scaffoldState: ScaffoldState,
) {
    suspend fun showSnackMessage(@StringRes stringRes: Int) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(
            context.getString(stringRes)
        )
    }
}

@Composable
fun rememberSimpleScreenState(
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) = remember(scaffoldState) {
    SimpleScreenState(
        context = context,
        scaffoldState = scaffoldState
    )
}