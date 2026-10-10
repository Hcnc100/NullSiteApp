package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import com.nullpointer.nullsiteadmin.ui.share.AppRefreshState
import com.nullpointer.nullsiteadmin.ui.share.rememberAppRefreshState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope


@Stable
class LazyGridSwipeScreenState(
    context: Context,
    snackbarHostState: SnackbarHostState,
    private val sizeScroll: Float,
    val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
    val pullRefreshState: AppRefreshState,
) : SimpleScreenState(
    context = context,
    snackbarHostState = snackbarHostState
) {

}


@Composable
fun rememberLazyGridSwipeScreenState(
    onRefresh: () -> Unit = {},
    sizeScroll: Float,
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    lazyGridState: LazyGridState = rememberLazyGridState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    pullRefreshState: AppRefreshState = rememberAppRefreshState(
        onRefresh = onRefresh,
        refreshing = isRefreshing
    )
) = remember(
    snackbarHostState,
    lazyGridState,
    coroutineScope,
    pullRefreshState
) {
    LazyGridSwipeScreenState(
        context = context,
        scope = coroutineScope,
        sizeScroll = sizeScroll,
        snackbarHostState = snackbarHostState,
        pullRefreshState = pullRefreshState,
        lazyGridState = lazyGridState,
    )
}
