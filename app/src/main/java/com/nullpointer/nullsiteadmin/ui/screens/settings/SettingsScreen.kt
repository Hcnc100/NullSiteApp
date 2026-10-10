package com.nullpointer.nullsiteadmin.ui.screens.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.preview.provider.BooleanProvider
import com.nullpointer.nullsiteadmin.ui.screens.settings.viewModel.SettingsViewModel
import com.nullpointer.nullsiteadmin.ui.screens.settings.viewModel.componets.EnableBiometricSwitchTitle
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    settingsScreenState: SimpleScreenState = rememberSimpleScreenState()
) {
    val isBiometricEnabled by settingsViewModel.isBiometricEnabled.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        settingsViewModel.messageErrorSettings.collect(settingsScreenState::showSnackMessage)
    }

    SettingsScreen(
        isBiometricEnabled = isBiometricEnabled,
        snackbarHostState = settingsScreenState.snackbarHostState,
        isBiometricAvailable = settingsViewModel.isBiometricAvailable,
        changeBiometricEnabled = settingsViewModel::changeBiometricEnabled
    )

}

@Composable
fun SettingsScreen(
    isBiometricEnabled: Boolean,
    snackbarHostState: SnackbarHostState,
    isBiometricAvailable: Boolean,
    changeBiometricEnabled: (Boolean) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EnableBiometricSwitchTitle(
                isBiometricEnabled = isBiometricEnabled,
                isBiometricAvailable = isBiometricAvailable,
                changeBiometricEnabled = changeBiometricEnabled,
            )
            if (!isBiometricAvailable) {
                Text(
                    text = stringResource(id = R.string.biometric_no_avariable),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    }
}

@OrientationPreviews
@Composable
fun SettingsScreenBiometricAvailablePreview(
    @PreviewParameter(BooleanProvider::class)
    isBiometricEnabled: Boolean,
) {
    SettingsScreen(
        isBiometricEnabled = isBiometricEnabled,
        snackbarHostState = remember { SnackbarHostState() },
        isBiometricAvailable = true,
        changeBiometricEnabled = {}
    )
}


@OrientationPreviews
@Composable
fun SettingsScreenBiometricDisablePreview(
    @PreviewParameter(BooleanProvider::class)
    isBiometricEnabled: Boolean,
) {
    SettingsScreen(
        isBiometricEnabled = isBiometricEnabled,
        snackbarHostState = remember { SnackbarHostState() },
        isBiometricAvailable = false,
        changeBiometricEnabled = {}
    )
}

