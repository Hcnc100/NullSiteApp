package com.nullpointer.nullsiteadmin.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.domain.InfoUserRepository
import com.nullpointer.nullsiteadmin.models.PersonalInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfoUserViewModel @Inject constructor(
    private val infoUserRepository: InfoUserRepository
) : ViewModel() {

    val infoUser = flow<Resource<PersonalInfo>> {
        infoUserRepository.myPersonalInfo.collect {
            emit(Resource.Success(it))
        }
    }.catch {
        Timber.e("Error to load info user $it")
        emit(Resource.Failure(Exception(it)))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading()
    )


    fun updateAnyFieldUser(
        nameAdmin: String? = null,
        profession: String? = null,
        description: String? = null
    ) = viewModelScope.launch(Dispatchers.IO) {
        infoUserRepository.updateAnyFieldUser(nameAdmin, profession, description)
    }
}