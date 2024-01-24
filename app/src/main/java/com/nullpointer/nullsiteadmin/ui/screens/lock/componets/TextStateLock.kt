package com.nullpointer.nullsiteadmin.ui.screens.lock.componets

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.BiometricLockState
import com.nullpointer.nullsiteadmin.models.biometric.data.BiometricLockData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun TextStateLock(
    biometricLockData: BiometricLockData
) {
    when (biometricLockData.biometricState) {
        BiometricLockState.LOCKED_BY_MANY_INTENTS -> Text(
            text = stringResource(R.string.text_biometric_disable),
            textAlign = TextAlign.Center
        )

        BiometricLockState.LOCKED_BY_TIME_OUT -> Text(
            stringResource(R.string.text_time_out_lock, biometricLockData.timeOutLock),
            textAlign = TextAlign.Center
        )

        else -> Text(
            text = stringResource(R.string.text_lauch_biometric),
            textAlign = TextAlign.Center
        )
    }
}

@SimplePreview
@Composable
private fun TextStateLockPreview() {
    TextStateLock(
        biometricLockData = BiometricLockData(
            timeOutLock = 10,
            biometricState = BiometricLockState.LOCK
        )
    )
}

@SimplePreview
@Composable
private fun TextStateLockDisablePreview() {
    TextStateLock(
        biometricLockData = BiometricLockData(
            timeOutLock = 0,
            biometricState = BiometricLockState.LOCKED_BY_MANY_INTENTS
        )
    )
}

@SimplePreview
@Composable
private fun TextStateLockTimeOutPreview() {
    TextStateLock(
        biometricLockData = BiometricLockData(
            timeOutLock = 10,
            biometricState = BiometricLockState.LOCKED_BY_TIME_OUT
        )
    )
}