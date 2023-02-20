package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.presentation.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditInfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.subScreens.InfoPersonalLoading
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.subScreens.InfoProfileEmpty
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.subScreens.InfoProfileError
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.subScreens.InfoProfileSuccess
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
    val isDataEmpty by infoViewModel.infoUserIsEmpty.collectAsState(initial = true)

    LaunchedEffect(key1 = Unit) {
        infoViewModel.messageError.collect(infoProfileState::showSnackMessage)
    }

    InfoProfile(
        isDataEmpty = isDataEmpty,
        personalInfo = stateInfoProfile,
        scaffoldState = infoProfileState.scaffoldState,
        swipeRefreshState = infoProfileState.swipeRefreshState,
        actionRefreshInfo = infoViewModel::requestLastInformation,
        actionEditInfo = {
            actionRootDestinations.changeRoot(EditInfoProfileDestination(it))
        })
}

@Composable
private fun ButtonEditInfo(
    personalInfo: Resource<PersonalInfo>,
    actionEditInfo: (PersonalInfo) -> Unit
) {
    if (personalInfo is Resource.Success) {
        FloatingActionButton(
            onClick = { actionEditInfo(personalInfo.data) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = stringResource(R.string.description_edit_info_user)
            )
        }
    }
}

@Composable
private fun InfoProfile(
    actionRefreshInfo: () -> Unit,
    scaffoldState: ScaffoldState,
    swipeRefreshState: SwipeRefreshState,
    personalInfo: Resource<PersonalInfo>,
    actionEditInfo: (PersonalInfo) -> Unit,
    isDataEmpty: Boolean
) {
    ScaffoldSwipeRefresh(
        actionOnRefresh = actionRefreshInfo,
        scaffoldState = scaffoldState,
        swipeRefreshState = swipeRefreshState,
        floatingActionButton = {
            ButtonEditInfo(
                personalInfo = personalInfo,
                actionEditInfo = actionEditInfo
            )
        }
    ) {
        when (personalInfo) {
            is Resource.Loading -> InfoPersonalLoading()
            is Resource.Failure -> InfoProfileError()
            is Resource.Success -> {
                if (isDataEmpty) {
                    InfoProfileEmpty()
                } else {
                    InfoProfileSuccess(personalInfo = personalInfo.data)
                }
            }
        }
    }
}
