package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions.DELETER
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions.MARK_AS_OPEN
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions.REPLY
import com.nullpointer.nullsiteadmin.core.utils.sendEmail
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.BodyEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.ButtonReplyEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.ContainerAnimateEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.HeaderEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.ToolbarEmailDetails
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.viewModel.EmailDetailsViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.annotation.RootNavGraph


@RootNavGraph
@Destination(
    deepLinks = [
        DeepLink(uriPattern = "https://www.nullsiteadmin.com/$FULL_ROUTE_PLACEHOLDER")
    ]
)
@Composable
fun EmailDetailsScreen(
    email: EmailData,
    rootDestinations: ActionRootDestinations,
    emailsViewModel: EmailDetailsViewModel = hiltViewModel(),
    emailsDetailsState: SimpleScreenState = rememberSimpleScreenState()
) {
    val isAuthBiometricPassed by emailsViewModel.isAuthBiometricPassed.collectAsState()

    (isAuthBiometricPassed)?.let { isAuthPassed ->
        if (!isAuthPassed) {
            // ! if the auth no passed back
            LaunchedEffect(key1 = Unit) {
                rootDestinations.backDestination()
            }
        } else {
            LaunchedEffect(key1 = email) {
                if (!email.isOpen) {
                    emailsViewModel.markAsOpen(email)
                }
            }
            EmailsDetailsScreen(
                email = email,
                actionBack = rootDestinations::backDestination,
                scaffoldState = emailsDetailsState.scaffoldState,
                emailScreenAction = { action, email ->
                    when (action) {
                        MARK_AS_OPEN -> emailsViewModel.markAsOpen(email)
                        REPLY -> emailsDetailsState.context.sendEmail(email.email)
                        DELETER -> {
                            emailsViewModel.deleterEmail(email.idEmail)
                            rootDestinations.backDestination()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun EmailsDetailsScreen(
    email: EmailData,
    actionBack: () -> Unit,
    scaffoldState: ScaffoldState,
    emailScreenAction: (EmailsScreenActions, EmailData) -> Unit,
    orientation: Int = LocalConfiguration.current.orientation
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
            ButtonReplyEmail(
                actionReplay = { emailScreenAction(REPLY, email) }
            )
        }
    ) {

        when (orientation) {
            ORIENTATION_PORTRAIT -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ContainerAnimateEmail()
                    HeaderEmail(emailData = email)
                    BodyEmail(emailData = email)
                }
            }

            else -> {
                Row(
                    modifier = Modifier
                        .padding(it)
                        .padding(10.dp)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ContainerAnimateEmail(
                        modifier = Modifier.weight(0.3f)
                    )
                    Column(
                        modifier = Modifier
                            .weight(0.7f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HeaderEmail(emailData = email)
                        BodyEmail(emailData = email)
                    }
                }
            }
        }
    }
}


@OrientationPreviews
@Composable
private fun EmailsDetailsPreview() {
    EmailsDetailsScreen(
        email = EmailData.exampleLong,
        actionBack = {},
        scaffoldState = rememberScaffoldState(),
        emailScreenAction = { _, _ -> }
    )
}






