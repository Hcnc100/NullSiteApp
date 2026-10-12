package com.nullpointer.nullsiteadmin.ui.screens.home.componets

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.MainDestinationsProvider

@Composable
fun ItemNavDrawer(
    currentDestination: MainDestinations,
    isSelected: Boolean,
    onDestinationClicked: () -> Unit,
) {

    NavigationDrawerItem(
        selected = isSelected,
        onClick = onDestinationClicked,
        modifier = Modifier.padding(horizontal = 12.dp),
        label = { Text(currentDestination.labelRes?.let { stringResource(it) } ?: currentDestination.label) },
        icon = { Icon(painterResource(currentDestination.icon), contentDescription = null) },
        shape = MaterialTheme.shapes.medium,
    )
}

@SimplePreview
@Composable
fun ItemNavDrawerUnselectPreview(
    @PreviewParameter(MainDestinationsProvider::class)
    currentDestination: MainDestinations,
) {
    ItemNavDrawer(
        isSelected = false,
        onDestinationClicked = {},
        currentDestination = currentDestination
    )
}

@SimplePreview
@Composable
fun ItemNavDrawerSelectPreview(
    @PreviewParameter(MainDestinationsProvider::class)
    currentDestination: MainDestinations,
) {
    ItemNavDrawer(
        isSelected = true,
        onDestinationClicked = {},
        currentDestination = currentDestination
    )
}
