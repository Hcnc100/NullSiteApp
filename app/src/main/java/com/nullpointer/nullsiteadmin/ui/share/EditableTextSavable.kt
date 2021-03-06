package com.nullpointer.nullsiteadmin.ui.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.nullpointer.nullsiteadmin.models.PropertySavableString

@Composable
fun EditableTextSavable(
    valueProperty: PropertySavableString,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isEnabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            enabled = isEnabled,
            label = { Text(stringResource(id = valueProperty.label)) },
            placeholder = { Text(stringResource(id = valueProperty.hint)) },
            value = valueProperty.value,
            onValueChange = valueProperty::changeValue,
            isError = valueProperty.hasError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            visualTransformation = if (keyboardOptions.keyboardType != KeyboardType.Password)
                VisualTransformation.None else PasswordVisualTransformation(),
        )
        Row {
            Text(
                text = if (valueProperty.hasError) stringResource(id = valueProperty.errorValue) else "",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error,
                modifier = Modifier.weight(.9f)
            )
            Text(
                text = valueProperty.countLength,
                color = if (valueProperty.hasError) MaterialTheme.colors.error else Color.Unspecified,
                style = MaterialTheme.typography.caption
            )
        }

    }
}