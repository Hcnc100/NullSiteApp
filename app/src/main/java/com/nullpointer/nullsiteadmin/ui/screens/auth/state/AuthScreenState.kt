package com.nullpointer.nullsiteadmin.ui.screens.auth.state

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.ScaffoldState

class AuthScreenState(
    val scaffoldState: ScaffoldState,
    val context: Context,
) {

    suspend fun showMessage(@StringRes stringRes: Int) {
        scaffoldState.snackbarHostState.showSnackbar(
            context.getString(stringRes)
        )
    }
}