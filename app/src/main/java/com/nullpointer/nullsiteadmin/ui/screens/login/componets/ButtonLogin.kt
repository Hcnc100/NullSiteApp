package com.nullpointer.nullsiteadmin.ui.screens.login.componets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    Box(
        modifier = modifier.animateContentSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            ExtendedFloatingActionButton(onClick = actionClick) {
                Text(text = stringResource(R.string.text_auth_button))
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
