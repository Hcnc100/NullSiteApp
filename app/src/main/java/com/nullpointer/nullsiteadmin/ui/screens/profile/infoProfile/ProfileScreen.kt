package com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import com.nullpointer.nullsiteadmin.ui.share.AppRefreshState
import com.nullpointer.nullsiteadmin.ui.share.AppPullToRefreshBox
import com.nullpointer.nullsiteadmin.ui.share.rememberAppRefreshState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.preview.config.OrientationPreviews
import com.nullpointer.nullsiteadmin.ui.preview.provider.InfoProfileStateProvider
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditInfoProfileScreenDestination
import com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables.ButtonEditInfo
import com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables.infoUser.InfoUser
import com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.subScreens.InfoProfileEmpty
import com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.subScreens.InfoProfileError
import com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.viewModel.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.screens.shared.BlockProgress
import com.nullpointer.nullsiteadmin.ui.screens.states.PullRefreshScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberPullRefreshScreenState
import com.ramcosta.composedestinations.annotation.Destination


@HomeNavGraph(start = true)
@Destination
@Composable
fun ProfileScreen(
    actionRootDestinations: ActionRootDestinations,
    infoViewModel: InfoUserViewModel = hiltViewModel(),
    infoProfileState: PullRefreshScreenState = rememberPullRefreshScreenState(
        isRefreshing = infoViewModel.isRequestInfoUser,
        onRefresh = infoViewModel::requestLastInformation,
    )
) {


    val stateInfoProfile by infoViewModel.infoUser.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = Unit) {
        infoViewModel.messageError.collect(infoProfileState::showSnackMessage)
    }

    ProfileScreen(
        personalInfoData = stateInfoProfile,
        isRefreshing = infoViewModel.isRequestInfoUser,
        snackbarHostState = infoProfileState.snackbarHostState,
        pullRefreshState = infoProfileState.pullRefreshState,
        actionEditInfo = {
            (stateInfoProfile as? Resource.Success)?.let {
                actionRootDestinations.changeRoot(EditInfoProfileScreenDestination(it.data))
            }
        })
}


@Composable
fun ProfileScreen(
    isRefreshing: Boolean,
    actionEditInfo: () -> Unit,
    snackbarHostState: SnackbarHostState,
    pullRefreshState: AppRefreshState,
    personalInfoData: Resource<PersonalInfoData?>,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ButtonEditInfo(
                actionEditInfo = actionEditInfo,
                showButtonEditInfo = personalInfoData is Resource.Success
            )
        }
    ) {

        AppPullToRefreshBox(
            refreshState = pullRefreshState,
            enabled = personalInfoData !is Resource.Loading,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (personalInfoData) {
                is Resource.Loading -> BlockProgress()
                is Resource.Failure -> InfoProfileError()
                is Resource.Success -> {
                    when (personalInfoData.data) {
                        null -> InfoProfileEmpty()
                        else -> InfoUser(
                            personalInfoData = personalInfoData.data,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        }
    }
}


@OrientationPreviews
@Composable
private fun InfoProfileNotRefreshingPreview(
    @PreviewParameter(InfoProfileStateProvider::class)
    personalInfoData: Resource<PersonalInfoData?>,
) {
    ProfileScreen(
        isRefreshing = false,
        actionEditInfo = {},
        snackbarHostState = remember { SnackbarHostState() },
        personalInfoData = personalInfoData,
        pullRefreshState = rememberAppRefreshState(refreshing = false, onRefresh = {}),
    )
}


@OrientationPreviews
@Composable
private fun InfoProfileRefreshingPreview(
    @PreviewParameter(InfoProfileStateProvider::class)
    personalInfoData: Resource<PersonalInfoData?>,
) {
    ProfileScreen(
        isRefreshing = true,
        actionEditInfo = {},
        snackbarHostState = remember { SnackbarHostState() },
        personalInfoData = personalInfoData,
        pullRefreshState = rememberAppRefreshState(refreshing = true, onRefresh = {}),
    )
}



