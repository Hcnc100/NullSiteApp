package com.nullpointer.nullsiteadmin.ui.activitys

internal object NotificationPermissionPolicy {
    fun shouldRequest(sdk: Int, granted: Boolean, alreadyRequested: Boolean): Boolean =
        sdk >= 33 && !granted && !alreadyRequested
}
