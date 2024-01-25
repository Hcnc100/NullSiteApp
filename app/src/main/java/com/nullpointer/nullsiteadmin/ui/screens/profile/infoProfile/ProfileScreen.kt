package com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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


    val stateInfoProfile by infoViewModel.infoUser.collectAsState()


    LaunchedEffect(key1 = Unit) {
        infoViewModel.messageError.collect(infoProfileState::showSnackMessage)
    }

    ProfileScreen(
        personalInfoData = stateInfoProfile,
        isRefreshing = infoViewModel.isRequestInfoUser,
        scaffoldState = infoProfileState.scaffoldState,
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
    scaffoldState: ScaffoldState,
    pullRefreshState: PullRefreshState,
    personalInfoData: Resource<PersonalInfoData?>,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            ButtonEditInfo(
                actionEditInfo = actionEditInfo,
                showButtonEditInfo = personalInfoData is Resource.Success
            )
        }
    ) {

        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .then(
                    when (personalInfoData) {
                        is Resource.Loading -> Modifier
                        else -> Modifier.pullRefresh(pullRefreshState)
                    }
                ),
            contentAlignment = Alignment.TopCenter
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
            PullRefreshIndicator(
                state = pullRefreshState,
                refreshing = isRefreshing && personalInfoData !is Resource.Loading
            )
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
        scaffoldState = rememberScaffoldState(),
        personalInfoData = personalInfoData,
        pullRefreshState = rememberPullRefreshState(refreshing = false, onRefresh = {}),
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
        scaffoldState = rememberScaffoldState(),
        personalInfoData = personalInfoData,
        pullRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {}),
    )
}



