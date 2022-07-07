package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingScreenEmail(modifier: Modifier = Modifier) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        repeat(10) {
            val randomWidth = (100..300)
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier.padding(10.dp)) {
                    FakeText(width = randomWidth.random())
                    Spacer(modifier = Modifier.padding(10.dp))
                    FakeText(width = randomWidth.random())
                }
            }
        }
    }
}

@Composable
private fun FakeText(
    width:Int,
) {
    Card(
        modifier = Modifier
            .width(width.dp)
            .height(25.dp)
            .shimmer(),
        shape = RoundedCornerShape(10.dp)
    ) {}
}