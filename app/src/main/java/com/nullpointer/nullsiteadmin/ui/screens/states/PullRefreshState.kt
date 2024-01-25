package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material.ScaffoldState
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Stable
class PullRefreshScreenState(
    context: Context,
    scaffoldState: ScaffoldState,
    val pullRefreshState: PullRefreshState
) : SimpleScreenState(
    context = context,
    scaffoldState = scaffoldState
)

@Composable
fun rememberPullRefreshScreenState(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    pullRefreshState: PullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )
) = remember(
    scaffoldState,
    pullRefreshState
) {
    PullRefreshScreenState(
        context = context,
        scaffoldState = scaffoldState,
        pullRefreshState = pullRefreshState
    )
}