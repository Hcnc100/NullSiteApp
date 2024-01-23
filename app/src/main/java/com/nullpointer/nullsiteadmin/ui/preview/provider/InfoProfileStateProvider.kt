package com.nullpointer.nullsiteadmin.ui.preview.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData

class InfoProfileStateProviderProvider : PreviewParameterProvider<Resource<PersonalInfoData?>> {
    override val values = listOf(
        Resource.Loading,
        Resource.Failure,
        Resource.Success(
            PersonalInfoData(
                name = "name",
                urlImg = "urlImg",
                updateAt = 0,
                profession = "profession",
                description = "description"
            )
        ),
        Resource.Success(null)
    ).asSequence()
}