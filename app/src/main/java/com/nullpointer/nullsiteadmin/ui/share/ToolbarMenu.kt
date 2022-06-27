package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.nullpointer.nullsiteadmin.R

@Composable
fun ToolbarMenu(
    title: String,
    actionClickMenu: () -> Unit
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        title = { Text(text = title, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = actionClickMenu) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun ToolbarBack(
    title: String,
    actionBack: () -> Unit
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        title = { Text(text = title, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = actionBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    )
}
