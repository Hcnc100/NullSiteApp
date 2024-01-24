package com.nullpointer.nullsiteadmin.ui.screens.home.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun ButtonLogOut(
    actionLogOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = actionLogOut,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_logout),
                contentDescription = stringResource(R.string.description_close_session)
            )
            Text(text = stringResource(R.string.text_log_out))
        }

    }
}


@SimplePreview
@Composable
private fun ButtonLogOutPreview() {
    ButtonLogOut(actionLogOut = {})
}