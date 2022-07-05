package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.EmailContact
import com.nullpointer.nullsiteadmin.presentation.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditProjectScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EmailDetailsScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun EmailScreen(
    emailsVM: EmailsViewModel= hiltViewModel(LocalContext.current as ComponentActivity),
    emailScreenState: SimpleScreenState = rememberSimpleScreenState(),
    actionRootDestinations: ActionRootDestinations
) {
    val emailsState by emailsVM.listEmails.collectAsState()
    LaunchedEffect(key1 = Unit) {
        emailsVM.errorEmail.collect(emailScreenState::showSnackMessage)
    }
    Scaffold(
        scaffoldState = emailScreenState.scaffoldState
    ) { paddingValues ->
        when (val listEmails = emailsState) {
            Resource.Failure -> AnimationScreen(
                animation = R.raw.error,
                textEmpty = stringResource(R.string.message_error_contact),
                modifier = Modifier.padding(paddingValues)
            )
            Resource.Loading -> {}
            is Resource.Success -> {
                if (listEmails.data.isEmpty()) {
                    AnimationScreen(
                        animation = R.raw.empty1,
                        textEmpty = stringResource(R.string.message_empty_contact),
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    ListEmails(
                        listEmails = listEmails.data,
                        actionDetails = {
                            val route=EmailDetailsScreenDestination(it).route
                            actionRootDestinations.changeRoot(
                                "https://myapp.com/$route".toUri()
                            )
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListEmails(
    listEmails: List<EmailContact>,
    actionDetails: (EmailContact) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = stringResource(R.string.text_number_emails,listEmails.size),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
        items(
            count = listEmails.size,
            key = { listEmails[it].id }) { index ->
            EmailItem(
                email = listEmails[index],
                modifier = Modifier.animateItemPlacement(),
                actionDetails = actionDetails
            )
        }
    }
}