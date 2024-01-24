package com.nullpointer.nullsiteadmin.ui.screens.email.emial.components.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import com.nullpointer.nullsiteadmin.ui.screens.email.emial.components.items.EmailItem
import com.nullpointer.nullsiteadmin.ui.share.CircularProgressAnimation


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListSuccessEmails(
    isConcatenate: Boolean,
    modifier: Modifier = Modifier,
    listEmails: List<EmailData>,
    actionDetails: (EmailData) -> Unit,
    lazyListState: LazyListState,
) {

    Box {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = listEmails,
                key = { it.idEmail },
            ) { email ->
                EmailItem(
                    email = email,
                    actionDetails = { actionDetails(email) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
        CircularProgressAnimation(
            isVisible = isConcatenate,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.BottomCenter)
        )
    }

}