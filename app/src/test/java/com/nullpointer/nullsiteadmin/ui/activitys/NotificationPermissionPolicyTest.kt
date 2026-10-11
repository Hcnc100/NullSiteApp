package com.nullpointer.nullsiteadmin.ui.activitys

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPermissionPolicyTest {
    @Test fun firstLaunchOnAndroid13OrLaterRequestsPermission() {
        assertTrue(NotificationPermissionPolicy.shouldRequest(33, false, false))
        assertTrue(NotificationPermissionPolicy.shouldRequest(36, false, false))
    }

    @Test fun olderAndroidNeverRequestsRuntimeNotificationPermission() {
        assertFalse(NotificationPermissionPolicy.shouldRequest(21, false, false))
        assertFalse(NotificationPermissionPolicy.shouldRequest(32, false, false))
    }

    @Test fun grantedPermissionDoesNotShowPrompt() {
        assertFalse(NotificationPermissionPolicy.shouldRequest(33, true, false))
        assertFalse(NotificationPermissionPolicy.shouldRequest(36, true, true))
    }

    @Test fun denialDismissalOrRecreationDoesNotRepeatPrompt() {
        assertFalse(NotificationPermissionPolicy.shouldRequest(33, false, true))
        assertFalse(NotificationPermissionPolicy.shouldRequest(36, false, true))
    }
}
