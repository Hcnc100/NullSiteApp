package com.nullpointer.nullsiteadmin.ui.screens.profileScreen.editInfoProfile.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.BooleanProvider
import com.nullpointer.nullsiteadmin.ui.screens.profileScreen.infoProfile.componets.composables.infoUser.LoadingIconImageProfile

@Composable
fun EditPhotoProfile(
    urlImg: Uri,
    editEnabled: Boolean,
    actionClick: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
) {

    Box(
        modifier = modifier
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest
                .Builder(context)
                .data(urlImg)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(id = R.string.description_current_img_profile),
            modifier = modifier
                .size(200.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            loading = {
                LoadingIconImageProfile(
                    showLoading = true,
                    modifier = Modifier.fillMaxSize()
                )
            },
            error = {
                LoadingIconImageProfile(
                    showLoading = false,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
        if (editEnabled)
            FloatingActionButton(
                onClick = actionClick,
                modifier = Modifier
                    .padding(15.dp)
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.description_edit_img_profile)
                )
            }
    }
}

@SimplePreview
@Composable
private fun EditPhoneProfilePreview(
    @PreviewParameter(BooleanProvider::class)
    editEnabled: Boolean
) {
    EditPhotoProfile(
        urlImg = Uri.parse("https://avatars.githubusercontent.com/u/38139389?v=4"),
        actionClick = {},
        editEnabled = editEnabled,
    )
}
