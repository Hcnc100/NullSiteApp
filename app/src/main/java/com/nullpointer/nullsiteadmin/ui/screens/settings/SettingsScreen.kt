package com.nullpointer.nullsiteadmin.ui.screens.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.preview.provider.BooleanProvider
import com.nullpointer.nullsiteadmin.ui.screens.settings.viewModel.SettingsViewModel
import com.nullpointer.nullsiteadmin.ui.screens.settings.viewModel.componets.EnableBiometricSwitchTitle
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.nullpointer.runningcompose.ui.preview.config.OrientationPreviews
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    settingsScreenState: SimpleScreenState = rememberSimpleScreenState()
) {
    val isBiometricEnabled by settingsViewModel.isBiometricEnabled.collectAsState()

    LaunchedEffect(key1 = Unit) {
        settingsViewModel.messageErrorSettings.collect(settingsScreenState::showSnackMessage)
    }

    SettingsScreen(
        isBiometricEnabled = isBiometricEnabled,
        scaffoldState = settingsScreenState.scaffoldState,
        isBiometricAvailable = settingsViewModel.isBiometricAvailable,
        changeBiometricEnabled = settingsViewModel::changeBiometricEnabled
    )

}

@Composable
fun SettingsScreen(
    isBiometricEnabled: Boolean,
    scaffoldState: ScaffoldState,
    isBiometricAvailable: Boolean,
    changeBiometricEnabled: (Boolean) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
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
                    style = MaterialTheme.typography.caption.copy(
                        color = MaterialTheme.colors.error
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
        scaffoldState = rememberScaffoldState(),
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
        scaffoldState = rememberScaffoldState(),
        isBiometricAvailable = false,
        changeBiometricEnabled = {}
    )
}

