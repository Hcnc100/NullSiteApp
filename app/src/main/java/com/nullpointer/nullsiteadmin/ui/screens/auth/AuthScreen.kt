package com.nullpointer.nullsiteadmin.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.auth.viewModel.AuthFieldViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.FocusScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberFocusScreenState
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.PasswordTextSavable
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph(start = true)
@Destination
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    authFieldVM: AuthFieldViewModel = hiltViewModel(),
    authScreenState: FocusScreenState = rememberFocusScreenState()
) {
    LaunchedEffect(key1 = Unit) {
        authViewModel.messageErrorAuth.collect(authScreenState::showSnackMessage)
    }
    Scaffold(
        scaffoldState = authScreenState.scaffoldState,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ButtonAuth(isAuthenticating = authViewModel.isAuthenticating) {
                authScreenState.hiddenKeyBoard()
                authFieldVM.getDataAuth().let {
                    authViewModel.authWithEmailAndPassword(it)
                }
            }
        }
    ) { paddingValues ->
        ContainerAuthScreen(
            modifier = Modifier.padding(paddingValues)
        ) {
            LogoApp(modifier = Modifier.padding(vertical = 70.dp))
            Spacer(modifier = Modifier.height(20.dp))
            FieldLogin(
                email = authFieldVM.emailAdmin,
                modifier = Modifier.width(300.dp),
                password = authFieldVM.passwordAdmin,
                isEnabled = !authViewModel.isAuthenticating,
                moveNextField = authScreenState::moveNextFocus,
                actionSignIn = {
                    authScreenState.hiddenKeyBoard()
                    authFieldVM.getDataAuth().let {
                        authViewModel.authWithEmailAndPassword(it)
                    }
                }
            )
        }
    }
}


@Composable
private fun ContainerAuthScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun ButtonAuth(
    modifier: Modifier = Modifier,
    isAuthenticating: Boolean,
    actionClick: () -> Unit,
) {
    Box(modifier = modifier) {
        if (isAuthenticating) {
            CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
        } else {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(R.string.text_auth_button)) },
                onClick = actionClick
            )
        }
    }
}

@Composable
private fun FieldLogin(
    isEnabled: Boolean,
    actionSignIn: () -> Unit,
    moveNextField: () -> Unit,
    email: PropertySavableString,
    modifier: Modifier = Modifier,
    password: PropertySavableString
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ContainerFieldAuth {
            EditableTextSavable(
                isEnabled = isEnabled,
                valueProperty = email,
                modifier = Modifier.padding(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                    capitalization = KeyboardCapitalization.None
                ),
                keyboardActions = KeyboardActions(onNext = { moveNextField() })
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        ContainerFieldAuth {
            PasswordTextSavable(
                isEnabled = isEnabled,
                valueProperty = password,
                modifier = Modifier.padding(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { actionSignIn() })
            )
        }
    }
}

@Composable
private fun ContainerFieldAuth(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.background),
        content = content
    )
}

@Composable
private fun LogoApp(
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = R.drawable.ic_safe,
        contentDescription = stringResource(R.string.description_logo_app),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize()
            .size(130.dp)
    )
}