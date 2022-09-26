package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.utils.myShimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingInfoUser(
    modifier: Modifier = Modifier,
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(150.dp)
                .clip(CircleShape)
                .myShimmer(shimmer)
        )
        repeat(2) {
            Spacer(modifier = Modifier.height(20.dp))
            FakeTextInfo(modifier = Modifier.shimmer(shimmer))
        }
        Spacer(modifier = Modifier.height(20.dp))
        FakeDescription(modifier = Modifier.shimmer(shimmer))
    }
}

@Composable
private fun FakeTextInfo(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .then(modifier)
    )
}

@Composable
private fun FakeDescription(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .then(modifier)
    )
}