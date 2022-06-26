package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations
import com.nullpointer.nullsiteadmin.ui.screens.destinations.DirectionDestination
import com.nullpointer.nullsiteadmin.ui.screens.navDestination
import com.ramcosta.composedestinations.navigation.navigateTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavigatorDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavController
) {
    Drawer(
        navController = navController,
        onDestinationClicked = { route ->
            navController.navigateTo(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            scope.launch {
                drawerState.close()
            }
        }
    )
}

@Composable
private fun Drawer(
    navController: NavController,
    onDestinationClicked: (destination: DirectionDestination) -> Unit
) {
    val currentDestination = navController.currentBackStackEntryAsState()
        .value?.navDestination
    Column {
        AsyncImage(
            model = R.drawable.cover2,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.8f)
        )
        Spacer(modifier = Modifier.height(10.dp))
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
                    contentDescription = "",
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

private fun getColorSelected(
    isSelected: Boolean,
    colorSelected: Color,
    normalColor: Color = Color.Unspecified
): Color = if (isSelected) colorSelected else normalColor

