package com.nullpointer.nullsiteadmin.ui.screens.profileScreen.editInfoProfile.components.formInfoProfile

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.runningcompose.ui.preview.config.OrientationPreviews


@Composable
fun FormInfoProfile(
    isEnable: Boolean,
    isDataValid: Boolean,
    actionSave: () -> Unit,
    hiddenKeyBoard: () -> Unit,
    modifier: Modifier = Modifier,
    nameAdmin: PropertySavableString,
    professionAdmin: PropertySavableString,
    descriptionAdmin: PropertySavableString,
    orientation: Int = LocalConfiguration.current.orientation
) {

    when (orientation) {
        ORIENTATION_PORTRAIT -> FormInfoProfilePortrait(
            isEnable = isEnable,
            isDataValid = isDataValid,
            actionSave = actionSave,
            hiddenKeyBoard = hiddenKeyBoard,
            modifier = modifier,
            nameAdmin = nameAdmin,
            professionAdmin = professionAdmin,
            descriptionAdmin = descriptionAdmin
        )

        else -> FormInfoProfileLandscape(
            isEnable = isEnable,
            isDataValid = isDataValid,
            actionSave = actionSave,
            hiddenKeyBoard = hiddenKeyBoard,
            modifier = modifier,
            nameAdmin = nameAdmin,
            professionAdmin = professionAdmin,
            descriptionAdmin = descriptionAdmin
        )
    }
}


@OrientationPreviews
@Composable
private fun FormInfoProfilePreview() {
    FormInfoProfile(
        isEnable = true,
        isDataValid = true,
        actionSave = {},
        hiddenKeyBoard = {},
        nameAdmin = PropertySavableString.example,
        professionAdmin = PropertySavableString.example,
        descriptionAdmin = PropertySavableString.example
    )
}