package com.nullpointer.nullsiteadmin.ui.screens.infoProfile.dialogChange.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.core.delagetes.SavableProperty
import com.nullpointer.nullsiteadmin.models.InfoType
import com.nullpointer.nullsiteadmin.models.InfoType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialogChangeViewModel @Inject constructor(
    state: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val KEY_TEXT_VALUE = "KEY_TEXT_VALUE"
        private const val KEY_IS_VISIBLE = "KEY_IS_VISIBLE"
        private const val KEY_LABEL_TEXT = "KEY_LABEL_TEXT"
        private const val KEY_INIT_VALUE = "KEY_INIT_VALUE"
        private const val KEY_ERROR_VALUE = "KEY_ERROR_VALUE"
        private const val MAX_LENGTH_NAME = 50
        private const val MAX_LENGTH_PROFESSION = 50
        private const val MAX_LENGTH_DESCRIPTION = 250
    }

    var currentTextValue by SavableComposeState(state, KEY_TEXT_VALUE, "")
        private set
    var isVisible by SavableComposeState(state, KEY_IS_VISIBLE, false)
        private set
    var infoType by SavableProperty(state, KEY_LABEL_TEXT, NAME)
        private set
    var initTextValue by SavableProperty(state, KEY_INIT_VALUE, "")
        private set

    var errorTextValue by SavableComposeState(state, KEY_ERROR_VALUE, "")
        private set

    val currentSize
        get() = when (infoType) {
            NAME -> "${currentTextValue.length}/$MAX_LENGTH_NAME"
            PROFESSION -> "${currentTextValue.length}/$MAX_LENGTH_PROFESSION"
            DESCRIPTION -> "${currentTextValue.length}/$MAX_LENGTH_DESCRIPTION"
        }
    val isSaveEnable get() = errorTextValue == "" && currentTextValue != initTextValue

    fun showDialog(newInfoType: InfoType, newText: String) {
        initTextValue = newText.trim()
        currentTextValue = newText.trim()
        infoType = newInfoType
        isVisible = true
    }

    fun updateValueChange(newValue: String) {
        if(!newValue.endsWith("\n")){
            currentTextValue = newValue
            when (infoType) {
                NAME -> {
                    errorTextValue = if (currentTextValue.isEmpty()) {
                        "El nombre es requerido"
                    } else if (currentTextValue.length > MAX_LENGTH_NAME) {
                        "El nombre no puede ser muy largo"
                    } else {
                        ""
                    }
                }
                PROFESSION -> {
                    errorTextValue = if (currentTextValue.isEmpty()) {
                        "La profession es requerida"
                    } else if (currentTextValue.length > MAX_LENGTH_PROFESSION) {
                        "El nombre de la professiojn no pede ser tan largo"
                    } else {
                        ""
                    }
                }
                DESCRIPTION -> {
                    errorTextValue = if (currentTextValue.isEmpty()) {
                        "La descripcion es necesaria"
                    } else if (currentTextValue.length > MAX_LENGTH_DESCRIPTION) {
                        "La descripcion no puede ser tan larga"
                    } else {
                        ""
                    }
                }
            }
        }

    }


    fun hideDialog() {
        isVisible = false
        errorTextValue = ""
    }
}