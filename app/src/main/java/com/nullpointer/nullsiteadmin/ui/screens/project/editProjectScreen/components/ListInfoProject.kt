package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview
import com.nullpointer.nullsiteadmin.ui.share.EditableTextSavable

@Composable
fun ListInfoProject(
    hiddenKeyBoard: () -> Unit,
    modifier: Modifier = Modifier,
    nameProject: PropertySavableString,
    urlImgProject: PropertySavableString,
    descriptionProject: PropertySavableString,
    urlRepositoryProject: PropertySavableString,
    isEnable: Boolean
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EditableTextSavable(
            valueProperty = urlImgProject,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Uri,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
            isEnabled = isEnable
        )
        EditableTextSavable(
            valueProperty = nameProject,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
            isEnabled = isEnable
        )
        EditableTextSavable(
            valueProperty = urlRepositoryProject,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Uri,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
            isEnabled = isEnable
        )
        EditableTextSavable(
            valueProperty = descriptionProject,
            isEnabled = isEnable
        )
    }
}

@SimplePreview
@Composable
private fun ListInfoProjectPreview() {
    ListInfoProject(
        hiddenKeyBoard = {},
        nameProject = PropertySavableString.example,
        urlImgProject = PropertySavableString.example,
        descriptionProject = PropertySavableString.example,
        urlRepositoryProject = PropertySavableString.example,
        isEnable = true
    )
}
