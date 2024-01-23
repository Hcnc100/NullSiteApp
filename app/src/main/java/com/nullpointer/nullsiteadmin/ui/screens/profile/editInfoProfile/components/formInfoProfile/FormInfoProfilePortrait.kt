package com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.formInfoProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.ButtonUpdateInfoProfile
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable

@Composable
fun FormInfoProfilePortrait(
    isEnable: Boolean,
    isDataValid: Boolean,
    actionSave: () -> Unit,
    hiddenKeyBoard: () -> Unit,
    modifier: Modifier = Modifier,
    nameAdmin: PropertySavableString,
    professionAdmin: PropertySavableString,
    descriptionAdmin: PropertySavableString
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        EditableTextSavable(
            valueProperty = nameAdmin,
            singleLine = true,
            isEnabled = isEnable,
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )
        EditableTextSavable(
            valueProperty = professionAdmin,
            singleLine = true,
            isEnabled = isEnable,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() })
        )
        EditableTextSavable(valueProperty = descriptionAdmin)
        ButtonUpdateInfoProfile(
            isEnable = isDataValid,
            actionClick = actionSave
        )
    }
}


@SimplePreview
@Composable
fun FormInfoProfilePortraitPreview() {
    FormInfoProfilePortrait(
        isEnable = true,
        isDataValid = true,
        actionSave = {},
        hiddenKeyBoard = {},
        nameAdmin = PropertySavableString.example,
        professionAdmin = PropertySavableString.example,
        descriptionAdmin = PropertySavableString.example
    )
}

