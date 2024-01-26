package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class HomeScreenState(
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

@Composable
fun rememberHomeScreenState(
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
) = remember(scaffoldState, navController, coroutineScope) {
    HomeScreenState(
        context = context,
        scope = coroutineScope,
        navController = navController,
        scaffoldState = scaffoldState
    )
}
