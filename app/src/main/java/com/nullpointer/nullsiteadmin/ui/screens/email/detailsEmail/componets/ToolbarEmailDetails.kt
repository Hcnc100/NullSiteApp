package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBackWithDeleter

@Composable
fun ToolbarEmailDetails(
    actionBack: () -> Unit,
    actionDeleter: () -> Unit
) {
    ToolbarBackWithDeleter(
        title = stringResource(R.string.title_email_details),
        actionBack = actionBack,
        actionDeleter = actionDeleter,
        contentDescription = stringResource(id = R.string.description_deleter_email)
    )
}


@SimplePreview
@Composable
fun ToolbarEmailDetailsPreview() {
    ToolbarEmailDetails(
        actionBack = {},
        actionDeleter = {}
    )
}