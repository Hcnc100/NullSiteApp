package com.nullpointer.nullsiteadmin.ui.screens.infoProfile.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EditableViewModel @Inject constructor(
    state: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val MAX_NAME = 50
        private const val MAX_PROFESSION = 100
        private const val MAX_DESCRIPTION = 200
    }

    val personalInfo: StateFlow<Resource<PersonalInfo>> = flow {
        emit(
            Resource.Success(
                PersonalInfo(
                    name = "Pepe",
                    profession = "Panadero",
                    description = "Passionate about technology and self-taught learning, but with an inclination towards mobile development, fond of artificial intelligence and computer security. My philosophy is to never stop learning.",
                    urlImg = "https://picsum.photos/200"
                )
            )
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading()
    )

    fun changeInfoProfile(
        newName: String? = null,
        newProfession: String? = null,
        newDescription: String? = null
    ) {

    }
}
