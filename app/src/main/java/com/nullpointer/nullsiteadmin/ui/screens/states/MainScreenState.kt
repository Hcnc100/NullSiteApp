package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainScreenState(
    context: Context,
    val scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    val navController: NavHostController
) : SimpleScreenState(context, scaffoldState) {

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
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberAnimatedNavController(),
) = remember(scaffoldState, navController, coroutineScope) {
    MainScreenState(
        context = context,
        scope = coroutineScope,
        navController = navController,
        scaffoldState = scaffoldState
    )
}
