package com.nullpointer.nullsiteadmin.ui.activitys

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.content.ContextCompat
import com.nullpointer.nullsiteadmin.ui.screens.main.MainScreen
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Denial must not block access to the app. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var loading = true
        val splash = installSplashScreen()
        enableEdgeToEdge()
        splash.setKeepOnScreenCondition { loading }

        setContent {
            NullSiteAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        actionChangeLoading = {
                            if (loading) loading = false
                        }
                    )
                }
            }
        }
        requestNotificationsOnFirstLaunch()
    }

    private fun requestNotificationsOnFirstLaunch() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val preferences = getSharedPreferences("notification_permission", MODE_PRIVATE)
        val alreadyRequested = preferences.getBoolean("requested", false)
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (NotificationPermissionPolicy.shouldRequest(Build.VERSION.SDK_INT, granted, alreadyRequested)) {
            // Record before launching so recreation or a denial cannot trigger repeated prompts.
            preferences.edit().putBoolean("requested", true).apply()
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (granted && !alreadyRequested) {
            // Respect a grant inherited from an OS upgrade, even if revoked later in Settings.
            preferences.edit().putBoolean("requested", true).apply()
        }
    }
}


