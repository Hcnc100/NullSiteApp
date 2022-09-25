package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import com.nullpointer.nullsiteadmin.presentation.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.ListEmptyEmail
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.ListErrorEmail
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.ListLoadingEmail
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.ListSuccessEmails
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EmailDetailsScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun EmailScreen(
    emailsVM: EmailsViewModel = shareViewModel(),
    actionRootDestinations: ActionRootDestinations,
    emailScreenState: SimpleScreenState = rememberSimpleScreenState()
) {
    val emailsState by emailsVM.listEmails.collectAsState()

    LaunchedEffect(key1 = Unit) {
        emailsVM.errorEmail.collect(emailScreenState::showSnackMessage)
    }

    Scaffold(
        scaffoldState = emailScreenState.scaffoldState
    ) { paddingValues ->
        ListEmails(
            listEmails = emailsState,
            isConcatenate = emailsVM.isConcatenate,
            modifier = Modifier.padding(paddingValues),
            concatenateEmails = emailsVM::concatenateEmails,
            actionDetails = {
                val route = EmailDetailsScreenDestination(it).route
                actionRootDestinations.changeRoot(
                    "https://www.nullsiteadmin.com/$route".toUri()
                )
            })
    }
}

@Composable
private fun ListEmails(
    isConcatenate: Boolean,
    modifier: Modifier = Modifier,
    concatenateEmails: () -> Unit,
    actionDetails: (EmailContact) -> Unit,
    listEmails: Resource<List<EmailContact>>
) {
    when (listEmails) {
        Resource.Loading -> ListLoadingEmail(modifier = modifier)
        Resource.Failure -> ListErrorEmail(modifier = modifier)
        is Resource.Success -> {
            if (listEmails.data.isEmpty()) {
                ListEmptyEmail(modifier = modifier)
            } else {
                ListSuccessEmails(
                    modifier = modifier,
                    listEmails = listEmails.data,
                    actionDetails = actionDetails,
                    isConcatenate = isConcatenate,
                    concatenateEmails = concatenateEmails
                )
            }
        }
    }
}