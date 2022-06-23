package com.nullpointer.nullsiteadmin.ui.screens.infoProfile.dialogChange

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.models.InfoType
import com.nullpointer.nullsiteadmin.models.InfoType.*
import com.nullpointer.nullsiteadmin.ui.screens.infoProfile.dialogChange.viewModel.DialogChangeViewModel

@Composable
fun DialogChangeInfo(
    dialogChangeVM: DialogChangeViewModel,
    actionAccept: (InfoType, String) -> Unit,
) {
    val height = when (dialogChangeVM.infoType) {
        NAME -> 100.dp
        PROFESSION -> 100.dp
        DESCRIPTION -> 180.dp
    }
    AlertDialog(
        title = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Cambia el valor", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(20.dp))
                Column(horizontalAlignment = Alignment.End) {
                    OutlinedTextField(
                        value = dialogChangeVM.currentTextValue,
                        onValueChange = dialogChangeVM::updateValueChange,
                        label = { Text(dialogChangeVM.infoType.label) },
                        isError = dialogChangeVM.errorTextValue != "",
                        modifier = Modifier.height(height)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                    ) {
                        if (dialogChangeVM.errorTextValue == "") {
                            Spacer(
                                modifier = Modifier
                                    .width(1.dp)
                                    .weight(.8f)
                            )
                        } else {
                            Text(
                                dialogChangeVM.errorTextValue,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .weight(.8f),
                                color = MaterialTheme.colors.error,
                            )
                        }
                        Text(
                            dialogChangeVM.currentSize,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.weight(.2f),
                            color = if (dialogChangeVM.errorTextValue == "") Color.Unspecified else MaterialTheme.colors.error,
                            textAlign = TextAlign.End
                        )
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        },
        dismissButton = {
            TextButton(onClick = dialogChangeVM::hideDialog) {
                Text(text = "Cancelar")
            }
        },
        confirmButton = {
            OutlinedButton(
                enabled = dialogChangeVM.isSaveEnable,
                onClick = {
                    actionAccept(
                        dialogChangeVM.infoType,
                        dialogChangeVM.currentTextValue
                    )
                }
            ) {
                Text(text = "Confirmar")
            }
        },
        onDismissRequest = dialogChangeVM::hideDialog
    )
}