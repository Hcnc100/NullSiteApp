package com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.formInfoProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.preview.config.LandscapePreview
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.ButtonUpdateInfoProfile
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable


@Composable
fun FormInfoProfileLandscape(
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EditableTextSavable(
                modifier = Modifier.weight(0.5f),
                valueProperty = nameAdmin,
                singleLine = true,
                isEnabled = isEnable,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            EditableTextSavable(
                modifier = Modifier.weight(0.5f),
                valueProperty = professionAdmin,
                singleLine = true,
                isEnabled = isEnable,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
        }
        EditableTextSavable(
            valueProperty = descriptionAdmin,
            singleLine = false,
            isEnabled = isEnable,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { hiddenKeyBoard() })
        )
        ButtonUpdateInfoProfile(
            isEnable = isDataValid,
            actionClick = actionSave,
        )
    }
}


@LandscapePreview
@Composable
fun FormInfoProfileLandscapePreview() {
    FormInfoProfileLandscape(
        isEnable = true,
        isDataValid = true,
        actionSave = {},
        hiddenKeyBoard = {},
        nameAdmin = PropertySavableString.example,
        professionAdmin = PropertySavableString.example,
        descriptionAdmin = PropertySavableString.example
    )
}
