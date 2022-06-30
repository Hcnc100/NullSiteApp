package com.nullpointer.nullsiteadmin.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.screens.auth.viewModel.AuthFieldViewModel
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun AuthScreen(
    authFieldVM: AuthFieldViewModel = hiltViewModel()
) {
    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colors.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            LogoApp(modifier = Modifier.padding(20.dp))

            FieldLogin(
                email = authFieldVM.emailAdmin,
                password = authFieldVM.passwordAdmin,
                modifier = Modifier.width(300.dp)
            )
            ButtonAuth(
                modifier = Modifier.padding(vertical = 20.dp)
            ) {

            }

        }
    }
}

@Composable
private fun ButtonAuth(
    modifier: Modifier = Modifier,
    actionClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        text = { Text(text = "Authtenticate") },
        onClick = actionClick
    )
}

@Composable
private fun FieldLogin(
    email: PropertySavableString,
    password: PropertySavableString,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ContainerFieldAuth {
            EditableTextSavable(
                valueProperty = email,
                modifier = Modifier.padding(10.dp)
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        ContainerFieldAuth {
            EditableTextSavable(
                valueProperty = password,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
private fun ContainerFieldAuth(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.background)
    ) {
        content()
    }
}

@Composable
private fun LogoApp(
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = R.drawable.ic_safe,
        contentDescription = "",
        modifier = modifier.size(130.dp)
    )
}