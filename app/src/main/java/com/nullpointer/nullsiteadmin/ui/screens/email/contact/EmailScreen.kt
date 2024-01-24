package com.nullpointer.nullsiteadmin.ui.screens.email.contact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.presentation.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EmailDetailsScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.email.contact.components.lists.ListEmptyEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.contact.components.lists.ListErrorEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.contact.components.lists.ListSuccessEmails
import com.nullpointer.nullsiteadmin.ui.screens.states.LazySwipeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberLazySwipeScreenState
import com.nullpointer.nullsiteadmin.ui.share.BlockProcessing
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@HomeNavGraph
@Destination
@Composable
fun EmailScreen(
    actionRootDestinations: ActionRootDestinations,
    emailsViewModel: EmailsViewModel = shareViewModel(),
    emailScreenState: LazySwipeScreenState = rememberLazySwipeScreenState(
        sizeScroll = 100F,
        isRefreshing = emailsViewModel.isRequestEmail,
        onRefresh = emailsViewModel::requestLastEmail,
    )
) {
    val stateListEmails by emailsViewModel.listEmails.collectAsState()

    LaunchedEffect(key1 = Unit) {
        emailsViewModel.errorEmail.collect(emailScreenState::showSnackMessage)
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val visibleItems = emailScreenState.lazyListState.layoutInfo.visibleItemsInfo
            val lastVisibleItemIndex =
                visibleItems.lastOrNull()?.index ?: return@derivedStateOf false
            lastVisibleItemIndex >= emailScreenState.lazyListState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(key1 = shouldLoadMore) {
        if (shouldLoadMore) {
            emailsViewModel.concatenateEmails()
        }
    }


    EmailScreen(
        listEmails = stateListEmails,
        lazyListState = emailScreenState.lazyListState,
        scaffoldState = emailScreenState.scaffoldState,
        isConcatenate = emailsViewModel.isConcatenateEmail,
        isRefreshing = emailsViewModel.isRequestEmail,
        pullRefreshState = emailScreenState.pullRefreshState,
        actionDetails = {
            actionRootDestinations.changeRoot(EmailDetailsScreenDestination(it))
        })

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EmailScreen(
    isRefreshing: Boolean,
    isConcatenate: Boolean,
    lazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    pullRefreshState: PullRefreshState,
    actionDetails: (EmailData) -> Unit,
    listEmails: Resource<List<EmailData>>
) {

    Scaffold(
        scaffoldState = scaffoldState,
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .then(
                    when (listEmails) {
                        Resource.Loading -> Modifier
                        else -> Modifier.pullRefresh(pullRefreshState)
                    }
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            when (listEmails) {
                Resource.Loading -> BlockProcessing()
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
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing && listEmails !is Resource.Loading,
                state = pullRefreshState
            )
        }
    }
}
