package com.nullpointer.nullsiteadmin.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.NavGraphs
import com.nullpointer.nullsiteadmin.ui.screens.destinations.InfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.screens.states.MainScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberMainScreenState
import com.nullpointer.nullsiteadmin.ui.share.NavigatorDrawer
import com.nullpointer.nullsiteadmin.ui.share.ToolbarMenu
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.rememberNavHostEngine

@RootNavGraph
@Destination
@Composable
 fun MainScreen(
    authViewModel: AuthViewModel,
    actionRootDestinations: ActionRootDestinations,
    mainScreenState: MainScreenState = rememberMainScreenState()
) {
    Scaffold(
        scaffoldState = mainScreenState.scaffoldState,
        topBar = {
            ToolbarMenu(
                title = mainScreenState.titleNav,
                actionClickMenu = mainScreenState::openDrawer
            )
        },
        drawerContent = {
            NavigatorDrawer(
                closeSession = authViewModel::logOut,
                closeDrawer = mainScreenState::closeDrawer,
                navController = mainScreenState.navController
            )
        },
    ) { paddingValues ->

        DestinationsNavHost(
            startRoute = InfoProfileDestination,
            navGraph = NavGraphs.home,
            navController = mainScreenState.navController,
            engine = rememberNavHostEngine(),
            modifier = Modifier.padding(paddingValues),
            dependenciesContainerBuilder = {
                dependency(actionRootDestinations)
                dependency(authViewModel)
            }
        )
    }
}

