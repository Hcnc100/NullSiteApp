package com.nullpointer.nullsiteadmin.ui.screens.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview


@Composable
fun BlockProgress(
    modifier: Modifier= Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}

@SimplePreview
@Composable
private fun BlockProgressPreview() {
    BlockProgress()
}