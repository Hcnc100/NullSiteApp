package com.nullpointer.nullsiteadmin.ui.screens.detailsEmail

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions.*
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.sendEmail
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.core.utils.toFormat
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.presentation.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.animation.LottieContainer
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBackWithDeleter
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER


@RootNavGraph
@Destination(
    deepLinks = [
        DeepLink(uriPattern = "https://www.nullsiteadmin.com/$FULL_ROUTE_PLACEHOLDER")
    ]
)
@Composable
fun EmailDetailsScreen(
    email: EmailContact,
    authViewModel: AuthViewModel,
    rootDestinations: ActionRootDestinations,
    emailsViewModel: EmailsViewModel = shareViewModel(),
    emailsDetailsState: SimpleScreenState = rememberSimpleScreenState()
) {
    (authViewModel.isAuthBiometricPassed as? Resource.Success)?.let { isAuthPassed ->
        if (!isAuthPassed.data) {
            // ! if the auth no passed back
            LaunchedEffect(key1 = Unit) {
                rootDestinations.backDestination()
            }
        } else {
            // ! if the auth passed, show content email
            LaunchedEffect(key1 = Unit) {
                emailsViewModel.markAsOpen(email)
            }
            EmailsDetailsScreen(
                email = email,
                context = emailsDetailsState.context,
                actionBack = rootDestinations::backDestination,
                scaffoldState = emailsDetailsState.scaffoldState,
                emailScreenAction = { action, email ->
                    when (action) {
                        MARK_AS_OPEN -> emailsViewModel.markAsOpen(email)
                        DELETER -> emailsViewModel.deleterEmail(email.idEmail)
                        REPLY -> emailsDetailsState.context.sendEmail(email.email)
                    }
                }
            )
        }
    }

}

@Composable
private fun EmailsDetailsScreen(
    context: Context,
    email: EmailContact,
    actionBack: () -> Unit,
    scaffoldState: ScaffoldState,
    emailScreenAction: (EmailsScreenActions, EmailContact) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ToolbarEmailDetails(
                actionBack = actionBack,
                actionDeleter = {
                    emailScreenAction(DELETER, email)
                    actionBack()
                }
            )
        },
        floatingActionButton = {
            ButtonReplyEmail { emailScreenAction(REPLY, email) }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ContainerAnimateEmail()
            ContainerHeaderEmail(emailFrom = email.email, nameFrom = email.name)
            Spacer(modifier = Modifier.height(10.dp))
            BodyEmail(
                body = email.message,
                subject = email.subject,
                timestamp = email.timestampLong.toFormat(context)
            )
        }
    }
}

@Composable
private fun ToolbarEmailDetails(actionBack: () -> Unit, actionDeleter: () -> Unit) {
    ToolbarBackWithDeleter(
        title = stringResource(R.string.title_email_details),
        actionBack = actionBack,
        contentDescription = stringResource(id = R.string.description_deleter_email),
        actionDeleter = actionDeleter
    )
}

@Composable
private fun BodyEmail(
    timestamp: String,
    subject: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Card(shape = RoundedCornerShape(10.dp), modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = timestamp,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = subject)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = body)
        }
    }
}

@Composable
private fun ButtonReplyEmail(
    actionReplay: () -> Unit
) {
    FloatingActionButton(onClick = actionReplay) {
        Icon(
            painter = painterResource(id = R.drawable.ic_reply),
            contentDescription = stringResource(R.string.description_reply_email)
        )
    }
}

@Composable
private fun ContainerAnimateEmail(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieContainer(
            modifier = Modifier.size(150.dp),
            animation = R.raw.email
        )
    }
}

@Composable
private fun ContainerHeaderEmail(
    emailFrom: String,
    nameFrom: String,
    modifier: Modifier = Modifier
) {
    Card(shape = RoundedCornerShape(10.dp), modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = emailFrom)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = nameFrom)
        }
    }
}