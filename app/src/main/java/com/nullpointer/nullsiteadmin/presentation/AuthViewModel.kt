package com.nullpointer.nullsiteadmin.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messageErrorAuth = Channel<Int>()
    val messageErrorAuth = _messageErrorAuth.receiveAsFlow()

    val isUserAuth = flow<Resource<Boolean>> {
        authRepository.isUserAuth.collect {
            emit(Resource.Success(it))
        }
    }.flowOn(
        Dispatchers.IO
    ).catch {
        Timber.e("Error to load info auth $it")
        _messageErrorAuth.trySend(R.string.error_unkown)
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    // * this is no savable because when the activity finish process (for example no memory)
    // * the function auth finish and restore state of "isAuthenticating"
    var isAuthenticating by mutableStateOf(false)
        private set


    fun authWithEmailAndPassword(
        dataUser: Pair<String, String>?
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            isAuthenticating = true
            val (email, password) = dataUser!!
            authRepository.authUserWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is NullPointerException -> _messageErrorAuth.trySend(R.string.error_data_invalid)
                else -> {
                    _messageErrorAuth.trySend(R.string.error_authenticated)
                    Timber.e("Error auth $e")
                }
            }
        } finally {
            isAuthenticating = false
        }
    }

    fun logOut(){
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.logout()
        }
    }
}