package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.utils.getGrayColor

@Composable
fun CircularProgressAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Card(
            shape = CircleShape,
            backgroundColor = getGrayColor()
        ) {
            CircularProgressIndicator(modifier = Modifier.padding(5.dp))
        }
    }
}