package com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables.infoUser

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.BooleanProvider

@Composable
fun LoadingIconImageProfile(
    showLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_person),
            contentDescription = null,
            tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
            modifier = Modifier.fillMaxSize(0.7f)
        )
        if (showLoading)
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                strokeWidth = 5.dp
            )
    }
}

@SimplePreview
@Composable
private fun PreviewLoadingIconImageProfile(
    @PreviewParameter(BooleanProvider::class)
    showLoading: Boolean,
) {
    LoadingIconImageProfile(
        showLoading = showLoading,
        modifier = Modifier.size(200.dp)
    )
}
