package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions.DELETER
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions.MARK_AS_OPEN
import com.nullpointer.nullsiteadmin.actions.EmailsScreenActions.REPLY
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.BodyEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.ButtonReplyEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.ContainerAnimateEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.HeaderEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets.ToolbarEmailDetails
import com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.viewModel.EmailDetailsViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.EmailDetailsScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberEmailDetailsScreen
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
    emailsDetailsState: EmailDetailsScreenState = rememberEmailDetailsScreen(),
) {
    val isAuthBiometricPassed by emailsViewModel.isAuthBiometricPassed.collectAsStateWithLifecycle()

    (isAuthBiometricPassed)?.let { isAuthPassed ->
        if (!isAuthPassed) {
            // ! if the auth no passed back
            LaunchedEffect(key1 = Unit) {
                rootDestinations.backDestination()
            }
        } else {

            LaunchedEffect(key1 = Unit) {
                emailsViewModel.errorEmailDetails.collect(
                    emailsDetailsState::showSnackMessage
                )
            }

            LaunchedEffect(key1 = email) {
                if (!email.isOpen) {
                    emailsViewModel.markAsOpen(email)
                }
            }
            EmailsDetailsScreen(
                email = email,
                actionBack = rootDestinations::backDestination,
                snackbarHostState = emailsDetailsState.snackbarHostState,
                isDeleting = emailsViewModel.isDeleting,
                emailScreenAction = { action, email ->
                    when (action) {
                        MARK_AS_OPEN -> emailsViewModel.markAsOpen(email)
                        REPLY -> emailsDetailsState.launchEmail(email.email)
                        DELETER -> {
                            emailsViewModel.deleterEmail(
                                idEmail = email.idEmail,
                                actionSuccess = rootDestinations::backDestination
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
internal fun EmailsDetailsScreen(
    email: EmailData,
    actionBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    emailScreenAction: (EmailsScreenActions, EmailData) -> Unit,
    orientation: Int = LocalConfiguration.current.orientation,
    isDeleting: Boolean = false,
) {
    var confirmDelete by rememberSaveable(email.idEmail) { mutableStateOf(false) }
    BackHandler(enabled = isDeleting) { /* Keep the deletion's ViewModel alive until completion. */ }
    if (confirmDelete && !isDeleting) {
        // Resolve in the screen's locale before entering the dialog's window context.
        val title = stringResource(R.string.email_delete_title)
        val message = stringResource(R.string.email_delete_confirmation, email.name)
        val confirmLabel = stringResource(R.string.email_delete_confirm)
        val cancelLabel = stringResource(R.string.email_delete_cancel)
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    emailScreenAction(DELETER, email)
                }) { Text(confirmLabel, color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text(cancelLabel) }
            },
        )
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column { ToolbarEmailDetails(
                actionBack = actionBack,
                enabled = !isDeleting,
                actionDeleter = { confirmDelete = true }
            )
                if (isDeleting) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text(stringResource(R.string.email_deleting),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        floatingActionButton = {
            if (!isDeleting) ButtonReplyEmail(
                actionReplay = { emailScreenAction(REPLY, email) }
            )
        }
    ) {

        when (orientation) {
            ORIENTATION_PORTRAIT -> {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ContainerAnimateEmail(height = 96.dp)
                    HeaderEmail(emailData = email)
                    BodyEmail(emailData = email)
                }
            }

            else -> {
                Row(
                    modifier = Modifier
                        .padding(it)
                        .padding(16.dp)
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
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 96.dp),
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
        snackbarHostState = remember { SnackbarHostState() },
        emailScreenAction = { _, _ -> }
    )
}






