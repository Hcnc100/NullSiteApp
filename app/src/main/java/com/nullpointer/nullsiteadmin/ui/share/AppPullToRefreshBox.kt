package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import com.nullpointer.nullsiteadmin.R

/** Screen-owned refresh data; callbacks stay current without rebuilding scroll state. */
@Stable
class AppRefreshState(
    val gestureState: PullToRefreshState,
    private val refreshing: State<Boolean>,
    private val onRefresh: State<() -> Unit>,
) {
    val isRefreshing: Boolean get() = refreshing.value
    fun refresh() { if (!isRefreshing) onRefresh.value() }
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
    val label = stringResource(R.string.refresh_content)
    val canRefresh = enabled && !refreshState.isRefreshing
    // Keep the same content owner when refresh is disabled (editing/upload/loading).
    // The modifier, unlike PullToRefreshBox in Material 1.3.2, supports enabled.
    Box(modifier.pullToRefresh(
        isRefreshing = refreshState.isRefreshing,
        state = refreshState.gestureState,
        enabled = canRefresh,
        onRefresh = { if (canRefresh) refreshState.refresh() },
    ).semantics {
        customActions = if (canRefresh) listOf(CustomAccessibilityAction(label) {
            refreshState.refresh(); true
        }) else emptyList()
    }, contentAlignment = Alignment.TopCenter) {
        content()
        if (enabled) PullToRefreshDefaults.Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = refreshState.isRefreshing,
            state = refreshState.gestureState,
        )
    }
}
