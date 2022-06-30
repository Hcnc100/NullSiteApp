package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.toFormat
import com.nullpointer.nullsiteadmin.models.EmailContact

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmailItem(
    email: EmailContact,
    actionDeleter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (isExpanded, changeIsExpanded) = rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        shape = RoundedCornerShape(10.dp),
        onClick = { changeIsExpanded(!isExpanded) },
        modifier = modifier
            .animateContentSize()
            .padding(vertical = 4.dp, horizontal = 10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            HeaderEmail(email = email, isExpanded = isExpanded)
            if (isExpanded) ContainerEmail(email = email, actionDeleter = actionDeleter)
        }
    }
}

@Composable
private fun HeaderEmail(
    email: EmailContact,
    isExpanded: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(.9f)
        ) {
            Text(
                text = stringResource(R.string.text_to_email,email.email,),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.text_subject_email,email.subject),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.body2
            )
        }
        Box(
            modifier = Modifier.weight(.1f),
            contentAlignment = Alignment.Center) {
            IndicatorOpenEmail(isExpanded = isExpanded)
        }
    }
}

@Composable
fun IndicatorOpenEmail(isExpanded: Boolean) {
    Box(
        modifier = Modifier
            .size(25.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colors.primary)
    ) {
        Icon(
            painter = painterResource(id = if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
            contentDescription = stringResource(R.string.description_icon_email_open)
        )
    }
}

@Composable
private fun ContainerEmail(
    email: EmailContact,
    actionDeleter: (String) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.text_date_email, email.timestamp.toFormat(context)),
            style = MaterialTheme.typography.caption
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = email.message, style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(15.dp))
        FloatingActionButton(
            onClick = { actionDeleter(email.id) },
            modifier = Modifier.size(35.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_deleter),
                contentDescription = stringResource(R.string.description_deleter_email)
            )
        }
    }
}