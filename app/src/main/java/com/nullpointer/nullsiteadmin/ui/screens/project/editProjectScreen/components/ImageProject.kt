package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun ImageProjectEdit(
    urlImgProject: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = urlImgProject,
        contentDescription = stringResource(R.string.description_current_img_project),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.8f)
    )
}


@SimplePreview
@Composable
private fun ImageProjectPreview() {
    ImageProjectEdit(
        urlImgProject = "https://i.imgur.com/2zYQJ9I.jpg"
    )
}