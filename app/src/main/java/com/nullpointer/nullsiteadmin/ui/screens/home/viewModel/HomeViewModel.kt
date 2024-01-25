package com.nullpointer.nullsiteadmin.ui.screens.home.viewModel

import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    init {
        verifyPhoneData()
    }

    fun logout() = launchSafeIO {
        authRepository.logout()
    }

    private fun verifyPhoneData() = launchSafeIO {
        authRepository.verifyInfoPhoneData()
    }
}