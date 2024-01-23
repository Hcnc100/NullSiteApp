package com.nullpointer.nullsiteadmin.ui.screens.profileScreen.infoProfile.componets.composables.infoUser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.ui.screens.profileScreen.infoProfile.componets.composables.PhotoProfile
import com.nullpointer.runningcompose.ui.preview.config.LandscapePreview

@Composable
fun InfoUserLandscape(
    modifier: Modifier = Modifier,
    personalInfoData: PersonalInfoData,
) {
    Row(
        modifier = modifier
            .padding(15.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PhotoProfile(
            urlImgProfile = personalInfoData.urlImg,
            modifier = Modifier
                .weight(0.3f)
                .wrapContentSize()
        )
        ContainerInfoUser(
            personalInfoData = personalInfoData,
            modifier = Modifier.weight(0.7f)
        )
    }
}

@LandscapePreview
@Composable
private fun PreviewInfoUserNormalLandscape() {
    InfoUserLandscape(
        personalInfoData = PersonalInfoData.example
    )
}

@LandscapePreview
@Composable
private fun PreviewInfoUserLongLandscape() {
    InfoUserLandscape(
        personalInfoData = PersonalInfoData.descriptionLong
    )
}