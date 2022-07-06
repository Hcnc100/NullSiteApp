package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R

@Composable
fun ToolbarMenu(
    title: String,
    actionClickMenu: () -> Unit
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        title = { Text(text = title, color = Color.White) },
        navigationIcon = { IconMenu(actionClickMenu) }
    )
}

@Composable
private fun IconBack(actionClickMenu: () -> Unit) {
    IconButton(onClick = actionClickMenu) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(R.string.description_icon_back),
            tint = Color.White
        )
    }
}

@Composable
private fun IconMenu(actionClickMenu: () -> Unit) {
    IconButton(onClick = actionClickMenu) {
        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = stringResource(R.string.description_drawer_menu),
            tint = Color.White
        )
    }
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
                    contentDescription = stringResource(R.string.description_icon_back),
                    tint = Color.White
                )
            }
        }
    )
}


@Composable
fun ToolbarBackWithDeleter(
    title: String,
    contentDescription: String,
    actionBack: () -> Unit,
    actionDeleter: () -> Unit,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        title = { Text(text = title, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = actionBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.description_icon_back),
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = actionDeleter) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_deleter),
                    contentDescription = contentDescription,
                    tint = Color.White
                )
            }
        }
    )
}