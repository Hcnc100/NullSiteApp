package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainScreenState(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val scope: CoroutineScope,
    val navController: NavHostController
) : SimpleScreenState(scaffoldState, context, focusManager) {

    var titleNav by mutableStateOf("")
        private set

    init {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            titleNav = MainDestinations.getLabel(destination.route)
        }
    }
    fun openDrawer() {
        scope.launch {
            scaffoldState.drawerState.open()
        }
    }

    fun closeDrawer() {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
 fun rememberMainScreenState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberAnimatedNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current
) = remember(scaffoldState, navController, coroutineScope) {
    MainScreenState(scaffoldState, context, focusManager, coroutineScope, navController)
}
