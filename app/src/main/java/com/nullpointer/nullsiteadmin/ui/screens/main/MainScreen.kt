package com.nullpointer.nullsiteadmin.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    val isAuthUserState by authViewModel.isUserAuth.collectAsStateWithLifecycle()

    val isAuthBiometricPassed by authViewModel.isAuthBiometricPassed.collectAsStateWithLifecycle()

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

    // Leaf Scaffolds own system insets; a root Scaffold would apply them twice.
    Box(Modifier.fillMaxSize()) {
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
                        modifier = Modifier.fillMaxSize(),
                        dependenciesContainerBuilder = {
                            dependency(actionRootDestinations)
                        },
                    )
                }
            }
        }
    }

}
