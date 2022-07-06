package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.screens.appDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.Destination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.DirectionDestination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun NavigatorDrawer(
    closeSession: () -> Unit,
    closeDrawer:()->Unit,
    navController: NavController
) {
    Drawer(
        closeSession = {
            closeSession()
            closeDrawer()
        },
        navController =navController,
        onDestinationClicked = { route ->
            navController.navigate(route){
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            closeDrawer()
        }
    )
}

@Composable
private fun Drawer(
    navController: NavController,
    closeSession: () -> Unit,
    onDestinationClicked: (destination: DirectionDestination) -> Unit
) {
    val currentDestination = navController.currentBackStackEntryAsState()
        .value?.appDestination()
    Column(modifier = Modifier.fillMaxHeight()) {
        ImageDraw()
        ListDestinationDrawer(
            currentDestination = currentDestination,
            onDestinationClicked = onDestinationClicked,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        ButtonLogOut(
            actionLogOut = closeSession,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        )

    }
}

@Composable
private fun ButtonLogOut(
    actionLogOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = actionLogOut,
        modifier = modifier
    ) {
        Row {
            Icon(
                painterResource(id = R.drawable.ic_logout),
                contentDescription = stringResource(R.string.description_close_session)
                            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(R.string.text_log_out))
        }

    }
}

@Composable
private fun ListDestinationDrawer(
    currentDestination: Destination?,
    onDestinationClicked: (DirectionDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MainDestinations.values().forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .clickable { onDestinationClicked(it.destinations) }
                    .background(
                        getColorSelected(
                            isSelected = currentDestination == it.destinations,
                            colorSelected = Color.Gray.copy(alpha = 0.4f)
                        )
                    )
                    .padding(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = it.icon),
                    contentDescription = stringResource(R.string.description_icon_draw_menu),
                    tint = getColorSelected(
                        isSelected = currentDestination == it.destinations,
                        colorSelected = MaterialTheme.colors.primary,
                        normalColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = it.label, color = getColorSelected(
                        isSelected = currentDestination == it.destinations,
                        colorSelected = MaterialTheme.colors.primary
                    )
                )
            }
        }
    }

}


@Composable
private fun ImageDraw(
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = R.drawable.cover2,
        contentDescription = stringResource(R.string.description_img_web),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.8f)
    )
}

private fun getColorSelected(
    isSelected: Boolean,
    colorSelected: Color,
    normalColor: Color = Color.Unspecified
): Color = if (isSelected) colorSelected else normalColor

