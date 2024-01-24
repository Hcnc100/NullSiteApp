package com.nullpointer.nullsiteadmin.ui.screens.home.componets

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.MainDestinationsProvider

@Composable
fun ItemNavDrawer(
    currentDestination: MainDestinations,
    isSelected: Boolean,
    onDestinationClicked: () -> Unit,
) {

    val colorSelectedRow by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colors.secondary else Color.Unspecified,
        label = "COLOR_SELECTED_ITEM_DRAWER",
    )

    val colorSelectedText by animateColorAsState(
        targetValue = if (isSelected) Color.Black else MaterialTheme.colors.onBackground,
        label = "COLOR_SELECTED_ITEM_DRAWER",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDestinationClicked() }
            .background(colorSelectedRow)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            painter = painterResource(id = currentDestination.icon),
            contentDescription = stringResource(R.string.description_icon_draw_menu),
            tint = colorSelectedText
        )
        Text(
            text = currentDestination.label,
            color = colorSelectedText
        )
    }
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