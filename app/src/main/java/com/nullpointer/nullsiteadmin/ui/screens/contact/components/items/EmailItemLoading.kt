package com.nullpointer.nullsiteadmin.ui.screens.contact.components.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.utils.myShimmer
import com.valentinilk.shimmer.Shimmer


@Composable
fun EmailItemLoading(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            IconFakeLoading(
                modifier = Modifier.myShimmer(shimmer)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                FakeText(modifier = Modifier.myShimmer(shimmer))
                Spacer(modifier = Modifier.padding(10.dp))
                FakeText(modifier = Modifier.myShimmer(shimmer))
            }
        }
    }
}

@Composable
private fun IconFakeLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(modifier)
    )
}

@Composable
private fun FakeText(
    modifier: Modifier = Modifier,
) {
    val width = remember { (100..300).random() }

    Box(
        modifier = Modifier
            .width(width.dp)
            .clip(RoundedCornerShape(4.dp))
            .height(15.dp)
            .then(modifier),
    )
}