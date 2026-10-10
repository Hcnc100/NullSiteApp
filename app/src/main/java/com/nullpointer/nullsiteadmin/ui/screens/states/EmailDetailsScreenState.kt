package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class EmailDetailsScreenState(
    context: Context,
    snackbarHostState: SnackbarHostState,
) : SimpleScreenState(
    context = context,
    snackbarHostState = snackbarHostState
) {
    fun launchEmail(
        email: String
    ) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$email"))
        context.startActivity(intent)
    }

}


@Composable
fun rememberEmailDetailsScreen(
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) = remember(snackbarHostState) {
    EmailDetailsScreenState(
        context = context,
        snackbarHostState = snackbarHostState
    )
}
