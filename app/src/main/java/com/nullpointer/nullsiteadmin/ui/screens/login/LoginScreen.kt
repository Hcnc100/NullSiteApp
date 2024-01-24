package com.nullpointer.nullsiteadmin.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.login.componets.FormLogin
import com.nullpointer.nullsiteadmin.ui.screens.login.componets.LogoApp
import com.nullpointer.nullsiteadmin.ui.screens.login.states.AuthAction
import com.nullpointer.nullsiteadmin.ui.screens.login.states.AuthAction.LOGIN
import com.nullpointer.nullsiteadmin.ui.screens.login.states.AuthAction.MOVE_NEXT
import com.nullpointer.nullsiteadmin.ui.screens.login.viewModel.LoginScreenViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.FocusScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberFocusScreenState
import com.nullpointer.runningcompose.ui.preview.config.OrientationPreviews
import com.ramcosta.composedestinations.annotation.Destination

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
    orientation: Int = LocalConfiguration.current.orientation
) {
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            LogoApp()
            Spacer(modifier = Modifier.height(150.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(min = 200.dp, max = 300.dp)
            ) {
                FormLogin(
                    email = email,
                    password = password,
                    isAuthenticating = isAuthenticating,
                    onAuthAction = onAuthAction
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

//        BoxWithConstraints(
//            modifier = Modifier.padding(it)
//        ) {
//            val height = maxHeight
//            when (orientation) {
//                ORIENTATION_PORTRAIT -> {
//
//                }
//
//                else -> {
//                    Row(
//                        modifier = Modifier
//                            .verticalScroll(rememberScrollState())
//                            .height(height),
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        LogoApp()
//                        Column {
//                            Column(
//                                modifier = Modifier
//                                    .padding(horizontal = 20.dp)
//                                    .requiredWidthIn(
//                                        min = 200.dp,
//                                        max = 300.dp
//                                    ),
//                                verticalArrangement = Arrangement.spacedBy(20.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                FormLogin(
//                                    email = email,
//                                    password = password,
//                                    isAuthenticating = isAuthenticating,
//                                    onAuthAction = onAuthAction
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }

}

@OrientationPreviews
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        email = PropertySavableString.example,
        password = PropertySavableString.example,
        scaffoldState = rememberScaffoldState(),
        isAuthenticating = false,
        onAuthAction = { /*TODO*/ }
    )
}

