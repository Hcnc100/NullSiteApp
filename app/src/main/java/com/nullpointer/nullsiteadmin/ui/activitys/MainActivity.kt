package com.nullpointer.nullsiteadmin.ui.activitys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nullpointer.nullsiteadmin.ui.screens.main.MainScreen
import com.nullpointer.nullsiteadmin.ui.theme.NullSiteAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
    }
}


