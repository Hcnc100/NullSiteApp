package com.nullpointer.nullsiteadmin.ui.screens.infoProfile.dialogChange

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.models.InfoType
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.dialogChange.viewModel.DialogChangeViewModel

@Composable
fun DialogChangeInfo(
    dialogChangeVM: DialogChangeViewModel,
    actionAccept: (InfoType, String) -> Unit,
) {
    AlertDialog(
        title = {
            Column {
                Text("Cambia el valor")
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = dialogChangeVM.currentTextValue,
                    onValueChange = dialogChangeVM::updateValueChange,
                    label = { Text(dialogChangeVM.infoType.label) }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = dialogChangeVM::hideDialog) {
                Text(text = "Cancelar")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                actionAccept(
                    dialogChangeVM.infoType,
                    dialogChangeVM.currentTextValue
                )
            }) {
                Text(text = "Confirmar")
            }
        }, onDismissRequest = dialogChangeVM::hideDialog
    )
}