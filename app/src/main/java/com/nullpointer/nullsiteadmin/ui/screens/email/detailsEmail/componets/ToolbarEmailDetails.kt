package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ToolbarEmailDetails(
    actionBack: () -> Unit,
    actionDeleter: () -> Unit,
    enabled: Boolean = true,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.title_email_details), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = actionBack, enabled = enabled) {
                Icon(painterResource(R.drawable.ic_arrow_back), stringResource(R.string.description_icon_back))
            }
        },
        actions = {
            IconButton(onClick = actionDeleter, enabled = enabled) {
                Icon(painterResource(R.drawable.ic_deleter), stringResource(R.string.description_deleter_email))
            }
        },
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
