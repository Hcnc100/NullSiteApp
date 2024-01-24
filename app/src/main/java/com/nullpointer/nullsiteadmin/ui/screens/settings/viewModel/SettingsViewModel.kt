package com.nullpointer.nullsiteadmin.ui.screens.settings.viewModel

import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.domain.biometric.BiometricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val biometricRepository: BiometricRepository
) : ViewModel() {


}