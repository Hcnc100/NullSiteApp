package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import com.google.accompanist.swiperefresh.SwipeRefreshState
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
import com.nullpointer.nullsiteadmin.ui.screens.states.LazySwipeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberLazySwipeScreenState
import com.nullpointer.nullsiteadmin.ui.share.ScaffoldSwipeRefresh
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun EmailScreen(

    actionRootDestinations: ActionRootDestinations,
    emailsViewModel: EmailsViewModel = shareViewModel(),
    emailScreenState: LazySwipeScreenState = rememberLazySwipeScreenState(
        sizeScroll = 100F, isRefreshing = emailsViewModel.isRequestEmail
    )
) {
    val stateListEmails by emailsViewModel.listEmails.collectAsState()

    LaunchedEffect(key1 = Unit) {
        emailsViewModel.errorEmail.collect(emailScreenState::showSnackMessage)
    }

    EmailScreen(
        listEmails = stateListEmails,
        lazyListState = emailScreenState.lazyListState,
        scaffoldState = emailScreenState.scaffoldState,
        isConcatenate = emailsViewModel.isConcatenateEmail,
        swipeRefreshState = emailScreenState.swipeRefreshState,
        actionRefreshEmails = emailsViewModel::requestLastEmail,
        concatenateEmails = { emailsViewModel.concatenateEmails(emailScreenState::scrollToMore) },
        actionDetails = {
            val route = EmailDetailsScreenDestination(it).route
            actionRootDestinations.changeRoot(
                "https://www.nullsiteadmin.com/$route".toUri()
            )
        })

}

@Composable
private fun EmailScreen(
    isConcatenate: Boolean,
    lazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    concatenateEmails: () -> Unit,
    actionRefreshEmails: () -> Unit,
    swipeRefreshState: SwipeRefreshState,
    actionDetails: (EmailContact) -> Unit,
    listEmails: Resource<List<EmailContact>>
) {

    ScaffoldSwipeRefresh(
        scaffoldState = scaffoldState,
        actionOnRefresh = actionRefreshEmails,
        swipeRefreshState = swipeRefreshState
    ) {
        when (listEmails) {
            Resource.Loading -> ListLoadingEmail()
            Resource.Failure -> ListErrorEmail()
            is Resource.Success -> {
                if (listEmails.data.isEmpty()) {
                    ListEmptyEmail()
                } else {
                    ListSuccessEmails(
                        listEmails = listEmails.data,
                        lazyListState = lazyListState,
                        actionDetails = actionDetails,
                        isConcatenate = isConcatenate,
                        concatenateEmails = concatenateEmails
                    )
                }
            }
        }
    }
}
