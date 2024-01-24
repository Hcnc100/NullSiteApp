package com.nullpointer.nullsiteadmin.ui.screens.profileScreen.infoProfile.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.infoUser.InfoUserRepository
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoUserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val infoUserRepository: InfoUserRepository
) : ViewModel() {

    private val _messageError = Channel<Int>()
    val messageError = _messageError.receiveAsFlow()

    var isRequestInfoUser by mutableStateOf(false)
        private set

    init {
        requestLastInformation()
    }


    val infoUser = flow<Resource<PersonalInfoData?>> {
        infoUserRepository.myPersonalInfoData.collect {
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


    fun requestLastInformation() = launchSafeIO(
        isEnabled = !isRequestInfoUser,
        blockBefore = { isRequestInfoUser = true },
        blockAfter = { isRequestInfoUser = false },
        blockException = {
            ExceptionManager.sendMessageErrorToException(
                exception = it,
                message = "Error update info",
                channel = _messageError
            )
        },
        blockIO = {
        infoUserRepository.getPersonalInfo()
        }
    )
}