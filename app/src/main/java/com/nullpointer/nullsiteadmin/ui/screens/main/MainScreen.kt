package com.nullpointer.nullsiteadmin.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.screens.NavGraphs
import com.nullpointer.nullsiteadmin.ui.screens.destinations.HomeScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.LockScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.LoginScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.shared.rememberLifecycleEvent
import com.nullpointer.nullsiteadmin.ui.screens.states.MainScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberMainScreenState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency


@Composable
fun MainScreen(
    actionChangeLoading: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    rootScreenState: MainScreenState = rememberMainScreenState()
) {

    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(key1 = lifecycleEvent) {
        when (lifecycleEvent) {
            Lifecycle.Event.ON_RESUME -> authViewModel.initVerifyBiometrics()
            // ! TODO: Fix this, it's not working when select image profile
//            Lifecycle.Event.ON_STOP -> authViewModel.blockBiometric()
            else -> Unit
        }
    }

    val isAuthUserState by authViewModel.isUserAuth.collectAsState()

    val isAuthBiometricPassed by authViewModel.isAuthBiometricPassed.collectAsState()

    MainScreen(
        isAuthUserState = isAuthUserState,
        actionChangeLoading = actionChangeLoading,
        isAuthBiometricPassed = isAuthBiometricPassed,
        navHostController = rootScreenState.navController,
        actionRootDestinations = rootScreenState.actionRootDestinations
    )
}


@Composable
fun MainScreen(
    isAuthUserState: Resource<Boolean>,
    navHostController: NavHostController,
    actionChangeLoading: () -> Unit,
    isAuthBiometricPassed: Boolean?,
    actionRootDestinations: ActionRootDestinations,
) {

    Scaffold { padding ->
        (isAuthUserState as? Resource.Success)?.let { dataAuth ->
            (isAuthBiometricPassed)?.let { isBiometricPassed ->
                val isAuthUser = dataAuth.data
                when {
                    isAuthUser && isBiometricPassed -> HomeScreenDestination
                    isAuthUser && !isBiometricPassed -> LockScreenDestination
                    else -> LoginScreenDestination
                }.let { startRoute ->
                    LaunchedEffect(key1 = Unit) {
                        actionChangeLoading()
                    }
                    DestinationsNavHost(
                        startRoute = startRoute,
                        navGraph = NavGraphs.root,
                        navController = navHostController,
                        modifier = Modifier.padding(padding),
                        dependenciesContainerBuilder = {
                            dependency(actionRootDestinations)
                        },
                    )
                }
            }
        }
    }

}