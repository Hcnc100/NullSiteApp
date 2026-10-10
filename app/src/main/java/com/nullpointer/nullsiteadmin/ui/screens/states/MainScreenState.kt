package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.Direction

@Stable
class MainScreenState(
    context: Context,
    snackbarHostState: SnackbarHostState,
    val navController: NavHostController
) : SimpleScreenState(context, snackbarHostState) {

    val actionRootDestinations = object : ActionRootDestinations {
        override fun backDestination() = navController.popBackStack()
        override fun changeRoot(route: Uri) = navController.navigate(route)
        override fun changeRoot(direction: Direction) = navController.navigate(direction)
    }
}

@Composable
fun rememberMainScreenState(
    context: Context = LocalContext.current,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navHostController: NavHostController = rememberNavController()
) = remember(snackbarHostState, navHostController) {
    MainScreenState(
        context = context,
        snackbarHostState = snackbarHostState,
        navController = navHostController
    )
}
