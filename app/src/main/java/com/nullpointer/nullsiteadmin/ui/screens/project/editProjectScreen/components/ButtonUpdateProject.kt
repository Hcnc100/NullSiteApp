package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components

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
 fun ButtonUpdateProject(
    isEnable: Boolean,
    actionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = actionClick,
        enabled = isEnable,
        modifier = modifier
    ) {
        Text(stringResource(R.string.text_update_project))
    }
}

@SimplePreview
@Composable
fun ButtonUpdateProjectPreview(
    @PreviewParameter(BooleanProvider::class)
    isEnable: Boolean
) {
    ButtonUpdateProject(
        isEnable = isEnable,
        actionClick = {}
    )
}