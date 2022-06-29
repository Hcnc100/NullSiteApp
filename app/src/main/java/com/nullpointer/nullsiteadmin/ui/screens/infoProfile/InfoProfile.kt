package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.presentation.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.nullpointer.nullsiteadmin.ui.screens.destinations.EditInfoProfileDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient

@Destination(start = true)
@Composable
fun InfoProfile(
    infoUserVM: InfoUserViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<EditInfoProfileDestination, PersonalInfo>,
    navigator: DestinationsNavigator
) {
    resultRecipient.onResult(listener = infoUserVM::updatePersonalInfo)
    val stateInfoProfile by infoUserVM.infoUser.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit) {
        infoUserVM.messageError.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            if (stateInfoProfile is Resource.Success) {
                FloatingActionButton(onClick = {
                    navigator.navigate(EditInfoProfileDestination.invoke((stateInfoProfile as Resource.Success<PersonalInfo>).data))
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = ""
                    )
                }
            }
        }
    ) {

        when (val infoProfile = stateInfoProfile) {
            is Resource.Loading -> LoadingInfoUser(modifier = Modifier.padding(it))
            is Resource.Failure -> AnimationScreen(
                animation = R.raw.error,
                textEmpty = "Hubo un error al cargar los datos",
                modifier = Modifier.padding(it)
            )
            is Resource.Success -> InfoUser(
                personalInfo = infoProfile.data,
                modifier = Modifier.padding(it)
            )

        }
    }

}

