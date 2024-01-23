package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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

@OptIn(ExperimentalMaterialApi::class)
@Stable
class LazyGridSwipeScreenState(
    context: Context,
    scaffoldState: ScaffoldState,
    private val sizeScroll: Float,
    val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
    val pullRefreshState: PullRefreshState,
) : SimpleScreenState(
    context = context,
    scaffoldState = scaffoldState
) {

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberLazyGridSwipeScreenState(
    onRefresh: () -> Unit = {},
    sizeScroll: Float,
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    pullRefreshState: PullRefreshState = rememberPullRefreshState(
        onRefresh = onRefresh,
        refreshing = isRefreshing
    )
) = remember(
    scaffoldState,
    lazyGridState,
    coroutineScope,
    pullRefreshState
) {
    LazyGridSwipeScreenState(
        context = context,
        scope = coroutineScope,
        sizeScroll = sizeScroll,
        scaffoldState = scaffoldState,
        pullRefreshState = pullRefreshState,
        lazyGridState = lazyGridState,
    )
}