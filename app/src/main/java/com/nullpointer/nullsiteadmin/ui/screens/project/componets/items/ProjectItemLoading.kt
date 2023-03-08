package com.nullpointer.nullsiteadmin.ui.screens.project.componets.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.utils.myShimmer
import com.valentinilk.shimmer.Shimmer

@Composable
fun ProjectItemLoading(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .myShimmer(shimmer)
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextFakeProject(
                modifier = Modifier.myShimmer(shimmer)
            )
        }
    }
}

@Composable
private fun TextFakeProject(
    modifier: Modifier = Modifier
) {
    val numberLines = remember {
        (1..3).random()
    }
    repeat(numberLines) {
        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth()
                .then(modifier)
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}