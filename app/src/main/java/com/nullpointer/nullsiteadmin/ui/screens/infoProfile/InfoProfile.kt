package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.presentation.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditInfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.buttonEditInfo.ButtonEditInfo
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.items.InfoUser
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.subScreens.InfoProfileEmpty
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.subScreens.InfoProfileError
import com.nullpointer.nullsiteadmin.ui.screens.shared.BlockProgress
import com.nullpointer.nullsiteadmin.ui.screens.states.SwipeScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSwipeScreenState
import com.nullpointer.nullsiteadmin.ui.share.ScaffoldSwipeRefresh
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph(start = true)
@Destination
@Composable
fun InfoProfile(
    actionRootDestinations: ActionRootDestinations,
    infoViewModel: InfoUserViewModel = hiltViewModel(),
    infoProfileState: SwipeScreenState = rememberSwipeScreenState(
        isRefreshing = infoViewModel.isRequestInfoUser
    )
) {


    val stateInfoProfile by infoViewModel.infoUser.collectAsState()

    LaunchedEffect(key1 = Unit) {
        infoViewModel.messageError.collect(infoProfileState::showSnackMessage)
    }

    InfoProfile(
        personalInfoData = stateInfoProfile,
        scaffoldState = infoProfileState.scaffoldState,
        swipeRefreshState = infoProfileState.swipeRefreshState,
        actionRefreshInfo = infoViewModel::requestLastInformation,
        actionEditInfo = {
            (stateInfoProfile as? Resource.Success)?.let {
                actionRootDestinations.changeRoot(EditInfoProfileDestination(it.data))
            }
        })
}


@Composable
fun InfoProfile(
    actionEditInfo: () -> Unit,
    actionRefreshInfo: () -> Unit,
    scaffoldState: ScaffoldState,
    swipeRefreshState: SwipeRefreshState,
    personalInfoData: Resource<PersonalInfoData?>,
) {
    ScaffoldSwipeRefresh(
        actionOnRefresh = actionRefreshInfo,
        scaffoldState = scaffoldState,
        swipeRefreshState = swipeRefreshState,
        floatingActionButton = {
            ButtonEditInfo(
                personalInfoData = personalInfoData,
                actionEditInfo = actionEditInfo
            )
        }
    ) {
        when (personalInfoData) {
            is Resource.Loading -> BlockProgress()
            is Resource.Failure -> InfoProfileError()
            is Resource.Success -> {
                when(personalInfoData.data){
                    null -> InfoProfileEmpty()
                    else->InfoUser(
                        personalInfoData = personalInfoData.data,
                    )
                }
            }
        }
    }
}


