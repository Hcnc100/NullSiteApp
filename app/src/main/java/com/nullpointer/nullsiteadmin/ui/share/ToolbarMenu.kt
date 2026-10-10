package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.material3.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.ime
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.runtime.Composable

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R

/** Reclaim the toolbar's height for large text fields in the compact landscape IME viewport. */
@Composable
fun EditorToolbarBack(title: String, actionBack: () -> Unit) {
    val landscapeIme = LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE &&
        WindowInsets.ime.getBottom(LocalDensity.current) > 0
    if (!landscapeIme) ToolbarBack(title, actionBack)
}

@Composable
fun ToolbarMenu(
    title: String,
    actionClickMenu: () -> Unit
) {
    TopAppBar(
        windowInsets = WindowInsets.statusBars,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = { IconMenu(actionClickMenu) }
    )
}

@Composable
private fun IconBack(actionClickMenu: () -> Unit) {
    IconButton(onClick = actionClickMenu) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(R.string.description_icon_back),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun IconMenu(actionClickMenu: () -> Unit) {
    IconButton(onClick = actionClickMenu) {
        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = stringResource(R.string.description_drawer_menu),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ToolbarBack(
    title: String,
    actionBack: () -> Unit
) {
    TopAppBar(
        windowInsets = WindowInsets.statusBars,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = actionBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.description_icon_back),
                    tint = MaterialTheme.colorScheme.onSurface
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
        windowInsets = WindowInsets.statusBars,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = actionBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.description_icon_back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(onClick = actionDeleter) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_deleter),
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}
