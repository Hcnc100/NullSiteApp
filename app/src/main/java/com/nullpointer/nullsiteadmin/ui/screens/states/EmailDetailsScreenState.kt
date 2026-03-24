package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class EmailDetailsScreenState(
    context: Context,
    scaffoldState: ScaffoldState,
) : SimpleScreenState(
    context = context,
    scaffoldState = scaffoldState
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
    scaffoldState: ScaffoldState = rememberScaffoldState()
) = remember(scaffoldState) {
    EmailDetailsScreenState(
        context = context,
        scaffoldState = scaffoldState
    )
}