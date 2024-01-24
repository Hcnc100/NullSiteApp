package com.nullpointer.nullsiteadmin.ui.screens.email.detailsEmail.componets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.shared.LottieContainer

@Composable
fun ContainerAnimateEmail(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieContainer(
            modifier = Modifier.fillMaxWidth(),
            animation = R.raw.email
        )
    }
}

@SimplePreview
@Composable
private fun ContainerAnimateEmailPreview() {
    ContainerAnimateEmail()
}
