package com.nullpointer.nullsiteadmin.ui.screens.email.emial.components.items

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.toFullDate
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.email.EmailProvider

@Composable
fun EmailItem(
    email: EmailData,
    actionDetails: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {

    val colorBackground by animateColorAsState(
        if (email.isOpen) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
        label = "BACKGROUND_COLOR_EMAIL_ITEM"
    )

    val colorForeground by animateColorAsState(
        if (email.isOpen) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary,
        label = "FOREGROUND_COLOR_EMAIL_ITEM"
    )

    val timeShow = remember(email.createdAt) {
        email.createdAt.toFullDate(context)
    }

    val (
        iconEmail,
        descriptionEmail,
        colorIconEmail,
    ) = when (email.isOpen) {
        true -> Triple(
            R.drawable.ic_email_open,
            R.string.description_email_open,
            MaterialTheme.colorScheme.onSurface
        )

        false -> Triple(
            R.drawable.ic_email_pending,
            R.string.description_email_pending,
            MaterialTheme.colorScheme.onPrimary
        )
    }

    Surface(
        color = colorBackground,
        contentColor = colorForeground,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .clickable { actionDetails() }
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = iconEmail),
                contentDescription = stringResource(id = descriptionEmail),
                modifier = Modifier.size(30.dp),
                tint = colorIconEmail
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = timeShow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End)
                )
                Text(
                    text = email.subject,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp
                )
                Text(
                    text = email.message,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.W200,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@SimplePreview
@Composable
private fun EmailItemOpenPreview(
    @PreviewParameter(EmailProvider::class)
    email: EmailData
) {
    EmailItem(
        email = email,
        actionDetails = { /*TODO*/ }
    )
}
