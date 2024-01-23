package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterialApi::class)
@Stable
open class SwipeScreenState(
    context: Context,
    scaffoldState: ScaffoldState,
    val pullRefreshState: PullRefreshState,
    val swipeRefreshState: SwipeRefreshState,
) : SimpleScreenState(context = context, scaffoldState = scaffoldState) {
    val isRefreshing = swipeRefreshState.isRefreshing
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberSwipeScreenState(
    onRefresh: () -> Unit = {},
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    swipeRefreshState: SwipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing),
    pullRefreshState: PullRefreshState = rememberPullRefreshState(
        onRefresh = onRefresh,
        refreshing = isRefreshing
    )
) = remember(
    scaffoldState,
    swipeRefreshState,
    pullRefreshState
) {
    SwipeScreenState(
        context = context,
        scaffoldState = scaffoldState,
        swipeRefreshState = swipeRefreshState,
        pullRefreshState = pullRefreshState
    )
}