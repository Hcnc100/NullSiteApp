package com.nullpointer.nullsiteadmin.datasource.biometric.local

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT
import android.hardware.biometrics.BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT_PERMANENT
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.actions.BiometricResult
import com.nullpointer.nullsiteadmin.core.utils.extensions.keyguardManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber

class BiometricDataSourceImpl(
    private val context: Context
) : BiometricDataSource {
    override suspend fun enableFingerBiometric(): BiometricResult {
        return launchBiometric(
            messageBiometric = context.getString(R.string.title_text_enable_lock_app),
            messageCancel = context.getString(R.string.text_title_cancel)
        )
    }

    override suspend fun launchFingerBiometric(): BiometricResult {
        return launchBiometric(
            messageBiometric = context.getString(R.string.text_title_unlock_app),
            messageCancel = context.getString(R.string.text_title_cancel)
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun launchBiometric(
        messageBiometric: String,
        messageCancel: String
    ): BiometricResult {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return suspendCancellableCoroutine { continuation ->
                val callback = object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        Timber.d("Error biometric: $errorCode $errString")
                        when (errorCode) {
                            BIOMETRIC_ERROR_LOCKOUT -> continuation.resume(BiometricResult.LOCKED_TIME_OUT) {}
                            BIOMETRIC_ERROR_LOCKOUT_PERMANENT -> continuation.resume(BiometricResult.DISABLE_ALWAYS) {}
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Timber.d("Auth success")
                        continuation.resume(BiometricResult.PASSED) {}
                    }
                }
                val promptInfo = createBiometricPrompt(
                    title = messageBiometric,
                    negativeText = messageCancel,
                )
                promptInfo.authenticate(
                    createCancellationSignal(),
                    context.mainExecutor,
                    callback
                )
            }
        } else {
            throw RuntimeException("Not supported device")
        }
    }


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


    private fun createCancellationSignal(
        callbackCancel: (() -> Unit) = {}
    ): CancellationSignal {
        return CancellationSignal().apply {
            setOnCancelListener { callbackCancel() }
        }
    }


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
