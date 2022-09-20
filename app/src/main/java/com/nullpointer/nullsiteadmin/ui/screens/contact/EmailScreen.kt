package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.models.EmailContact
import com.nullpointer.nullsiteadmin.presentation.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
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
            modifier = Modifier.padding(paddingValues),
            listEmails = emailsState,
            actionDetails = {
                val route = EmailDetailsScreenDestination(it).route
                actionRootDestinations.changeRoot(
                    "https://www.nullsiteadmin.com/$route".toUri()
                )
            })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListEmails(
    modifier: Modifier = Modifier,
    actionDetails: (EmailContact) -> Unit,
    listEmails: Resource<List<EmailContact>>
) {
    when (listEmails) {
        Resource.Loading -> LoadingScreenEmail(modifier = modifier)
        Resource.Failure -> AnimationScreen(
            animation = R.raw.error,
            textEmpty = stringResource(R.string.message_error_contact),
            modifier = modifier
        )
        is Resource.Success -> {
            if (listEmails.data.isEmpty()) {
                AnimationScreen(
                    animation = R.raw.empty1,
                    textEmpty = stringResource(R.string.message_empty_contact),
                    modifier = modifier
                )
            } else {
                LazyColumn(modifier = modifier) {
                    item {
                        Text(
                            text = stringResource(
                                R.string.text_number_emails,
                                listEmails.data.size
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                    items(listEmails.data, key = { it.id }) { email ->
                        EmailItem(
                            email = email,
                            modifier = Modifier.animateItemPlacement(),
                            actionDetails = actionDetails
                        )
                    }
                }
            }
        }
    }
}

