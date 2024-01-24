package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.utils.toFullDate
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun BodyEmail(
    emailData: EmailData,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {

    val fullTime = remember(emailData.createdAt) {
        emailData.createdAt.toFullDate(context)
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = fullTime,
                modifier = Modifier.align(Alignment.End)
            )
            Text(text = emailData.subject)
            Text(text = emailData.message)
        }
    }
}

@SimplePreview
@Composable
private fun BodyEmailPreview() {
    BodyEmail(
        emailData = EmailData.exampleOpen,
    )
}