package com.nullpointer.nullsiteadmin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
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
    private val infoUserRepository: InfoUserRepository
) : ViewModel() {

    private val _messageError = Channel<Int>()
    val messageError = _messageError.receiveAsFlow()

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