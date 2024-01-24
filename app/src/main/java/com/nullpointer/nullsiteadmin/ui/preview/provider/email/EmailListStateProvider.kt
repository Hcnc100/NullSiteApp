package com.nullpointer.nullsiteadmin.ui.preview.provider.email

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.email.data.EmailData

class EmailListStateProvider : PreviewParameterProvider<Resource<List<EmailData>>> {
    override val values = listOf(
        Resource.Loading,
        Resource.Failure,
        Resource.Success(emptyList()),
        Resource.Success(EmailData.exampleList)
    ).asSequence()
}