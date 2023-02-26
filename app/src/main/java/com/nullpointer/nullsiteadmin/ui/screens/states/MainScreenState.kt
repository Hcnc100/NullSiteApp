package com.nullpointer.nullsiteadmin.ui.screens.states

import android.content.Context
import android.net.Uri
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.Direction

class MainScreenState(
    context: Context,
    scaffoldState: ScaffoldState,
    val navController: NavHostController
) : SimpleScreenState(context, scaffoldState) {

    val actionRootDestinations = object : ActionRootDestinations {
        override fun backDestination() = navController.popBackStack()
        override fun changeRoot(route: Uri) = navController.navigate(route)
        override fun changeRoot(direction: Direction) = navController.navigate(direction)
    }
}

@Composable
fun rememberMainScreenState(
    context: Context = LocalContext.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navHostController: NavHostController = rememberNavController()
) = remember(scaffoldState, navHostController) {
    MainScreenState(
        context = context,
        scaffoldState = scaffoldState,
        navController = navHostController
    )
}