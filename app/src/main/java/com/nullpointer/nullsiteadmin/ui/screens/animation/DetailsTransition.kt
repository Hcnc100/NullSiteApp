package com.nullpointer.nullsiteadmin.ui.screens.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditInfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditProjectScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.navDestination
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.utils.destinationSpec

@OptIn(ExperimentalAnimationApi::class)
object DetailsTransition: DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when (initialState.navDestination) {
            EditInfoProfileDestination,EditProjectScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                )
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {

        return when (targetState.navDestination) {
            EditInfoProfileDestination,EditProjectScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(700)
                )
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {

        return when (initialState.navDestination) {
            EditInfoProfileDestination,EditProjectScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(700)
                )
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {

        return when (targetState.navDestination) {
            EditInfoProfileDestination,EditProjectScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(700)
                )
            else -> null
        }
    }
}