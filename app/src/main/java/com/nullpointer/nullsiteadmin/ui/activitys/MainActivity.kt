package com.nullpointer.nullsiteadmin.ui.activitys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
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
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
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
                    val isAuthUserState by authViewModel.isUserAuth.collectAsState()
                    when (val isAuthUser = isAuthUserState) {
                        is Resource.Success -> {
                            loading = false
                            MainScreen(
                                authViewModel = authViewModel,
                                isAuthUser = isAuthUser.data
                            )
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
private fun MainScreen(
    mainAppState: MainAppState = rememberMainAppState(),
    authViewModel: AuthViewModel,
    isAuthUser: Boolean
) {
    Scaffold(
        scaffoldState = mainAppState.scaffoldState,
        topBar = {
            ToolbarMenu(
                title = mainAppState.titleNav,
                actionClickMenu = mainAppState::openDrawer,
                actionClickBack = mainAppState.navController::popBackStack,
                isMainScreen = mainAppState.isHomeRoute
            )
        },
        drawerContent = {
            NavigatorDrawer(
                mainAppState = mainAppState
            ) {
                mainAppState.closeDrawer()
                authViewModel.logOut()
            }
        },
        drawerGesturesEnabled = isAuthUser
    ) { paddingValues ->

        val navHostEngine = rememberAnimatedNavHostEngine(
            navHostContentAlignment = Alignment.BottomEnd,
            rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
        )

        DestinationsNavHost(
            startRoute = if (isAuthUser) InfoProfileDestination else AuthScreenDestination,
            navGraph = NavGraphs.root,
            navController = mainAppState.navController,
            engine = navHostEngine,
            modifier = Modifier.padding(paddingValues),
            dependenciesContainerBuilder = {
                dependency(authViewModel)
            }
        )
    }
}

class MainAppState(
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
fun rememberMainAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberAnimatedNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, coroutineScope) {
    MainAppState(scaffoldState, navController, coroutineScope)
}

