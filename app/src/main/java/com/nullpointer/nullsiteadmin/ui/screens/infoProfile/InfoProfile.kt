package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.presentation.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditInfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.buttonEditInfo.ButtonEditInfo
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.items.InfoUser
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.subScreens.InfoProfileEmpty
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.subScreens.InfoProfileError
import com.nullpointer.nullsiteadmin.ui.screens.shared.BlockProgress
import com.nullpointer.nullsiteadmin.ui.screens.states.SwipeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSwipeScreenState
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@HomeNavGraph(start = true)
@Destination
@Composable
fun InfoProfile(
    actionRootDestinations: ActionRootDestinations,
    infoViewModel: InfoUserViewModel = hiltViewModel(),
    infoProfileState: SwipeScreenState = rememberSwipeScreenState(
        isRefreshing = infoViewModel.isRequestInfoUser
    ),
    pullRefreshState: PullRefreshState = rememberPullRefreshState(
        onRefresh = infoViewModel::requestLastInformation,
        refreshing = infoViewModel.isRequestInfoUser
    )
) {


    val stateInfoProfile by infoViewModel.infoUser.collectAsState()

    LaunchedEffect(key1 = Unit) {
        infoViewModel.messageError.collect(infoProfileState::showSnackMessage)
    }

    InfoProfile(
        personalInfoData = stateInfoProfile,
        pullRefreshState = pullRefreshState,
        scaffoldState = infoProfileState.scaffoldState,
        isRefreshing = infoViewModel.isRequestInfoUser,
        actionEditInfo = {
            (stateInfoProfile as? Resource.Success)?.let {
                actionRootDestinations.changeRoot(EditInfoProfileDestination(it.data))
            }
        })
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InfoProfile(
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
                .then(
                    if (personalInfoData !is Resource.Failure) Modifier.pullRefresh(pullRefreshState) else Modifier
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
                        )
                    }
                }
            }
            PullRefreshIndicator(refreshing = isRefreshing, state = pullRefreshState)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@SimplePreview
@Composable
private fun InfoProfilePreview() {
    InfoProfile(
        isRefreshing = false,
        actionEditInfo = {},
        scaffoldState = rememberScaffoldState(),
        pullRefreshState = rememberPullRefreshState(refreshing = false, onRefresh = {}),
        personalInfoData = Resource.Loading,
    )
}


