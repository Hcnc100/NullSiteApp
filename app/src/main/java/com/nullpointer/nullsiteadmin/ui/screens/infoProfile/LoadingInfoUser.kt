package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingInfoUser(
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = CircleShape,
            backgroundColor = Color.Gray,
            modifier = Modifier
                .padding(20.dp)
                .size(150.dp)
                .shimmer()
        ) {}
        repeat(2) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                backgroundColor = Color.Gray,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .shimmer()
            ) {}
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(10.dp),
            backgroundColor = Color.Gray,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .shimmer()
        ) {}

    }
}