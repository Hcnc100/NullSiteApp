package com.nullpointer.nullsiteadmin.ui.navigator

import androidx.annotation.DrawableRes
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.screens.destinations.DirectionDestination
import com.nullpointer.nullsiteadmin.ui.screens.destinations.InfoProfileDestination

enum class MainDestinations(
    val label: String = "",
    @DrawableRes
    val icon: Int,
    val destinations: DirectionDestination
) {
    PersonalInfoScreen(
        label = "Personal Info",
        icon = R.drawable.ic_home,
        destinations = InfoProfileDestination
    ),

    ProjectsScreen(
        label = "Projects",
        icon = R.drawable.ic_work,
        destinations = InfoProfileDestination
    ),

    ContactScreen(
        label = "Contact",
        icon = R.drawable.ic_comment,
        destinations = InfoProfileDestination
    ),

    PreviewScreen(
        label = "Preview",
        icon = R.drawable.ic_web,
        destinations = InfoProfileDestination
    );

    companion object {
        fun isHomeRoute(route: String?): Boolean {
            if (route == null) return false
            return values().find { it.destinations.route == route } != null
        }
    }
}