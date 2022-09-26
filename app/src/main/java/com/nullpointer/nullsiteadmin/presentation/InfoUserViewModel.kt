package com.nullpointer.nullsiteadmin.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoUserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val infoUserRepository: InfoUserRepository
) : ViewModel() {

    companion object {
        private const val KEY_IS_REQUEST_INFO_USER = "KEY_IS_REQUEST_INFO_USER"
    }

    private val _messageError = Channel<Int>()
    val messageError = _messageError.receiveAsFlow()

    var isRequestInfoUser by SavableComposeState(
        defaultValue = false,
        key = KEY_IS_REQUEST_INFO_USER,
        savedStateHandle = savedStateHandle
    )
        private set


    val infoUser = flow<Resource<PersonalInfo>> {
        infoUserRepository.myPersonalInfo.collect {
            emit(Resource.Success(it))
        }
    }.flowOn(Dispatchers.IO).catch {
        Timber.e("Error to load info user $it")
        _messageError.trySend(R.string.error_load_info_user)
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    val infoUserIsEmpty = infoUser.map {
        if (it is Resource.Success) {
            it.data.idPersonal.isEmpty()
        } else {
            false
        }
    }

    init {
        requestLastInformation(false)
    }

    fun requestLastInformation(forceRefresh: Boolean = true) = launchSafeIO(
        isEnabled = !isRequestInfoUser,
        blockBefore = { isRequestInfoUser = true },
        blockAfter = { isRequestInfoUser = false },
        blockException = { Timber.d("Error update info $it") },
        blockIO = {
            val isUpdate = infoUserRepository.requestLastPersonalInfo(forceRefresh)
            if (isUpdate) Timber.d("Updated info user admin")
        }
    )

    fun updatePersonalInfo(
        personalInfo: PersonalInfo
    ) = launchSafeIO(
        blockIO = {
            infoUserRepository.updatePersonalInfo(personalInfo)
            delay(300)
            _messageError.trySend(R.string.message_data_upload)
        },
        blockException = {
            Timber.e("Error to update info personal $it")
            _messageError.trySend(R.string.error_data_user_upload)
        }
    )
}