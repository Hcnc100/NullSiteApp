package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
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
    Surface(
        modifier = modifier
    ) {
        Column {
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
                supportingText = { FieldSupportingText(valueProperty) },
            )

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
                R.string.description_hide_password
            ) else
            Pair(
                R.drawable.ic_visibility_off,
                R.string.description_show_password
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
                supportingText = { FieldSupportingText(valueProperty) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val (iconRes, descriptionRes) = iconAndDescription
                    if (valueProperty.currentValue.isNotEmpty())
                        IconButton(enabled = isEnabled, onClick = { changeVisible(!passwordVisible) }) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = stringResource(id = descriptionRes)
                            )
                        }
                }
            )

        }
    }
}

@Composable
private fun FieldSupportingText(value: PropertySavableString) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (value.hasError) stringResource(value.errorValue) else "",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = value.countLength,
            color = if (value.hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
