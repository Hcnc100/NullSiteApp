package com.nullpointer.nullsiteadmin.ui.screens.home.componets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun ImageDraw(
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = R.drawable.cover2,
        contentDescription = stringResource(R.string.description_img_web),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
    )
}


@SimplePreview
@Composable
private fun ImageDrawPreview() {
    ImageDraw()
}
