package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

open class SwipeScreenState(
    context: Context,
    scaffoldState: ScaffoldState,
    val swipeRefreshState: SwipeRefreshState
) : SimpleScreenState(context = context, scaffoldState = scaffoldState) {
    val isRefreshing = swipeRefreshState.isRefreshing
}


@Composable
fun rememberSwipeScreenState(
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    swipeRefreshState: SwipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
) = remember(scaffoldState, swipeRefreshState) {
    SwipeScreenState(
        context = context,
        scaffoldState = scaffoldState,
        swipeRefreshState = swipeRefreshState
    )
}