package com.nullpointer.nullsiteadmin.ui.screens.detailsEmail

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.utils.toFormat
import com.nullpointer.nullsiteadmin.models.EmailContact
import com.nullpointer.nullsiteadmin.presentation.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.interfaces.ActionRootDestinations
import com.nullpointer.nullsiteadmin.ui.screens.animation.LottieContainer
import com.nullpointer.nullsiteadmin.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER

@Destination(
    deepLinks = [
        DeepLink(uriPattern = "https://myapp.com/$FULL_ROUTE_PLACEHOLDER")
    ]
)
@Composable
fun EmailDetailsScreen(
    email: EmailContact,
    rootDestinations: ActionRootDestinations,
    emailsViewModel: EmailsViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            ToolbarBack(title = "Email details", actionBack = rootDestinations::backDestination)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                emailsViewModel.deleterEmail(email.id)
                rootDestinations.backDestination()
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_deleter), contentDescription = "")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieContainer(modifier = Modifier.size(150.dp), animation = R.raw.email)
            }

            Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = email.email)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = email.name)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = email.timestamp.toFormat(context),
                        modifier = Modifier.align(Alignment.End)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = email.subject)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = email.message)
                }
            }
        }
    }
}