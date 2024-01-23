package com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables.infoUser.LoadingIconImageProfile

@Composable
fun PhotoProfile(
    urlImgProfile: String,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {


    SubcomposeAsyncImage(
        model = ImageRequest
            .Builder(context)
            .data(urlImgProfile)
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
}


@SimplePreview
@Composable
private fun PreviewPhotoProfile() {
    PhotoProfile(
        urlImgProfile = "https://avatars.githubusercontent.com/u/38139389?v=4"
    )
}
