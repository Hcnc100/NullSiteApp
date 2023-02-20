package com.nullpointer.nullsiteadmin.core.delagetes

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle

class PropertySavableImg(
    tagSavable: String,
    state: SavedStateHandle,
    private var defaultValue: Uri = Uri.EMPTY
) {

    private val idSaved = "SAVED_PROPERTY_IMG_$tagSavable"

    var value: Uri by SavableComposeState(state, "$idSaved-CURRENT-VALUE", Uri.EMPTY)
        private set

    var isCompress by mutableStateOf(false)
        private set

    val hasChanged get() = value != defaultValue

    val isNotEmpty get() = value != Uri.EMPTY

    fun changeValue(newValue: Uri, isInit: Boolean = false) {
        if (isInit) defaultValue = newValue
        value = newValue
    }

    fun changeImageCompress(isCompress: Boolean) {
        this.isCompress = isCompress
    }

    fun clearValue() {
        isCompress = false
        value = defaultValue
    }
}