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
    val pullRefreshState: PullRefreshState,
) : SimpleScreenState(
    context = context,
    scaffoldState = scaffoldState,
) {
    fun onScrollChanged() {
        scope.launch {
            lazyListState.animateScrollBy(sizeScroll)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberLazySwipeScreenState(
    onRefresh: () -> Unit,
    sizeScroll: Float,
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    lazyListState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    pullRefreshState: PullRefreshState = rememberPullRefreshState(
        onRefresh = onRefresh,
        refreshing = isRefreshing
    )
) = remember(scaffoldState, lazyListState, coroutineScope) {
    LazySwipeScreenState(
        context = context,
        scope = coroutineScope,
        sizeScroll = sizeScroll,
        scaffoldState = scaffoldState,
        lazyListState = lazyListState,
        pullRefreshState = pullRefreshState,
    )
}