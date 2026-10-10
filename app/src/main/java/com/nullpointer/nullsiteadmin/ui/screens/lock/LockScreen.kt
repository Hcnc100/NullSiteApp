package com.nullpointer.nullsiteadmin.ui.screens.lock


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.BiometricLockState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.biometric.data.BiometricLockData
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.lock.componets.ButtonLaunchBiometric
import com.nullpointer.nullsiteadmin.ui.screens.lock.componets.TextStateLock
import com.nullpointer.nullsiteadmin.ui.screens.lock.viewModel.LockScreenViewModel
import com.nullpointer.nullsiteadmin.ui.screens.shared.BlockProgress
import com.nullpointer.nullsiteadmin.ui.screens.shared.LottieContainer
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph
@Destination
@Composable
fun LockScreen(
    lockScreenViewModel: LockScreenViewModel = hiltViewModel(),
    lockScreenState: SimpleScreenState = rememberSimpleScreenState()
) {

    val biometricLockState by lockScreenViewModel.biometricLockData.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        lockScreenViewModel.launchBiometric()
    }

    LaunchedEffect(key1 = Unit) {
        lockScreenViewModel.messageErrorBiometric.collect(lockScreenState::showSnackMessage)
    }


    LockScreen(
        biometricLockDataState = biometricLockState,
        snackbarHostState = lockScreenState.snackbarHostState,
        launchBiometric = lockScreenViewModel::launchBiometric
    )
}

@Composable
private fun LockScreen(
    launchBiometric: () -> Unit,
    snackbarHostState: SnackbarHostState,
    biometricLockDataState: Resource<BiometricLockData>,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            (biometricLockDataState as? Resource.Success)?.let {
                ButtonLaunchBiometric(
                    biometricLockData = it.data,
                    actionLaunchBiometric = launchBiometric
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        when (biometricLockDataState) {
            Resource.Failure -> Unit
            Resource.Loading -> BlockProgress()
            is Resource.Success -> Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.title_lock_screen),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.size(20.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(250.dp))
                LottieContainer(
                    modifier = Modifier.size(150.dp),
                    animation = R.raw.fingureprint
                )
                TextStateLock(biometricLockData = biometricLockDataState.data)
            }
        }
        }
    }
}

@OrientationPreviews
@Composable
private fun LockScreenDisablePreview() {
    LockScreen(
        snackbarHostState = remember { SnackbarHostState() },
        launchBiometric = {},
        biometricLockDataState = Resource.Success(
            BiometricLockData(
                timeOutLock = 10,
                biometricLockState = BiometricLockState.LOCK
            )
        )
    )
}

@OrientationPreviews
@Composable
private fun LockScreenEnablePreview() {
    LockScreen(
        snackbarHostState = remember { SnackbarHostState() },
        launchBiometric = {},
        biometricLockDataState = Resource.Success(
            BiometricLockData(
                timeOutLock = 0,
                biometricLockState = BiometricLockState.LOCK
            )
        )
    )
}


@OrientationPreviews
@Composable
private fun LockScreenTimeOutPreview() {
    LockScreen(
        snackbarHostState = remember { SnackbarHostState() },
        launchBiometric = {},
        biometricLockDataState = Resource.Success(
            BiometricLockData(
                timeOutLock = 10,
                biometricLockState = BiometricLockState.LOCKED_BY_TIME_OUT
            )
        )
    )
}

@OrientationPreviews
@Composable
private fun LockScreenUndefineLockPreview() {
    LockScreen(
        snackbarHostState = remember { SnackbarHostState() },
        launchBiometric = {},
        biometricLockDataState = Resource.Success(
            BiometricLockData(
                timeOutLock = 10,
                biometricLockState = BiometricLockState.LOCKED_BY_MANY_INTENTS
            )
        )
    )
}


@OrientationPreviews
@Composable
private fun LockScreenLoadingPreview() {
    LockScreen(
        snackbarHostState = remember { SnackbarHostState() },
        launchBiometric = {},
        biometricLockDataState = Resource.Loading
    )
}

@SimplePreview
@Composable
private fun LockScreenErrorPreview() {
    LockScreen(
        snackbarHostState = remember { SnackbarHostState() },
        launchBiometric = {},
        biometricLockDataState = Resource.Failure
    )
}
