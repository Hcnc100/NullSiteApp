package com.nullpointer.nullsiteadmin.ui.screens.email.emial.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager.sendMessageErrorToException
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.models.email.data.EmailData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
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
        requestLastEmail()
    }

    val listEmails =
        emailsRepository.listEmails.transform<List<EmailData>, Resource<List<EmailData>>> {
            emit(Resource.Success(it))
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
        actionSuccessConcatenate: () -> Unit = {}
    ) = launchSafeIO(
        isEnabled = isEnabledConcatenateEmail,
        blockBefore = { isConcatenateEmail = true },
        blockAfter = { isConcatenateEmail = false },
        blockIO = {
            val numberEmailsRequest = emailsRepository.concatenateEmails()
            Timber.d("Number of emails concatenate $numberEmailsRequest")
            delay(200)
            withContext(Dispatchers.Main) {
                isEnabledConcatenateEmail = numberEmailsRequest != 0
                actionSuccessConcatenate()
            }
        },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                channel = _errorEmail,
                message = "Error to concatenate emails"
            )
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
            delay(300)
            sendMessageErrorToException(
                exception = it,
                channel = _errorEmail,
                message = "Error deleter email $idEmail"
            )
        }
    )

    fun requestLastEmail() = launchSafeIO(
        isEnabled = !isRequestEmail,
        blockBefore = { isRequestEmail = true },
        blockAfter = { isRequestEmail = false },
        blockIO = {
            val sizeRequest = emailsRepository.requestLastEmail()
            Timber.d("News emails receiver $sizeRequest")
            delay(200)
            withContext(Dispatchers.Main) {
                isEnabledConcatenateEmail = true
            }
        },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                channel = _errorEmail,
                message = "Error request emails"
            )
        }
    )

    fun markAsOpen(email: EmailData) = launchSafeIO(
        isEnabled = !email.isOpen,
        blockIO = { emailsRepository.markAsOpen(email) },
        blockException = { Timber.e("Error mark as open $email : $it") }
    )
}