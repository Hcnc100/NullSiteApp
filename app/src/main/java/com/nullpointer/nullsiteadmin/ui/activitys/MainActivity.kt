package com.nullpointer.nullsiteadmin.ui.activitys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.screens.NavGraphs
import com.nullpointer.nullsiteadmin.ui.screens.destinations.AuthScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.InfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.share.NavigatorDrawer
import com.nullpointer.nullsiteadmin.ui.share.ToolbarMenu
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var titleNav by remember { mutableStateOf(MainDestinations.PersonalInfoScreen.label) }
                    val scope = rememberCoroutineScope()
                    val navController = rememberNavController()
                    val scaffoldState =
                        rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))
                    var isHomeRoute by remember { mutableStateOf(false) }
                    val isAuthUserState by authViewModel.isUserAuth.collectAsState()
                    navController.addOnDestinationChangedListener { _, destination, _ ->
                        isHomeRoute = MainDestinations.isHomeRoute(destination.route)
                        titleNav = MainDestinations.getLabel(destination.route)
                    }
                    when (val isAuthUser = isAuthUserState) {
                        is Resource.Success -> {
                            loading = false
                            Scaffold(
                                scaffoldState = scaffoldState,
                                topBar = {
                                    if (isHomeRoute)
                                        ToolbarMenu(title = titleNav) {
                                            scope.launch {
                                                scaffoldState.drawerState.open()
                                            }
                                        }
                                },
                                drawerContent = {
                                    NavigatorDrawer(
                                        drawerState = scaffoldState.drawerState,
                                        scope = scope,
                                        navController = navController,
                                    ) {
                                        scope.launch {
                                            scaffoldState.drawerState.close()
                                        }
                                        authViewModel.logOut()
                                    }
                                },
                                drawerGesturesEnabled = isAuthUser.data
                            ) { paddingValues ->
                                DestinationsNavHost(
                                    startRoute = if (isAuthUser.data) InfoProfileDestination else AuthScreenDestination,
                                    navGraph = NavGraphs.root,
                                    navController = navController,
                                    modifier = Modifier.padding(paddingValues),
                                    dependenciesContainerBuilder = {
                                        dependency(authViewModel)
                                    }
                                )
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

