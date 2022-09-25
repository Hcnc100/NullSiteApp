package com.nullpointer.nullsiteadmin.ui.screens.contact.components.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.EmailItem
import com.nullpointer.nullsiteadmin.ui.screens.contact.components.items.EmailItemLoading
import com.nullpointer.nullsiteadmin.ui.share.LazyListConcatenate
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer


@Composable
fun ListEmptyEmail(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier,
        animation = R.raw.empty1,
        textEmpty = stringResource(R.string.message_empty_contact)
    )
}

@Composable
fun ListErrorEmail(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier,
        animation = R.raw.error,
        textEmpty = stringResource(R.string.message_error_contact)
    )
}

@Composable
fun ListLoadingEmail(
    modifier: Modifier = Modifier
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    LazyColumn(modifier = modifier) {
        items(
            count = 10,
            key = { it }
        ) {
            EmailItemLoading(shimmer = shimmer)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListSuccessEmails(
    isConcatenate: Boolean,
    modifier: Modifier = Modifier,
    concatenateEmails: () -> Unit,
    listEmails: List<EmailContact>,
    actionDetails: (EmailContact) -> Unit,
    lazyListState: LazyListState = rememberLazyListState()
) {
    LazyListConcatenate(
        modifier = modifier,
        isConcatenate = isConcatenate,
        lazyListState = lazyListState,
        actionConcatenate = concatenateEmails,
    ) {
        item(key = "number-emails") {
            Text(
                text = stringResource(R.string.text_number_emails, listEmails.size),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
        items(
            items = listEmails,
            key = { it.idEmail }
        ) { email ->
            EmailItem(
                email = email,
                actionDetails = actionDetails,
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}