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
import com.nullpointer.nullsiteadmin.states.AuthState
import com.nullpointer.nullsiteadmin.states.AuthState.LOGIN
import com.nullpointer.nullsiteadmin.states.AuthState.MOVE_NEXT
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.auth.viewModel.AuthFieldViewModel
import com.nullpointer.nullsiteadmin.ui.screens.states.FocusScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberFocusScreenState
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.PasswordTextSavable
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.merge

@RootNavGraph(start = true)
@Destination
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    authFieldVM: AuthFieldViewModel = hiltViewModel(),
    authScreenState: FocusScreenState = rememberFocusScreenState()
) {
    LaunchedEffect(key1 = Unit) {
        merge(
            authFieldVM.messageCredentials,
            authViewModel.messageErrorAuth
        ).collect(authScreenState::showSnackMessage)
    }

    AuthScreen(
        email = authFieldVM.emailAdmin,
        password = authFieldVM.passwordAdmin,
        scaffoldState = authScreenState.scaffoldState,
        isAuthenticating = authViewModel.isAuthenticating,
        actionAuth = { action ->
            when (action) {
                MOVE_NEXT -> authScreenState.moveNextFocus()
                LOGIN -> {
                    authFieldVM.getDataAuth()?.let { userCredentials ->
                        authScreenState.hiddenKeyBoard()
                        authViewModel.login(userCredentials)
                    }
                }
            }
        }
    )
}


@Composable
fun AuthScreen(
    scaffoldState: ScaffoldState,
    isAuthenticating: Boolean,
    email: PropertySavableString,
    password: PropertySavableString,
    actionAuth: (AuthState) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        BoxWithConstraints(
            modifier = Modifier.padding(it)
        ) {
            val height = maxHeight
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .height(height),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AsyncImage(
                    model = R.drawable.ic_safe,
                    contentDescription = stringResource(R.string.description_logo_app),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                        .size(130.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ContainerFieldAuth {
                        EditableTextSavable(
                            isEnabled = !isAuthenticating,
                            valueProperty = email,
                            modifier = Modifier.padding(10.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Email,
                                capitalization = KeyboardCapitalization.None
                            ),
                            keyboardActions = KeyboardActions(onNext = { actionAuth(MOVE_NEXT) })
                        )
                    }
                    ContainerFieldAuth {
                        PasswordTextSavable(
                            isEnabled = !isAuthenticating,
                            valueProperty = password,
                            modifier = Modifier.padding(10.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { actionAuth(LOGIN) })
                        )
                    }

                    ButtonAuth(
                        isAuthenticating = isAuthenticating,
                        actionClick = { actionAuth(LOGIN) }
                    )
                }
            }
        }
    }
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
