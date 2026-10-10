package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext

@Stable
open class SimpleScreenState(
    val context: Context,
    val snackbarHostState: SnackbarHostState,
) {
    suspend fun showSnackMessage(@StringRes stringRes: Int) {
        snackbarHostState.currentSnackbarData?.dismiss()
        snackbarHostState.showSnackbar(
            context.getString(stringRes)
        )
    }
}

@Composable
fun rememberSimpleScreenState(
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) = remember(snackbarHostState) {
    SimpleScreenState(
        context = context,
        snackbarHostState = snackbarHostState
    )
}
