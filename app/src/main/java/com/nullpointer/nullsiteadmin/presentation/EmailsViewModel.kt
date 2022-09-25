package com.nullpointer.nullsiteadmin.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.models.email.EmailContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val emailsRepository: EmailsRepository
) : ViewModel() {

    companion object {
        private const val KEY_IS_CONCATENATE = "KEY_IS_CONCATENATE"
        private const val KEY_IS_REQUEST_EMAIL = "KEY_IS_REQUEST_EMAIL"
        private const val KEY_EMAIL_CONCATENATE = "KEY_EMAIL_CONCATENATE"
    }

    private val _errorEmail = Channel<Int>()
    val errorEmail = _errorEmail.receiveAsFlow()

    var isEnabledConcatenateEmail by SavableComposeState(
        defaultValue = true,
        key = KEY_EMAIL_CONCATENATE,
        savedStateHandle = savedStateHandle
    )
        private set

    var isConcatenateEmail by SavableComposeState(
        defaultValue = false,
        key = KEY_IS_CONCATENATE,
        savedStateHandle = savedStateHandle
    )
        private set

    var isRequestEmail by SavableComposeState(
        defaultValue = false,
        key = KEY_IS_REQUEST_EMAIL,
        savedStateHandle = savedStateHandle
    )
        private set

    init {
        requestLastEmail(false)
    }

    val listEmails = flow<Resource<List<EmailContact>>> {
        emailsRepository.listEmails.collect {
            emit(Resource.Success(it))
        }
    }.flowOn(Dispatchers.IO).catch {
        Timber.e("Error to load emails $it")
        _errorEmail.trySend(R.string.error_load_list_email)
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    fun concatenateEmails(
        actionSuccessConcatenate: () -> Unit
    ) = launchSafeIO(
        isEnabled = isEnabledConcatenateEmail,
        blockBefore = { isConcatenateEmail = true },
        blockAfter = { isConcatenateEmail = false },
        blockIO = {
            val numberEmailsRequest = emailsRepository.concatenateEmails()
            Timber.d("Number of emails concatenate $numberEmailsRequest")
            withContext(Dispatchers.Main) {
                isEnabledConcatenateEmail = numberEmailsRequest != 0
                actionSuccessConcatenate()
            }
        },
        blockException = {
            Timber.e("Error to concatenate emails $it")
        }
    )


    fun deleterEmail(
        idEmail: String
    ) = launchSafeIO(
        blockIO = {
            emailsRepository.deleterEmail(idEmail)
            delay(300)
            _errorEmail.trySend(R.string.message_deleter_email_success)
        },
        blockException = {
            Timber.e("Error to delete email $idEmail : $it")
            _errorEmail.trySend(R.string.error_deleter_email)
        }
    )

    fun requestLastEmail(
        forceRefresh: Boolean = true
    ) = launchSafeIO(
        isEnabled = !isRequestEmail,
        blockBefore = { isRequestEmail = true },
        blockAfter = { isRequestEmail = false },
        blockException = { Timber.e("Error request last email $it") },
        blockIO = {
            val sizeRequest = emailsRepository.requestLastEmail(forceRefresh)
            isEnabledConcatenateEmail = true
            Timber.d("News emails receiver $sizeRequest")
        }
    )

    fun markAsOpen(email: EmailContact) = launchSafeIO(
        blockIO = { emailsRepository.markAsOpen(email.idEmail) },
        blockException = {
            Timber.e("Error mark as open $email : $it")
            _errorEmail.trySend(R.string.error_unkown)
        }
    )
}