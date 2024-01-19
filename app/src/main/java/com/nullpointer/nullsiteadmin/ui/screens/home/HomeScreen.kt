package com.nullpointer.nullsiteadmin.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.NavGraphs
import com.nullpointer.nullsiteadmin.ui.screens.destinations.InfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.screens.states.HomeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberHomeScreenState
import com.nullpointer.nullsiteadmin.ui.share.NavigatorDrawer
import com.nullpointer.nullsiteadmin.ui.share.ToolbarMenu
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.rememberNavHostEngine

@RootNavGraph
@Destination
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    actionRootDestinations: ActionRootDestinations,
    mainScreenState: HomeScreenState = rememberHomeScreenState()
) {

    LaunchedEffect(key1 = Unit){
        authViewModel.verifyPhoneData()
    }

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

