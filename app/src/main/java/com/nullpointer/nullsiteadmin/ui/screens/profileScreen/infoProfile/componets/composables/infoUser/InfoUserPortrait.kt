package com.nullpointer.nullsiteadmin.ui.screens.profileScreen.infoProfile.componets.composables.infoUser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.profileScreen.infoProfile.componets.composables.PhotoProfile

@Composable
fun InfoUserPortrait(
    modifier: Modifier = Modifier,
    personalInfoData: PersonalInfoData,
) {
    Column(
        modifier = modifier
            .padding(15.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PhotoProfile(
            urlImgProfile = personalInfoData.urlImg,
        )
        ContainerInfoUser(
            personalInfoData = personalInfoData,
        )
    }
}

@SimplePreview
@Composable
fun PreviewInfoUserNormalPortrait() {
    InfoUserPortrait(
        personalInfoData = PersonalInfoData.example
    )
}


@SimplePreview
@Composable
fun PreviewInfoUserLongPortrait() {
    InfoUserPortrait(
        personalInfoData = PersonalInfoData.descriptionLong
    )
}





