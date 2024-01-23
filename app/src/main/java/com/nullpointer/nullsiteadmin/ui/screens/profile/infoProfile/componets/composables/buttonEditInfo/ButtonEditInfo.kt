package com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables.buttonEditInfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.preview.provider.BooleanProvider

@Composable
fun ButtonEditInfo(
    showButtonEditInfo: Boolean,
    actionEditInfo: () -> Unit
) {

    AnimatedVisibility(
        visible = showButtonEditInfo,
        enter = scaleIn(
            initialScale = 0.3f,
            animationSpec = tween(500)
        ),
        exit = scaleOut(
            targetScale = 0.3f,
            animationSpec = tween(500)
        )
    ) {
        FloatingActionButton(
            onClick = actionEditInfo
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = stringResource(R.string.description_edit_info_user)
            )
        }
    }
}


@Composable
@SimplePreview
private fun ButtonEditInfoPreview(
    @PreviewParameter(BooleanProvider::class)
    showButton: Boolean,
) {
    ButtonEditInfo(
        showButtonEditInfo = showButton,
        actionEditInfo = {}
    )
}