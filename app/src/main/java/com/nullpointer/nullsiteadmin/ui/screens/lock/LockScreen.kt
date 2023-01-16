package com.nullpointer.nullsiteadmin.ui.screens.lock


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.BiometricState
import com.nullpointer.nullsiteadmin.actions.BiometricState.*
import com.nullpointer.nullsiteadmin.presentation.AuthViewModel
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.animation.LottieContainer
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph
@Destination
@Composable
fun LockScreen(
    authViewModel: AuthViewModel
) {

    val timeOut by authViewModel.timeOutLocked.collectAsState()
    val stateLocked by authViewModel.stateLocked.collectAsState()
    val enableBiometrics by remember(timeOut, stateLocked) {
        derivedStateOf { stateLocked == Locked && timeOut == 0L }
    }
    LaunchedEffect(key1 = Unit) {
        authViewModel.launchBiometric()
    }
    LockScreen(
        timeOutLocked = timeOut,
        stateLocked = stateLocked,
        isEnableLaunchBiometrics = enableBiometrics,
        launchBiometric = authViewModel::launchBiometric
    )
}

@Composable
private fun LockScreen(
    timeOutLocked: Long,
    launchBiometric: () -> Unit,
    stateLocked: BiometricState,
    isEnableLaunchBiometrics: Boolean
) {
    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ButtonLaunchBiometric(
                isEnable = isEnableLaunchBiometrics,
                actionLaunchBiometric = launchBiometric
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title_lock_screen),
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.size(20.dp))
            Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = null)
            Spacer(modifier = Modifier.height(250.dp))
            LottieContainer(
                modifier = Modifier.size(150.dp),
                animation = R.raw.fingureprint
            )

            when (stateLocked) {
                Locked -> Text(
                    text = stringResource(R.string.text_lauch_biometric),
                    textAlign = TextAlign.Center
                )
                DisabledTimeOut -> Text(
                    text = stringResource(R.string.text_biometric_disable),
                    textAlign = TextAlign.Center
                )
                LockedTimeOut -> Text(
                    stringResource(R.string.text_time_out_lock, timeOutLocked),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ButtonLaunchBiometric(
    isEnable: Boolean,
    modifier: Modifier = Modifier,
    actionLaunchBiometric: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = actionLaunchBiometric,
        enabled = isEnable
    ) {
        Text(text = stringResource(R.string.text_button_unlock))
    }
}