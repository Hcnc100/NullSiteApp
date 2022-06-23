package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.InfoType
import com.nullpointer.nullsiteadmin.services.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen

@Composable
fun InfoProfile(
    infoUserVM: InfoUserViewModel = hiltViewModel()
) {
    val stateInfoProfile by infoUserVM.infoUser.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit) {
        infoUserVM.messageError.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text("Profile") })
        }
    ) {

        when (val infoProfile = stateInfoProfile) {
            is Resource.Loading -> LoadingInfoUser()
            is Resource.Failure -> AnimationScreen(
                animation = R.raw.error,
                textEmpty = "Hubo un error al cargar los datos"
            )
            is Resource.Success -> InfoUser(
                personalInfo = infoProfile.data
            ) { infoType, newValue ->
                when (infoType) {
                    InfoType.NAME -> infoUserVM.updateAnyFieldUser(nameAdmin = newValue)
                    InfoType.PROFESSION -> infoUserVM.updateAnyFieldUser(profession = newValue)
                    InfoType.DESCRIPTION -> infoUserVM.updateAnyFieldUser(description = newValue)
                }
            }

        }
    }

}

