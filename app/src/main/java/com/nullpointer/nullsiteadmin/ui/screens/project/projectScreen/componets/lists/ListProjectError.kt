package com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.componets.lists

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.screens.shared.AnimationScreen

@Composable
fun ListErrorProject(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier,
        animation = R.raw.error,
        textEmpty = stringResource(R.string.message_error_project)
    )
}