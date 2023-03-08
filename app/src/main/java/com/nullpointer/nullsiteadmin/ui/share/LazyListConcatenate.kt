package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.utils.getGrayColor

@Composable
fun CircularProgressAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier
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

@Composable
fun LazyListConcatenate(
    isConcatenate: Boolean,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    actionConcatenate: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: LazyListScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            content = content,
            contentPadding = paddingValues,
            verticalArrangement = verticalArrangement,
        )
        CircularProgressAnimation(
            isVisible = isConcatenate,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.BottomCenter)
        )
    }
    lazyListState.OnBottomReached(onLoadMore = actionConcatenate)
}