package com.nullpointer.nullsiteadmin.ui.share.bottomSheetSelectImage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview


@Composable
fun ItemButtonSheet(
    iconResource: Painter,
    textOptionRes: String,
    textDescriptionRes: String,
    actionLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .heightIn(min = 48.dp)
        .clickable { actionLaunch() }
        .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(
            painter = iconResource,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = textDescriptionRes
        )
        Text(text = textOptionRes)
    }
}

@SimplePreview
@Composable
fun ItemBottomSheetPreview() {
    ItemButtonSheet(
        iconResource = painterResource(id = R.drawable.ic_person),
        textOptionRes = stringResource(id = R.string.label_name_admin),
        textDescriptionRes = stringResource(id = R.string.label_name_admin),
        actionLaunch = {}
    )
}
