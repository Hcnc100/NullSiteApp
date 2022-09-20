package com.nullpointer.nullsiteadmin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.models.EmailContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailsViewModel @Inject constructor(
    private val emailsRepository: EmailsRepository
) : ViewModel() {

    private val _errorEmail = Channel<Int>()
    val errorEmail = _errorEmail.receiveAsFlow()

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

    fun markAsOpen(email: EmailContact) = launchSafeIO(
        blockIO = { emailsRepository.markAsOpen(email.id) },
        blockException = {
            Timber.e("Error mark as open $email : $it")
            _errorEmail.trySend(R.string.error_unkown)
        }
    )
}