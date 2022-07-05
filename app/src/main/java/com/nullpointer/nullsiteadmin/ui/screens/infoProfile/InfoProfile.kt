package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.presentation.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.navigator.HomeNavGraph
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditInfoProfileDestination
import com.nullpointer.nullsiteadmin.ui.screens.states.SimpleScreenState
import com.nullpointer.nullsiteadmin.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph(start = true)
@Destination
@Composable
fun InfoProfile(
    actionRootDestinations: ActionRootDestinations,
    infoViewModel: InfoUserViewModel = hiltViewModel(LocalContext.current as ComponentActivity),
    infoProfileState: SimpleScreenState = rememberSimpleScreenState()
) {

    val stateInfoProfile by infoViewModel.infoUser.collectAsState()

    LaunchedEffect(key1 = Unit) {
        infoViewModel.messageError.collect(infoProfileState::showSnackMessage)
    }
    Scaffold(
        scaffoldState = infoProfileState.scaffoldState,
        floatingActionButton = {
            if (stateInfoProfile is Resource.Success) {
                val project = (stateInfoProfile as Resource.Success<PersonalInfo>).data
                FloatingActionButton(onClick = {
                    actionRootDestinations.changeRoot(EditInfoProfileDestination.invoke(project))
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = stringResource(R.string.description_edit_info_user)
                    )
                }
            }
        }
    ) {

        when (val infoProfile = stateInfoProfile) {
            is Resource.Loading -> LoadingInfoUser(modifier = Modifier.padding(it))
            is Resource.Failure -> AnimationScreen(
                animation = R.raw.error,
                textEmpty = stringResource(R.string.message_error_load_info_user),
                modifier = Modifier.padding(it)
            )
            is Resource.Success -> InfoUser(
                personalInfo = infoProfile.data,
                modifier = Modifier.padding(it)
            )
        }
    }

}

