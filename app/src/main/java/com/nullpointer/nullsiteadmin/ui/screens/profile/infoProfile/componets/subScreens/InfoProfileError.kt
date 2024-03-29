package com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.subScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.screens.shared.AnimationScreen
import com.nullpointer.runningcompose.ui.preview.config.ThemePreviews

@Composable
fun InfoProfileError(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier,
        animation = R.raw.error,
        textEmpty = stringResource(R.string.message_error_load_info_user)
    )
}

@OrientationPreviews
@ThemePreviews
@Composable
private fun InfoProfileErrorPreview() {
    InfoProfileError()
}
