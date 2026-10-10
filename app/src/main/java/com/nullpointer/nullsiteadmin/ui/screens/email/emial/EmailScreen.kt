package com.nullpointer.nullsiteadmin.ui.screens.email.emial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import com.nullpointer.nullsiteadmin.ui.share.AppRefreshState
import com.nullpointer.nullsiteadmin.ui.share.AppPullToRefreshBox
import com.nullpointer.nullsiteadmin.ui.share.rememberAppRefreshState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.shareViewModel
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.preview.provider.email.EmailListStateProvider
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EmailDetailsScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.email.emial.components.lists.ListEmptyEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.emial.components.lists.ListErrorEmail
import com.nullpointer.nullsiteadmin.ui.screens.email.emial.components.lists.ListSuccessEmails
import com.nullpointer.nullsiteadmin.ui.screens.email.emial.viewModel.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.LazySwipeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberLazySwipeScreenState
import com.nullpointer.nullsiteadmin.ui.share.BlockProcessing
import com.ramcosta.composedestinations.annotation.Destination


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
    val stateListEmails by emailsViewModel.listEmails.collectAsStateWithLifecycle()

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

    LaunchedEffect(shouldLoadMore, emailsViewModel.isRequestEmail) {
        if (shouldLoadMore && !emailsViewModel.isRequestEmail) {
            emailsViewModel.concatenateEmails()
        }
    }


    EmailScreen(
        listEmails = stateListEmails,
        lazyListState = emailScreenState.lazyListState,
        snackbarHostState = emailScreenState.snackbarHostState,
        isConcatenate = emailsViewModel.isConcatenateEmail,
        isRefreshing = emailsViewModel.isRequestEmail,
        pullRefreshState = emailScreenState.pullRefreshState,
        actionDetails = {
            actionRootDestinations.changeRoot(EmailDetailsScreenDestination(it))
        })

}


@Composable
private fun EmailScreen(
    isRefreshing: Boolean,
    isConcatenate: Boolean,
    lazyListState: LazyListState,
    snackbarHostState: SnackbarHostState,
    pullRefreshState: AppRefreshState,
    actionDetails: (EmailData) -> Unit,
    listEmails: Resource<List<EmailData>>
) {

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        AppPullToRefreshBox(
            refreshState = pullRefreshState,
            enabled = listEmails !is Resource.Loading,
            modifier = Modifier
                .fillMaxSize()
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
                            contentPadding = innerPadding,
                            actionDetails = actionDetails,
                            isConcatenate = isConcatenate,
                        )
                    }
                }
            }

        }
    }
}


@OrientationPreviews
@Composable
private fun EmailScreenPreview(
    @PreviewParameter(EmailListStateProvider::class)
    emailsListState: Resource<List<EmailData>>
) {
    EmailScreen(
        isRefreshing = true,
        isConcatenate = true,
        lazyListState = rememberLazyListState(),
        snackbarHostState = remember { SnackbarHostState() },
        pullRefreshState = rememberAppRefreshState(
            onRefresh = {},
            refreshing = true
        ),
        actionDetails = {},
        listEmails = emailsListState
    )

}
