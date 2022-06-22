package com.nullpointer.nullsiteadmin.ui.screens.infoProfile.dialogChange.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.core.delagetes.SavableProperty
import com.nullpointer.nullsiteadmin.models.InfoType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import javax.inject.Inject

@HiltViewModel
class DialogChangeViewModel @Inject constructor(
    state: SavedStateHandle,
) : ViewModel() {
    companion object {
        const val KEY_TEXT_VALUE = "KEY_TEXT_VALUE"
        const val KEY_IS_VISIBLE = "KEY_IS_VISIBLE"
        const val KEY_LABEL_TEXT = "KEY_LABEL_TEXT"
        const val KEY_INIT_VALUE = "KEY_INIT_VALUE"
    }

    var currentTextValue by SavableComposeState(state, KEY_TEXT_VALUE, "")
        private set
    var isVisible by SavableComposeState(state, KEY_IS_VISIBLE, false)
        private set
    var infoType: InfoType by SavableComposeState(state, KEY_LABEL_TEXT, InfoType.NAME)
        private set
    var initTextValue by SavableProperty(state, KEY_INIT_VALUE, "")
        private set

    private val _errorTextValue = Channel<String>()
    val errorTextValue = _errorTextValue.consumeAsFlow()

    fun showDialog(newInfoType: InfoType, newText: String) {
        initTextValue = newText.trim()
        currentTextValue = newText.trim()
        infoType = newInfoType
        isVisible = true
    }

    fun updateValueChange(newValue: String) {
        currentTextValue = newValue
    }


    fun hideDialog() {
        isVisible = false
    }
}