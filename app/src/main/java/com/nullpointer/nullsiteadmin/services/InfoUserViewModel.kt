package com.nullpointer.nullsiteadmin.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.domain.InfoUserRepository
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoUserViewModel @Inject constructor(
    private val infoUserRepository: InfoUserRepository
) : ViewModel() {

    private val _messageError = Channel<String>()
    val messageError = _messageError.receiveAsFlow()

    val infoUser = flow<Resource<PersonalInfo>> {
        infoUserRepository.myPersonalInfo.collect {
            emit(Resource.Success(it))
        }
    }.catch {
        Timber.e("Error to load info user $it")
        _messageError.trySend("Error to load info user")
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )


    fun updateAnyFieldUser(
        nameAdmin: String? = null,
        profession: String? = null,
        description: String? = null
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            infoUserRepository.updateAnyFieldUser(nameAdmin, profession, description)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                else -> _messageError.trySend("Error to update info user")
            }
        }

    }
}