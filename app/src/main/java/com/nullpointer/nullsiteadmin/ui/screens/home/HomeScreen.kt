package com.nullpointer.nullsiteadmin.ui.screens.home

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.NavGraphs
import com.nullpointer.nullsiteadmin.ui.screens.destinations.InfoProfileScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.home.componets.NavigatorDrawer
import com.nullpointer.nullsiteadmin.ui.screens.states.HomeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberHomeScreenState
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
    mainScreenState: HomeScreenState = rememberHomeScreenState(),
) {

    LaunchedEffect(key1 = Unit) {
        authViewModel.verifyPhoneData()
    }

    val percent = when (LocalConfiguration.current.orientation) {
        ORIENTATION_LANDSCAPE -> 0.5f
        else -> 0.75f
    }

    Scaffold(
        drawerShape = customShape(percent),
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
                navController = mainScreenState.navController,
                percent = percent,
            )
        },
    ) { paddingValues ->

        DestinationsNavHost(
            startRoute = InfoProfileScreenDestination,
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

// * TODO - FIXED SIZE DRAWER
fun customShape(
    percent: Float
) = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        return Outline.Rectangle(
            Rect(
                left = 0f,
                top = 0f,
                right = (size.width * percent),
                bottom = size.height
            )
        )
    }
}


