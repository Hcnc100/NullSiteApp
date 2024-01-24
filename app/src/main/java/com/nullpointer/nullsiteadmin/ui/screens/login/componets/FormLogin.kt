package com.nullpointer.nullsiteadmin.ui.screens.login.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.login.states.AuthAction
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.nullpointer.nullsiteadmin.ui.share.PasswordTextSavable

@Composable
fun FormLogin(
    email: PropertySavableString,
    password: PropertySavableString,
    isAuthenticating: Boolean,
    onAuthAction: (AuthAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ContainerFieldAuth {
            EditableTextSavable(
                modifier = Modifier.padding(10.dp),
                isEnabled = !isAuthenticating,
                valueProperty = email,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                    capitalization = KeyboardCapitalization.None
                ),
                keyboardActions = KeyboardActions(onNext = { onAuthAction(AuthAction.MOVE_NEXT) })
            )
        }
        ContainerFieldAuth {
            PasswordTextSavable(
                modifier = Modifier.padding(10.dp),
                isEnabled = !isAuthenticating,
                valueProperty = password,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onAuthAction(AuthAction.LOGIN) })
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        ButtonLogin(
            isLoading = isAuthenticating,
            actionClick = { onAuthAction(AuthAction.LOGIN) }
        )
    }
}

@SimplePreview
@Composable
fun FormLoginPreview() {
    FormLogin(
        email = PropertySavableString.example,
        password = PropertySavableString.example,
        isAuthenticating = false,
        onAuthAction = { /*TODO*/ }
    )
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