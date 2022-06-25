package com.nullpointer.nullsiteadmin.ui.screens.contact

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.EmailContact
import com.nullpointer.nullsiteadmin.presentation.EmailsViewModel
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen

@Composable
fun EmailScreen(
    emailsVM: EmailsViewModel = hiltViewModel()
) {
    val emailsState by emailsVM.listEmails.collectAsState()
    Scaffold(

    ) {
        when (val listEmails = emailsState) {
            Resource.Failure -> AnimationScreen(
                animation = R.raw.error,
                textEmpty = "Error al cargar los emails"
            )
            Resource.Loading -> {}
            is Resource.Success -> {
                if (listEmails.data.isEmpty()) {
                    AnimationScreen(
                        animation = R.raw.empty1,
                        textEmpty = "La bandeja de entrada esta vacia"
                    )
                } else {
                    ListEmails(
                        listEmails = listEmails.data,
                        actionDeleter = emailsVM::deleterEmail
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListEmails(
    listEmails: List<EmailContact>,
    actionDeleter: (String) -> Unit
) {
    LazyColumn {
        item {
            Text(
                text = "Numero de emails: ${listEmails.size}",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
        items(
            count = listEmails.size,
            key = { listEmails[it].id }) { index ->
            EmailItem(
                email = listEmails[index],
                actionDeleter = actionDeleter,
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}