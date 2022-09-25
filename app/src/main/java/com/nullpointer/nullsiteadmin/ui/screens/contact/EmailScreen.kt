package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
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
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.lists.ListEmptyEmail
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.lists.ListErrorEmail
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.lists.ListLoadingEmail
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.lists.ListSuccessEmails
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EmailDetailsScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun EmailScreen(
    actionRootDestinations: ActionRootDestinations,
    emailsViewModel: EmailsViewModel = shareViewModel(),
    emailScreenState: SimpleScreenState = rememberSimpleScreenState()
) {
    val stateListEmails by emailsViewModel.listEmails.collectAsState()

    LaunchedEffect(key1 = Unit) {
        emailsViewModel.errorEmail.collect(emailScreenState::showSnackMessage)
    }

    EmailScreen(
        listEmails = stateListEmails,
        scaffoldState = emailScreenState.scaffoldState,
        isConcatenate = emailsViewModel.isConcatenateEmail,
        concatenateEmails = emailsViewModel::concatenateEmails,
        actionDetails = {
            val route = EmailDetailsScreenDestination(it).route
            actionRootDestinations.changeRoot(
                "https://www.nullsiteadmin.com/$route".toUri()
            )
        }
    )
}

@Composable
private fun EmailScreen(
    isConcatenate: Boolean,
    scaffoldState: ScaffoldState,
    concatenateEmails: () -> Unit,
    actionDetails: (EmailContact) -> Unit,
    listEmails: Resource<List<EmailContact>>
) {
    Scaffold(
        scaffoldState = scaffoldState
    ) {
        when (listEmails) {
            Resource.Loading -> ListLoadingEmail(modifier = Modifier.padding(it))
            Resource.Failure -> ListErrorEmail(modifier = Modifier.padding(it))
            is Resource.Success -> {
                if (listEmails.data.isEmpty()) {
                    ListEmptyEmail(modifier = Modifier.padding(it))
                } else {
                    ListSuccessEmails(
                        modifier = Modifier.padding(it),
                        listEmails = listEmails.data,
                        actionDetails = actionDetails,
                        isConcatenate = isConcatenate,
                        concatenateEmails = concatenateEmails
                    )
                }
            }
        }
    }
}
