package com.nullpointer.nullsiteadmin.ui.activitys

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.screens.NavGraphs
import com.nullpointer.nullsiteadmin.ui.screens.destinations.AuthScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.MainScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberRootScreenState
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.Direction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var loading = true
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { loading }

        setContent {
            NullSiteAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LaunchedEffect(key1 = Unit) {
                        authViewModel.launchBiometric(
                            callbackCancel = {
                                this@MainActivity.finish()
                            }
                        )
                    }
                    val isAuthUserState by authViewModel.isUserAuth.collectAsState()
                    val rootScreenState = rememberRootScreenState()


                    (isAuthUserState as? Resource.Success)?.let { dataAuth ->
                        (authViewModel.isAuthPassed as? Resource.Success)?.let { dataBiometric ->
                            if (dataBiometric.data) {
                                loading = false
                                DestinationsNavHost(
                                    navGraph = NavGraphs.root,
                                    engine = rootScreenState.navHostEngine,
                                    navController = rootScreenState.navHostController,
                                    dependenciesContainerBuilder = {
                                        dependency(authViewModel)
                                        dependency(createActionsRoot(navController))
                                    },
                                    startRoute = if (dataAuth.data) MainScreenDestination else AuthScreenDestination
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createActionsRoot(
        navController: NavController
    ): ActionRootDestinations {
        return object : ActionRootDestinations {
            override fun backDestination() = navController.popBackStack()
            override fun changeRoot(route: Uri) = navController.navigate(route)
            override fun changeRoot(direction: Direction) = navController.navigate(direction)

        }
    }
}


