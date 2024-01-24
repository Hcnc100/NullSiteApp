package com.nullpointer.nullsiteadmin.ui.screens.login.componets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun LogoApp(
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

@SimplePreview
@Composable
fun LogoAppPreview() {
    LogoApp()
}