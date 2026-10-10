package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import com.nullpointer.nullsiteadmin.ui.share.AppRefreshState
import com.nullpointer.nullsiteadmin.ui.share.rememberAppRefreshState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext

@Stable
class PullRefreshScreenState(
    context: Context,
    snackbarHostState: SnackbarHostState,
    val pullRefreshState: AppRefreshState
) : SimpleScreenState(
    context = context,
    snackbarHostState = snackbarHostState
)

@Composable
fun rememberPullRefreshScreenState(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    pullRefreshState: AppRefreshState = rememberAppRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )
) = remember(
    snackbarHostState,
    pullRefreshState
) {
    PullRefreshScreenState(
        context = context,
        snackbarHostState = snackbarHostState,
        pullRefreshState = pullRefreshState
    )
}
