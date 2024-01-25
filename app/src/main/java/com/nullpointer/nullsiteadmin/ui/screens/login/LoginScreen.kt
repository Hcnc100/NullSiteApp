package com.nullpointer.nullsiteadmin.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.screens.login.componets.ButtonLogin
import com.nullpointer.nullsiteadmin.ui.screens.login.componets.FormLogin
import com.nullpointer.nullsiteadmin.ui.screens.login.componets.LogoApp
import com.nullpointer.nullsiteadmin.ui.screens.login.states.AuthAction
import com.nullpointer.nullsiteadmin.ui.screens.login.states.AuthAction.LOGIN
import com.nullpointer.nullsiteadmin.ui.screens.login.states.AuthAction.MOVE_NEXT
import com.nullpointer.nullsiteadmin.ui.screens.login.viewModel.LoginScreenViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.FocusScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberFocusScreenState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true)
@Destination
@Composable
fun LoginScreen(
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(),
    authScreenState: FocusScreenState = rememberFocusScreenState()
) {
    LaunchedEffect(key1 = Unit) {
        loginScreenViewModel.messageErrorLogin.collect(authScreenState::showSnackMessage)
    }

    LoginScreen(
        email = loginScreenViewModel.emailAdmin,
        password = loginScreenViewModel.passwordAdmin,
        scaffoldState = authScreenState.scaffoldState,
        isAuthenticating = loginScreenViewModel.isAuthenticating,
        onAuthAction = { action ->
            when (action) {
                MOVE_NEXT -> authScreenState.moveNextFocus()
                LOGIN -> {
                    loginScreenViewModel.getDataAuth()?.let { userCredentials ->
                        loginScreenViewModel.login(userCredentials)
                    }
                }
            }
        }
    )
}


@Composable
fun LoginScreen(
    scaffoldState: ScaffoldState,
    isAuthenticating: Boolean,
    email: PropertySavableString,
    password: PropertySavableString,
    onAuthAction: (AuthAction) -> Unit,
    orientation: Int = LocalConfiguration.current.orientation,
    configuration: android.content.res.Configuration = LocalConfiguration.current
) {


    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.primary,
    ) { paddingValues ->


        Box(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            when (orientation) {
                android.content.res.Configuration.ORIENTATION_PORTRAIT -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(configuration.screenHeightDp.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        LogoApp()
                        Column(
                            verticalArrangement = Arrangement.spacedBy(50.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            FormLogin(
                                modifier = Modifier.widthIn(min = 200.dp, max = 300.dp),
                                email = email,
                                password = password,
                                isAuthenticating = isAuthenticating,
                                onAuthAction = onAuthAction
                            )
                            ButtonLogin(
                                isLoading = isAuthenticating,
                                actionClick = { onAuthAction(LOGIN) }
                            )
                        }

                    }
                }

                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(configuration.screenHeightDp.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(0.4f),
                            verticalArrangement = Arrangement.spacedBy(50.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LogoApp()
                            ButtonLogin(
                                isLoading = isAuthenticating,
                                actionClick = { onAuthAction(LOGIN) }
                            )
                        }
                        Box(
                            Modifier.weight(0.6f),
                            contentAlignment = Alignment.Center
                        ) {
                            FormLogin(
                                modifier = Modifier.widthIn(min = 200.dp, max = 300.dp),
                                email = email,
                                password = password,
                                isAuthenticating = isAuthenticating,
                                onAuthAction = onAuthAction
                            )
                        }
                    }
                }
            }
        }
    }
}

@OrientationPreviews
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        email = PropertySavableString.example,
        password = PropertySavableString.example,
        scaffoldState = rememberScaffoldState(),
        isAuthenticating = false,
        onAuthAction = { /*TODO*/ }
    )
}

