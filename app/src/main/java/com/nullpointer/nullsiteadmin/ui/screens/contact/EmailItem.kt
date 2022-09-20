package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.getGrayColor
import com.nullpointer.nullsiteadmin.models.EmailContact

@Composable
fun EmailItem(
    email: EmailContact,
    actionDetails: (EmailContact) -> Unit,
    modifier: Modifier = Modifier
) {

    val colorBackground by animateColorAsState(
        if (email.isOpen) Color.Transparent else MaterialTheme.colors.primary
    )

    val (iconEmail, descriptionEmail) = remember(email.isOpen) {
        if (email.isOpen) Pair(
            R.drawable.ic_email_open,
            R.string.description_email_open
        ) else Pair(
            R.drawable.ic_email_pending,
            R.string.description_email_pending
        )
    }

    Surface(
        color = colorBackground,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 10.dp)
            .clickable {
                actionDetails(email)
            }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconEmail),
                contentDescription = stringResource(id = descriptionEmail),
                modifier = Modifier.size(30.dp),
                tint = getGrayColor()
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = email.email,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
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

