package com.nullpointer.nullsiteadmin.ui.screens.projectScreen.project.componets.projectItem

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun ImageProject(urlImg: String) {
    AsyncImage(
        model = urlImg,
        contentDescription = stringResource(R.string.description_current_img_project),
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(250.dp)
    )
}

@SimplePreview
@Composable
private fun ImageProjectPreview() {
    ImageProject(
        urlImg = "https://avatars.githubusercontent.com/u/38139389?v=4"
    )
}