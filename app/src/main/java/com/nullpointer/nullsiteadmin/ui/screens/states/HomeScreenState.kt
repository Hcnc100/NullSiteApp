package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    snackbarHostState: SnackbarHostState,
    val drawerState: DrawerState,
    val navController: NavHostController
) : SimpleScreenState(context, snackbarHostState) {

    var titleNav by mutableStateOf("")
        private set

    init {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            titleNav = MainDestinations.getLabel(destination.route, context)
        }
    }

    fun openDrawer() {
        scope.launch {
            drawerState.open()
        }
    }

    fun closeDrawer() {
        scope.launch {
            drawerState.close()
        }
    }
}

@Composable
fun rememberHomeScreenState(
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    navController: NavHostController = rememberNavController(),
) = remember(context, snackbarHostState, navController, coroutineScope, drawerState) {
    HomeScreenState(
        context = context,
        scope = coroutineScope,
        drawerState = drawerState,
        navController = navController,
        snackbarHostState = snackbarHostState
    )
}
