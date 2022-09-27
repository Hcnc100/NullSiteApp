package com.nullpointer.nullsiteadmin.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph
@Destination
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel
) {
    val isBiometricEnabled by authViewModel.isBiometricEnabled.collectAsState()
    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            SwitchBiometric(
                isBiometricEnabled = isBiometricEnabled,
                isBiometricAvailable = authViewModel.isBiometricAvailable,
                changeBiometricEnabled = authViewModel::changeBiometricEnabled
            )
        }
    }
}

@Composable
private fun SwitchBiometric(
    isBiometricEnabled: Boolean,
    isBiometricAvailable: Boolean,
    modifier: Modifier = Modifier,
    changeBiometricEnabled: (Boolean) -> Unit
) {
    Column(modifier = modifier.padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(.8f)) {
                Text("Bloqueo por huella dactilar", style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Si habilitas esta opvion para entrar a la app se requerira de tu hella dactilar",
                    style = MaterialTheme.typography.caption
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                modifier = Modifier.weight(.2F),
                checked = isBiometricEnabled,
                onCheckedChange = changeBiometricEnabled,
                enabled = isBiometricAvailable
            )

        }
        if (!isBiometricAvailable) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "El bloqueo por huella dacticar no esta disponible",
                style = MaterialTheme.typography.caption
            )
        }
    }

}