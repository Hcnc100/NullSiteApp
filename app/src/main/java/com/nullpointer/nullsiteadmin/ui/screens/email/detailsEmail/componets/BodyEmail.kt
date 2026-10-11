package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets

import androidx.compose.material3.MaterialTheme
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun BodyEmail(
    emailData: EmailData,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {

    val fullTime = android.text.format.DateFormat.getMediumDateFormat(context).format(emailData.createdAt) +
        " · " + android.text.format.DateFormat.getTimeFormat(context).format(emailData.createdAt)

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    ) {
        SelectionContainer { Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = emailData.subject, style = MaterialTheme.typography.titleLarge)
            Text(
                text = fullTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider()
            Text(text = emailData.message, style = MaterialTheme.typography.bodyLarge)
        } }
    }
}

@SimplePreview
@Composable
private fun BodyEmailPreview() {
    BodyEmail(
        emailData = EmailData.exampleOpen,
    )
}
