package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.EmailContact

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmailItem(
    email: EmailContact,
    actionDetails: (EmailContact) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        shape = RoundedCornerShape(10.dp),
        onClick = { actionDetails(email) },
        modifier = modifier.padding(vertical = 4.dp, horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(shape = CircleShape, modifier = Modifier.size(30.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_email_pending),
                    contentDescription = ""
                )
            }
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
                    text = email.message, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.W200,
                    fontSize = 14.sp
                )
            }
        }
    }
}

