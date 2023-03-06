package com.nullpointer.nullsiteadmin.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    settingsScreenState: SimpleScreenState = rememberSimpleScreenState()
) {
    val isBiometricEnabled by authViewModel.isBiometricEnabled.collectAsState()

    LaunchedEffect(key1 = Unit) {
        authViewModel.messageErrorAuth.collect(settingsScreenState::showSnackMessage)
    }

    SettingsScreen(
        isBiometricEnabled = isBiometricEnabled,
        scaffoldState = settingsScreenState.scaffoldState,
        isBiometricAvailable = authViewModel.isBiometricAvailable,
        changeBiometricEnabled = authViewModel::changeBiometricEnabled
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
                .padding(10.dp)
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                stringResource(R.string.title_biometric),
                style = MaterialTheme.typography.h6.copy(fontSize = 16.sp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .clickable { changeBiometricEnabled(!isBiometricEnabled) }
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.description_enable_biometric),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    modifier = Modifier.weight(.2F),
                    checked = isBiometricEnabled,
                    onCheckedChange = {},
                )
            }
            if (!isBiometricAvailable) {
                Spacer(modifier = Modifier.height(10.dp))
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
