package com.nullpointer.nullsiteadmin.ui.screens.infoProfile.componets.buttonEditInfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun ButtonEditInfo(
    personalInfoData: Resource<PersonalInfoData?>,
    actionEditInfo: () -> Unit
) {

    AnimatedVisibility(
        visible = personalInfoData is Resource.Success,
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
private fun ButtonEditInfoPreview(){
    ButtonEditInfo(
        personalInfoData = Resource.Success(
            PersonalInfoData()
        ),
        actionEditInfo = {}
    )
}