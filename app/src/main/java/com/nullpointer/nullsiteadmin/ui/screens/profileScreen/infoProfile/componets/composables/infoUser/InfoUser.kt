package com.nullpointer.nullsiteadmin.ui.screens.profileScreen.infoProfile.componets.composables.infoUser

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.runningcompose.ui.preview.config.OrientationPreviews

@Composable
fun InfoUser(
    modifier: Modifier = Modifier,
    personalInfoData: PersonalInfoData,
    orientation: Int = LocalConfiguration.current.orientation
) {

    when (orientation) {
        ORIENTATION_PORTRAIT -> InfoUserPortrait(
            modifier = modifier,
            personalInfoData = personalInfoData
        )

        else -> InfoUserLandscape(
            modifier = modifier,
            personalInfoData = personalInfoData
        )
    }
}

@OrientationPreviews
@Composable
fun InfoUserPreview() {
    InfoUser(
        personalInfoData = PersonalInfoData.example
    )
}