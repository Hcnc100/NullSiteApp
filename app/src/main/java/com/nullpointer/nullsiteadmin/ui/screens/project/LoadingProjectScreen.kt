package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingProgressScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        repeat(4) {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .size(220.dp)
                            .shimmer()
                    ) {}
                    Spacer(modifier = Modifier.height(20.dp))
                    repeat((1..3).random()) {
                        Card(
                            modifier = Modifier
                                .height(20.dp)
                                .fillMaxWidth()
                                .shimmer()
                        ) {}
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}