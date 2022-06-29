package com.nullpointer.nullsiteadmin.models

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState

class PropertySavableString(
    state: SavedStateHandle,
    defaultValue: String = "",
    @StringRes
    val label: Int,
    @StringRes
    val hint:Int,
    private val maxLength: Int,
    @StringRes
    private val emptyError: Int,
    @StringRes
    private val lengthError: Int,
) {
    companion object {
        private const val INIT_ID = 1
        private const val FIN_ID = 1234567890
        private const val RESOURCE_DEFAULT = -1
    }

    private val idSaved = "SAVED_PROPERTY_${(INIT_ID..FIN_ID).random()}"

    var value by SavableComposeState(state, "$idSaved-value", defaultValue)
        private set

    var errorValue by SavableComposeState(state, "$idSaved-error", RESOURCE_DEFAULT)
        private set

    val countLength get() = "${value.length}/${maxLength}"

    val hasError get() = errorValue != RESOURCE_DEFAULT


    fun changeValue(stringValue: String) {
        this.value = stringValue
        this.errorValue = when {
            stringValue.isEmpty() -> emptyError
            stringValue.length > maxLength -> lengthError
            else -> RESOURCE_DEFAULT
        }
    }

    fun clearValue() {
        value = ""
        errorValue = RESOURCE_DEFAULT
    }

}
