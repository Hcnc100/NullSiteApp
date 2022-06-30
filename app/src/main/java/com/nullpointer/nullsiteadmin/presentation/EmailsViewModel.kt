package com.nullpointer.nullsiteadmin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.domain.email.EmailsRepository
import com.nullpointer.nullsiteadmin.models.EmailContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailsViewModel @Inject constructor(
    private val emailsRepository: EmailsRepository
) : ViewModel() {

    private val _errorEmail = Channel<String>()
    val errorEmail = _errorEmail.receiveAsFlow()

    val listEmails = flow<Resource<List<EmailContact>>> {
        emailsRepository.listEmails.collect {
            emit(Resource.Success(it))
        }
    }.flowOn(Dispatchers.IO).catch {
        Timber.e("Error to load emails $it")
        _errorEmail.trySend("Error to load list emails")
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    fun deleterEmail(
        idEmail: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            emailsRepository.deleterEmail(idEmail)
        } catch (e: Exception) {
            Timber.e("Error to delete email $idEmail")
            when (e) {
                is CancellationException -> throw e
                else -> {
                    _errorEmail.trySend("Error deleter this email")
                }
            }
        }
    }
}