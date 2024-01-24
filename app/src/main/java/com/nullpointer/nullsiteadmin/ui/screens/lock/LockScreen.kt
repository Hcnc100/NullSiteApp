package com.nullpointer.nullsiteadmin.ui.screens.lock


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.nullpointer.nullsiteadmin.ui.navigator.RootNavGraph
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

@RootNavGraph
@Destination
@Composable
fun LockScreen(
    lockScreenViewModel: LockScreenViewModel = hiltViewModel(),
    lockScreenState: SimpleScreenState = rememberSimpleScreenState()
) {

    val biometricLockState by lockScreenViewModel.biometricLockData.collectAsState()
    LaunchedEffect(key1 = Unit) {
        lockScreenViewModel.launchBiometric()
    }

    LaunchedEffect(key1 = Unit) {
        lockScreenViewModel.messageErrorBiometric.collect(lockScreenState::showSnackMessage)
    }


    LockScreen(
        biometricLockDataState = biometricLockState,
        scaffoldState = lockScreenState.scaffoldState,
        launchBiometric = lockScreenViewModel::launchBiometric
    )
}

@Composable
private fun LockScreen(
    launchBiometric: () -> Unit,
    scaffoldState: ScaffoldState,
    biometricLockDataState: Resource<BiometricLockData>,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            (biometricLockDataState as? Resource.Success)?.let {
                ButtonLaunchBiometric(
                    biometricLockData = it.data,
                    actionLaunchBiometric = launchBiometric
                )
            }
        }
    ) {

        when (biometricLockDataState) {
            Resource.Failure -> Unit
            Resource.Loading -> BlockProgress()
            is Resource.Success -> Column(
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

@OrientationPreviews
@Composable
private fun LockScreenDisablePreview() {
    LockScreen(
        scaffoldState = rememberScaffoldState(),
        launchBiometric = {},
        biometricLockDataState = Resource.Success(
            BiometricLockData(
                timeOutLock = 10,
                biometricState = BiometricLockState.LOCK
            )
        )
    )
}

@OrientationPreviews
@Composable
private fun LockScreenEnablePreview() {
    LockScreen(
        scaffoldState = rememberScaffoldState(),
        launchBiometric = {},
        biometricLockDataState = Resource.Success(
            BiometricLockData(
                timeOutLock = 0,
                biometricState = BiometricLockState.LOCK
            )
        )
    )
}


@OrientationPreviews
@Composable
private fun LockScreenTimeOutPreview() {
    LockScreen(
        scaffoldState = rememberScaffoldState(),
        launchBiometric = {},
        biometricLockDataState = Resource.Success(
            BiometricLockData(
                timeOutLock = 10,
                biometricState = BiometricLockState.LOCKED_BY_TIME_OUT
            )
        )
    )
}

@OrientationPreviews
@Composable
private fun LockScreenUndefineLockPreview() {
    LockScreen(
        scaffoldState = rememberScaffoldState(),
        launchBiometric = {},
        biometricLockDataState = Resource.Success(
            BiometricLockData(
                timeOutLock = 10,
                biometricState = BiometricLockState.LOCKED_BY_MANY_INTENTS
            )
        )
    )
}


@OrientationPreviews
@Composable
private fun LockScreenLoadingPreview() {
    LockScreen(
        scaffoldState = rememberScaffoldState(),
        launchBiometric = {},
        biometricLockDataState = Resource.Loading
    )
}

@SimplePreview
@Composable
private fun LockScreenErrorPreview() {
    LockScreen(
        scaffoldState = rememberScaffoldState(),
        launchBiometric = {},
        biometricLockDataState = Resource.Failure
    )
}