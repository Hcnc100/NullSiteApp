package com.nullpointer.nullsiteadmin.ui.screens.home.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.appDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.DirectionDestination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun NavigatorDrawer(
    percent: Float,
    closeSession: () -> Unit,
    closeDrawer: () -> Unit,
    navController: NavController
) {
    Drawer(
        percent = percent,
        closeSession = {
            closeSession()
            closeDrawer()
        },
        navController = navController,
        onDestinationClicked = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            closeDrawer()
        }
    )
}

@Composable
private fun Drawer(
    percent: Float,
    navController: NavController,
    closeSession: () -> Unit,
    onDestinationClicked: (destination: DirectionDestination) -> Unit
) {
    val currentDestination = navController.currentBackStackEntryAsState()
        .value?.appDestination()
    Column(
        modifier = Modifier
            .fillMaxWidth(percent)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ImageDraw()
        Column {
            MainDestinations.values().forEach {
                ItemNavDrawer(
                    currentDestination = it,
                    isSelected = currentDestination?.route == it.destinations.route,
                    onDestinationClicked = { onDestinationClicked(it.destinations) }
                )
            }
        }
        ButtonLogOut(
            actionLogOut = closeSession,
        )
    }
}

@SimplePreview
@Composable
fun DrawerPreview() {
    Drawer(
        navController = rememberNavController(),
        closeSession = { /*TODO*/ },
        onDestinationClicked = {},
        percent = 0.75f
    )
}