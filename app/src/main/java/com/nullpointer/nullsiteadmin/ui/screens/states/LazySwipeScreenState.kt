package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import kotlinx.coroutines.launch


@Stable
class LazySwipeScreenState(
    context: Context,
    snackbarHostState: SnackbarHostState,
    private val sizeScroll: Float,
    val lazyListState: LazyListState,
    private val scope: CoroutineScope,
    val pullRefreshState: AppRefreshState,
) : SimpleScreenState(
    context = context,
    snackbarHostState = snackbarHostState,
) {
    fun onScrollChanged() {
        scope.launch {
            lazyListState.animateScrollBy(sizeScroll)
        }
    }
}


@Composable
fun rememberLazySwipeScreenState(
    onRefresh: () -> Unit,
    sizeScroll: Float,
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    lazyListState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    pullRefreshState: AppRefreshState = rememberAppRefreshState(
        onRefresh = onRefresh,
        refreshing = isRefreshing
    )
) = remember(snackbarHostState, lazyListState, coroutineScope) {
    LazySwipeScreenState(
        context = context,
        scope = coroutineScope,
        sizeScroll = sizeScroll,
        snackbarHostState = snackbarHostState,
        lazyListState = lazyListState,
        pullRefreshState = pullRefreshState,
    )
}
