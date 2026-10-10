package com.nullpointer.nullsiteadmin.ui.screens.settings.viewModel.componets

import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.BooleanProvider

@Composable
fun EnableBiometricSwitchTitle(
    isBiometricAvailable: Boolean,
    isBiometricEnabled: Boolean,
    modifier: Modifier = Modifier,
    changeBiometricEnabled: (Boolean) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.padding(10.dp)
    ) {
        Text(
            stringResource(R.string.title_biometric),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .heightIn(min = 48.dp)
                .toggleable(
                    value = isBiometricEnabled,
                    enabled = isBiometricAvailable,
                    role = Role.Switch,
                    onValueChange = changeBiometricEnabled,
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.description_enable_biometric),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                enabled = isBiometricAvailable,
                checked = isBiometricEnabled,
                onCheckedChange = null,
            )
        }
    }
}

@SimplePreview
@Composable
private fun EnableBiometricSwitchTitleBiometricAvaliablePreview(
    @PreviewParameter(BooleanProvider::class)
    isBiometricEnabled: Boolean,
) {
    EnableBiometricSwitchTitle(
        isBiometricEnabled = isBiometricEnabled,
        changeBiometricEnabled = {},
        isBiometricAvailable = true
    )
}

@SimplePreview
@Composable
private fun EnableBiometricSwitchTitleBiometricDisablePreview(
    @PreviewParameter(BooleanProvider::class)
    isBiometricEnabled: Boolean,
) {
    EnableBiometricSwitchTitle(
        isBiometricEnabled = isBiometricEnabled,
        changeBiometricEnabled = {},
        isBiometricAvailable = false
    )
}
