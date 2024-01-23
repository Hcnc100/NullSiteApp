package com.nullpointer.nullsiteadmin.ui.screens.profileScreen.editInfoProfile.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.BooleanProvider

@Composable
fun ButtonUpdateInfoProfile(
    modifier: Modifier = Modifier,
    isEnable: Boolean,
    actionClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = actionClick,
        enabled = isEnable
    ) {
        Text(text = stringResource(R.string.text_save_personal_info))
    }
}


@SimplePreview
@Composable
private fun ButtonUpdateInfoProfilePreview(
    @PreviewParameter(BooleanProvider::class)
    isEnable: Boolean
) {
    ButtonUpdateInfoProfile(
        isEnable = isEnable,
        actionClick = {}
    )
}