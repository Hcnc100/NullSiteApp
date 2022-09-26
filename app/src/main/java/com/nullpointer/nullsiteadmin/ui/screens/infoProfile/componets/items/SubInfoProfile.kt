package com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.InfoUser
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.LoadingInfoUser

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

@Composable
fun InfoProfileEmpty(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier,
        animation = R.raw.empty1,
        textEmpty = stringResource(R.string.message_empty_info_user)
    )
}

@Composable
fun InfoProfileSuccess(
    personalInfo: PersonalInfo,
    modifier: Modifier = Modifier
) {
    InfoUser(
        personalInfo = personalInfo,
        modifier = modifier
    )
}

@Composable
fun InfoPersonalLoading(
    modifier: Modifier = Modifier
) {
    LoadingInfoUser(modifier = modifier)
}