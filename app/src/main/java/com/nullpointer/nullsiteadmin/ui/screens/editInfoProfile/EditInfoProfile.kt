package com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.screens.editInfoProfile.viewModel.EditInfoViewModel
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun EditInfoProfile(
    editProjectViewModel: EditInfoViewModel = hiltViewModel(),
    personalInfo: PersonalInfo,
    resultNavigator: ResultBackNavigator<PersonalInfo>
) {

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    // * init info in view model
    LaunchedEffect(key1 = Unit) {
        editProjectViewModel.initInfoProfile(personalInfo)
    }

    LaunchedEffect(key1 = Unit) {
        editProjectViewModel.messageError.collect {
            scaffoldState.snackbarHostState.showSnackbar(context.getString(it))
        }
    }

    Scaffold(scaffoldState = scaffoldState) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EditPhotoProfile(
                urlImg = "https://picsum.photos/200",
                modifier = Modifier.padding(10.dp)
            )
            EditableInformation(
                nameAdmin = editProjectViewModel.name,
                professionAdmin = editProjectViewModel.profession,
                descriptionAdmin = editProjectViewModel.description,
                modifier = Modifier.padding(10.dp)
            )
            Button(
                onClick = {
                    editProjectViewModel.getUpdatedPersonalInfo()?.let {
                        resultNavigator.navigateBack(it)
                    }
                },
                enabled = editProjectViewModel.isDataValid
            ) {
                Text(text = "Save Info Profile")
            }
        }
    }
}

@Composable
private fun EditableInformation(
    nameAdmin: PropertySavableString,
    professionAdmin: PropertySavableString,
    descriptionAdmin: PropertySavableString,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        EditableTextSavable(valueProperty = nameAdmin)
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = professionAdmin)
        Spacer(modifier = Modifier.height(10.dp))
        EditableTextSavable(valueProperty = descriptionAdmin)
    }
}

@Composable
private fun EditPhotoProfile(
    modifier: Modifier = Modifier,
    urlImg: String
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box {
            AsyncImage(
                model = urlImg,
                contentDescription = "",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(15.dp)
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = ""
                )
            }
        }

    }
}

