package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString

@Composable
fun EditableTextSavable(
    modifier: Modifier = Modifier,
    modifierText: Modifier = Modifier,
    isEnabled: Boolean = true,
    singleLine: Boolean = false,
    valueProperty: PropertySavableString,
    shape: Shape = MaterialTheme.shapes.small,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Surface {
        Column(modifier = modifier) {
            OutlinedTextField(
                shape = shape,
                enabled = isEnabled,
                singleLine = singleLine,
                isError = valueProperty.hasError,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                value = valueProperty.currentValue,
                modifier = modifierText.fillMaxWidth(),
                onValueChange = valueProperty::changeValue,
                visualTransformation = visualTransformation,
                label = { Text(stringResource(id = valueProperty.label)) },
                placeholder = { Text(stringResource(id = valueProperty.hint)) },
            )
            Row {
                Text(
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.weight(.9f),
                    text = if (valueProperty.hasError) stringResource(id = valueProperty.errorValue) else ""
                )
                Text(
                    text = valueProperty.countLength,
                    style = MaterialTheme.typography.caption,
                    color = if (valueProperty.hasError) MaterialTheme.colors.error else Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun PasswordTextSavable(
    modifier: Modifier = Modifier,
    modifierText: Modifier = Modifier,
    isEnabled: Boolean = true,
    singleLine: Boolean = false,
    valueProperty: PropertySavableString,
    shape: Shape = MaterialTheme.shapes.small,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val (passwordVisible, changeVisible) = rememberSaveable { mutableStateOf(false) }

    val iconAndDescription: Pair<Int, Int> = remember(passwordVisible) {
        if (passwordVisible)
            Pair(
                R.drawable.ic_visibility,
                R.string.description_show_password
            ) else
            Pair(
                R.drawable.ic_visibility_off,
                R.string.description_hide_password
            )
    }

    Surface {
        Column(modifier = modifier.fillMaxWidth()) {
            OutlinedTextField(
                shape = shape,
                enabled = isEnabled,
                singleLine = singleLine,
                isError = valueProperty.hasError,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                value = valueProperty.currentValue,
                modifier = modifierText.fillMaxWidth(),
                onValueChange = valueProperty::changeValue,
                label = { Text(stringResource(id = valueProperty.label)) },
                placeholder = { Text(stringResource(id = valueProperty.hint)) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val (iconRes, descriptionRes) = iconAndDescription
                    if (valueProperty.currentValue.isNotEmpty())
                        IconButton(onClick = { changeVisible(!passwordVisible) }) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = stringResource(id = descriptionRes)
                            )
                        }
                }
            )
            Row {
                Text(
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.weight(.9f),
                    text = if (valueProperty.hasError) stringResource(id = valueProperty.errorValue) else ""
                )
                Text(
                    text = valueProperty.countLength,
                    style = MaterialTheme.typography.caption,
                    color = if (valueProperty.hasError) MaterialTheme.colors.error else Color.Unspecified
                )
            }
        }
    }
}