package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.nullpointer.nullsiteadmin.R

@Composable
fun ToolbarMenu(
    title: String,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = R.drawable.ic_menu), contentDescription = "")
            }
        }
    )
}