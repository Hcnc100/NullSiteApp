package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Stable
class LazySwipeScreenState(
    context: Context,
    scaffoldState: ScaffoldState,
    private val sizeScroll: Float,
    val lazyListState: LazyListState,
    private val scope: CoroutineScope,
    swipeRefreshState: SwipeRefreshState,
    pullRefreshState: PullRefreshState,
) : SwipeScreenState(
    context = context,
    scaffoldState = scaffoldState,
    swipeRefreshState = swipeRefreshState,
    pullRefreshState = pullRefreshState
) {
    fun scrollToMore() = scope.launch {
        lazyListState.animateScrollBy(sizeScroll)
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberLazySwipeScreenState(
    onRefresh: () -> Unit = {},
    sizeScroll: Float,
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    lazyListState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    swipeRefreshState: SwipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = isRefreshing
    ),
    pullRefreshState: PullRefreshState = rememberPullRefreshState(
        onRefresh = onRefresh,
        refreshing = isRefreshing
    )
) = remember(scaffoldState, lazyListState, swipeRefreshState, coroutineScope) {
    LazySwipeScreenState(
        context = context,
        scope = coroutineScope,
        sizeScroll = sizeScroll,
        scaffoldState = scaffoldState,
        lazyListState = lazyListState,
        pullRefreshState = pullRefreshState,
        swipeRefreshState = swipeRefreshState
    )
}