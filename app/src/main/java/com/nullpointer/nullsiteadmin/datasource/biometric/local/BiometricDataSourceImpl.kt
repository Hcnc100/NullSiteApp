package com.nullpointer.nullsiteadmin.datasource.biometric.local

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT
import android.hardware.biometrics.BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT_PERMANENT
import android.hardware.biometrics.BiometricPrompt.BIOMETRIC_ERROR_USER_CANCELED
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.BiometricResultState
import com.nullpointer.nullsiteadmin.core.utils.extensions.keyguardManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber

/**
 * Implementation of the BiometricDataSource interface.
 * This class handles the biometric authentication in the application.
 */
class BiometricDataSourceImpl(
    private val context: Context
) : BiometricDataSource {

    /**
     * Initiates the biometric authentication process to enable the app lock.
     * @return BiometricResult - the result of the biometric authentication process.
     */
    override suspend fun enableFingerBiometric(): BiometricResultState {
        return launchBiometric(
            messageBiometric = context.getString(R.string.title_text_enable_lock_app),
            messageCancel = context.getString(R.string.text_title_cancel)
        )
    }

    /**
     * Initiates the biometric authentication process to unlock the app.
     * @return BiometricResult - the result of the biometric authentication process.
     */
    override suspend fun unlockByFingerBiometric(): BiometricResultState {
        return launchBiometric(
            messageBiometric = context.getString(R.string.text_title_unlock_app),
            messageCancel = context.getString(R.string.text_title_cancel)
        )
    }

    /**
     * Initiates the biometric authentication process.
     * @param messageBiometric - the message to be displayed in the biometric prompt.
     * @param messageCancel - the message to be displayed for the cancel option in the biometric prompt.
     * @return BiometricResult - the result of the biometric authentication process.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    // This function initiates the biometric authentication process.
    suspend fun launchBiometric(
        messageBiometric: String,
        messageCancel: String
    ): BiometricResultState {
        // Check if the Android version is less than Build.VERSION_CODES.P.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return BiometricResultState.NOT_SUPPORTED
        }

        // Start the biometric authentication process.
        return suspendCancellableCoroutine { continuation ->
            // Create a BiometricPrompt.AuthenticationCallback to handle the results of the authentication process.
            val callback = @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                // Handle the error cases.
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    Timber.d("Error biometric: $errorCode $errString")
                    when (errorCode) {
                        BIOMETRIC_ERROR_LOCKOUT -> continuation.resume(BiometricResultState.TEMPORARILY_LOCKED) {}
                        BIOMETRIC_ERROR_LOCKOUT_PERMANENT -> continuation.resume(
                            BiometricResultState.DISABLE
                        ) {}

                        BIOMETRIC_ERROR_USER_CANCELED -> continuation.cancel()
                    }
                }

                // Handle the success case.
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Timber.d("Auth success")
                    continuation.resume(BiometricResultState.PASSED) {}
                }
            }

            // Create a BiometricPrompt and a CancellationSignal.
            val promptInfo = createBiometricPrompt(
                title = messageBiometric,
                negativeText = messageCancel,
                callbackCancel = { continuation.cancel() }
            )
            val cancellationSignal = createCancellationSignal { continuation.cancel() }

            // Start the authentication process.
            promptInfo.authenticate(
                cancellationSignal,
                context.mainExecutor,
                callback
            )

            // Handle the cancellation of the coroutine.
            continuation.invokeOnCancellation {
                cancellationSignal.cancel()
            }
        }
    }

    /**
     * Checks if the device supports biometric authentication.
     * @return Boolean - true if the device supports biometric authentication, false otherwise.
     */
    override fun checkBiometricSupport(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!context.keyguardManager.isDeviceSecure) {
                Timber.e("The device is not secure")
                return false
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.USE_BIOMETRIC
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.e("Biometric permission is not granted")
                return false
            }
            return context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        } else {
            return false
        }
    }

    /**
     * Creates a cancellation signal for the biometric authentication process.
     * @param callbackCancel - the callback to be invoked when the cancellation signal is triggered.
     * @return CancellationSignal - the created cancellation signal.
     */
    private fun createCancellationSignal(
        callbackCancel: (() -> Unit) = {}
    ): CancellationSignal {
        return CancellationSignal().apply {
            setOnCancelListener { callbackCancel() }
        }
    }

    /**
     * Creates a biometric prompt for the biometric authentication process.
     * @param title - the title to be displayed in the biometric prompt.
     * @param negativeText - the text to be displayed for the cancel option in the biometric prompt.
     * @param callbackCancel - the callback to be invoked when the cancel option is selected.
     * @return BiometricPrompt - the created biometric prompt.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun createBiometricPrompt(
        title: String,
        negativeText: String,
        callbackCancel: (() -> Unit)? = null
    ): BiometricPrompt {
        return BiometricPrompt.Builder(context).apply {
            setTitle(title)
            setNegativeButton(
                /* text = */ negativeText,
                /* executor = */ context.mainExecutor
            ) { dialog: DialogInterface?, _: Int ->
                dialog?.cancel()
                callbackCancel?.invoke()
            }
        }.build()
    }

}