package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Screen-owned refresh data; callbacks stay current without rebuilding scroll state. */
@Stable
class AppRefreshState(
    val gestureState: PullToRefreshState,
    private val refreshing: State<Boolean>,
    private val onRefresh: State<() -> Unit>,
) {
    val isRefreshing: Boolean get() = refreshing.value
    fun refresh() = onRefresh.value()
}

@Composable
fun rememberAppRefreshState(refreshing: Boolean, onRefresh: () -> Unit): AppRefreshState {
    val gesture = rememberPullToRefreshState()
    val currentRefreshing = rememberUpdatedState(refreshing)
    val currentRefresh = rememberUpdatedState(onRefresh)
    return remember(gesture) { AppRefreshState(gesture, currentRefreshing, currentRefresh) }
}

@Composable
fun AppPullToRefreshBox(
    refreshState: AppRefreshState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    // 1.3.2 has no enabled parameter: don't attach refresh gestures during initial loading.
    if (enabled) {
        PullToRefreshBox(
            isRefreshing = refreshState.isRefreshing,
            onRefresh = refreshState::refresh,
            state = refreshState.gestureState,
            modifier = modifier,
            contentAlignment = Alignment.TopCenter,
            content = content,
        )
    } else {
        Box(modifier, contentAlignment = Alignment.TopCenter, content = content)
    }
}
