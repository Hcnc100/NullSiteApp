package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.InfoType
import com.nullpointer.nullsiteadmin.presentation.InfoUserViewModel
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.ramcosta.composedestinations.annotation.Destination

@Destination(start = true)
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
            is Resource.Loading -> LoadingInfoUser(modifier = Modifier.padding(it))
            is Resource.Failure -> AnimationScreen(
                animation = R.raw.error,
                textEmpty = "Hubo un error al cargar los datos",
                modifier = Modifier.padding(it)
            )
            is Resource.Success -> InfoUser(
                personalInfo = infoProfile.data,
                modifier = Modifier.padding(it)
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

