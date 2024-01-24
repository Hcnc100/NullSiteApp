package com.nullpointer.nullsiteadmin.ui.preview.provider.email

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nullpointer.nullsiteadmin.models.email.data.EmailData

class EmailProvider : PreviewParameterProvider<EmailData> {
    override val values = listOf(
        EmailData.exampleClose,
        EmailData.exampleOpen,
    ).asSequence()
}