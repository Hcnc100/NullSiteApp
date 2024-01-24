package com.nullpointer.nullsiteadmin.ui.screens.email.contact.components.lists

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.shared.AnimationScreen

@Composable
fun ListEmptyEmail(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier,
        animation = R.raw.empty1,
        textEmpty = stringResource(R.string.message_empty_contact)
    )
}

@SimplePreview
@Composable
private fun ListEmptyEmailPreview() {
    ListEmptyEmail()
}