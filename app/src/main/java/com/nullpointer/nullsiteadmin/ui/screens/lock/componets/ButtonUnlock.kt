package com.nullpointer.nullsiteadmin.ui.screens.lock.componets

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.BiometricLockState
import com.nullpointer.nullsiteadmin.models.biometric.data.BiometricLockData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun ButtonLaunchBiometric(
    modifier: Modifier = Modifier,
    biometricLockData: BiometricLockData,
    actionLaunchBiometric: () -> Unit
) {

    val isEnable = remember {
        derivedStateOf {
            biometricLockData.biometricLockState == BiometricLockState.LOCK && biometricLockData.timeOutLock <= 0
        }
    }

    Button(
        modifier = modifier,
        onClick = actionLaunchBiometric,
        enabled = isEnable.value
    ) {
        Text(text = stringResource(R.string.text_button_unlock))
    }
}

@SimplePreview
@Composable
private fun ButtonLaunchBiometricEnablePreview() {
    ButtonLaunchBiometric(
        biometricLockData = BiometricLockData(
            timeOutLock = 10,
            biometricLockState = BiometricLockState.LOCK
        ),
        actionLaunchBiometric = {}
    )
}

@SimplePreview
@Composable
private fun ButtonLaunchBiometricDisablePreview() {
    ButtonLaunchBiometric(
        biometricLockData = BiometricLockData(
            timeOutLock = 0,
            biometricLockState = BiometricLockState.LOCKED_BY_MANY_INTENTS
        ),
        actionLaunchBiometric = {}
    )
}