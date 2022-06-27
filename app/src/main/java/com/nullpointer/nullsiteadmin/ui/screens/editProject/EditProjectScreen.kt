package com.nullpointer.nullsiteadmin.ui.screens.editProject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel.EditProjectViewModel
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun EditProjectScreen(
    project: Project,
    editProjectVM: EditProjectViewModel = hiltViewModel(),
    resultNavigator: ResultBackNavigator<Project>
) {
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = Unit) {
        editProjectVM.messageError.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    LaunchedEffect(key1 = Unit) {
        editProjectVM.initVM(project)
    }
    Scaffold(scaffoldState = scaffoldState, topBar = {
        ToolbarBack(title = "Edit Project", actionBack = resultNavigator::navigateBack)
    }) { it ->
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = editProjectVM.urlImage,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.8f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            EditableProperty(
                name = "Url Image",
                currentValue = editProjectVM.urlImage,
                changeValue = editProjectVM::updateUrlImage,
                errorValue = editProjectVM.errorUrlImage,
                countValue = editProjectVM.urlImgLength,
            )

            Spacer(modifier = Modifier.height(10.dp))
            EditableProperty(
                name = "Name",
                currentValue = editProjectVM.name,
                changeValue = editProjectVM::updateName,
                errorValue = editProjectVM.errorName,
                countValue = editProjectVM.nameLength
            )
            Spacer(modifier = Modifier.height(10.dp))

            EditableProperty(
                name = "Url Repository",
                currentValue = editProjectVM.urlRepository,
                changeValue = editProjectVM::updateUrlRepository,
                errorValue = editProjectVM.errorUrlRepo,
                countValue = editProjectVM.urlRepoLength
            )
            Spacer(modifier = Modifier.height(10.dp))

            EditableProperty(
                name = "Description",
                currentValue = editProjectVM.description,
                changeValue = editProjectVM::updateDescription,
                errorValue = editProjectVM.errorDescription,
                countValue = editProjectVM.descriptionLength
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    editProjectVM.getUpdatedProject()?.let {
                        resultNavigator.navigateBack(it)
                    }
                },
                enabled = editProjectVM.isSaveEnable
            ) {
                Text("Actualiza  el projecto")
            }
        }
    }
}

@Composable
fun EditableProperty(
    name: String,
    currentValue: String,
    changeValue: (String) -> Unit,
    errorValue: String,
    countValue: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            label = { Text(text = name) },
            value = currentValue,
            onValueChange = changeValue,
            isError = errorValue.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            Text(
                text = errorValue,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error,
                modifier = Modifier.weight(.9f)
            )
            Text(
                text = countValue,
                color = if (errorValue.isEmpty()) Color.Unspecified else MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
        }

    }
}