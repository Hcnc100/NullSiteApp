package com.nullpointer.nullsiteadmin.ui.screens.login.componets

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.BooleanProvider

@Composable
fun ButtonLogin(
    isLoading: Boolean,
    actionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Crossfade(
            targetState = isLoading,
            label = "ANIMATE_LOGIN_BUTTON"
        ) { isLoading ->
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
            } else {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(R.string.text_auth_button)) },
                    onClick = actionClick
                )
            }
        }
    }
}

@SimplePreview
@Composable
private fun ButtonLoginPreview(
    @PreviewParameter(BooleanProvider::class)
    isLoading: Boolean
) {
    ButtonLogin(
        isLoading = isLoading,
        actionClick = { /*TODO*/ }
    )
}