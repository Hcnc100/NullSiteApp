package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
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
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = actionReplay,
        text = { Text(stringResource(R.string.email_reply)) },
        icon = {
        Icon(
            painter = painterResource(id = R.drawable.ic_reply),
            contentDescription = null
        )
    })
}

@SimplePreview
@Composable
private fun ButtonReplyEmailPreview() {
    ButtonReplyEmail(
        actionReplay = {}
    )
}
