package com.nullpointer.nullsiteadmin.ui.navigator

import androidx.annotation.DrawableRes
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.screens.destinations.*

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
        destinations = ProjectScreenDestination
    ),

    ContactScreen(
        label = "Contact",
        icon = R.drawable.ic_comment,
        destinations = EmailScreenDestination
    ),

    PreviewScreen(
        label = "Preview",
        icon = R.drawable.ic_web,
        destinations = PreviewScreenDestination
    );

    companion object {
        fun isHomeRoute(route: String?): Boolean {
            if (route == null) return false
            return values().find { it.destinations.route == route } != null
        }

        fun getLabel(route: String?): String {
            if (route == null) return ""
            return values().find { it.destinations.route == route }?.label ?: ""
        }
    }
}