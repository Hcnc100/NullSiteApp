package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun ButtonReplyEmail(
    actionReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = actionReplay
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_reply),
            contentDescription = stringResource(R.string.description_reply_email)
        )
    }
}

@SimplePreview
@Composable
private fun ButtonReplyEmailPreview() {
    ButtonReplyEmail(
        actionReplay = {}
    )
}