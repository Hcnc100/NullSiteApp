package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun ScaffoldSwipeRefresh(
    actionOnRefresh: () -> Unit,
    scaffoldState: ScaffoldState,
    swipeRefreshState: SwipeRefreshState,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = topBar,
        scaffoldState = scaffoldState,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
    ) {
        SwipeRefresh(
            content = content,
            state = swipeRefreshState,
            onRefresh = actionOnRefresh,
            modifier = Modifier.padding(it)
        )
    }
}