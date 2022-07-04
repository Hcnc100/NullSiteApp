package com.nullpointer.nullsiteadmin.ui.screens.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.screens.NavGraphs
import com.nullpointer.nullsiteadmin.ui.screens.animation.DetailsTransition
import com.nullpointer.nullsiteadmin.ui.screens.destinations.InfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.share.NavigatorDrawer
import com.nullpointer.nullsiteadmin.ui.share.ToolbarMenu
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.dependency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Destination(
    style = DetailsTransition::class
)
@Composable
 fun MainScreen(
    mainScreenState: MainScreenState = rememberMainScreenState(),
    actionRootDestinations: ActionRootDestinations
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
                mainAppState = mainScreenState,
                closeSession = actionRootDestinations::logout
            )
        },
    ) { paddingValues ->

        val navHostEngine = rememberAnimatedNavHostEngine(
            navHostContentAlignment = Alignment.BottomEnd,
            rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
        )

        DestinationsNavHost(
            startRoute = InfoProfileDestination,
            navGraph = NavGraphs.root,
            navController = mainScreenState.navController,
            engine = navHostEngine,
            modifier = Modifier.padding(paddingValues),
            dependenciesContainerBuilder = {
                dependency(actionRootDestinations)
            }
        )
    }
}

 class MainScreenState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope,
) {
    var isHomeRoute by mutableStateOf(false)
        private set

    var titleNav by mutableStateOf(MainDestinations.PersonalInfoScreen.label)
        private set

    init {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            isHomeRoute = MainDestinations.isHomeRoute(destination.route)
            titleNav = MainDestinations.getLabel(destination.route)
        }
    }

    fun openDrawer() {
        coroutineScope.launch {
            scaffoldState.drawerState.open()
        }
    }

    fun closeDrawer() {
        coroutineScope.launch {
            scaffoldState.drawerState.close()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun rememberMainScreenState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberAnimatedNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, coroutineScope) {
    MainScreenState(scaffoldState, navController, coroutineScope)
}