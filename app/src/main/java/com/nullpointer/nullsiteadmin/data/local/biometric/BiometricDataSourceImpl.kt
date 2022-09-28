package com.nullpointer.nullsiteadmin.data.local.biometric

import android.app.KeyguardManager
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
import timber.log.Timber

class BiometricDataSourceImpl(
    private val context: Context
) : BiometricDataSource {

    private val keyguardManager
        get() = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


    override fun launchBiometricInit(
        callbackResult: (BiometricResult) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val biometricPrompt = createBiometricPrompt(
                title = context.getString(R.string.text_title_unlock_app),
                negativeText = context.getString(R.string.text_title_cancel)
            )
            biometricPrompt.authenticate(
                createCancellationSignal(),
                context.mainExecutor,
                createBiometricCallBack(callbackResult)
            )
        } else {
            throw RuntimeException("Not supported device")
        }
    }


    override fun checkBiometricSupport(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!keyguardManager.isDeviceSecure) {
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

    override fun enableBiometric(
        callbackResult: (BiometricResult) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val biometricPrompt = createBiometricPrompt(
                title = context.getString(R.string.title_text_enable_lock_app),
                negativeText = context.getString(R.string.text_title_cancel),
            )
            biometricPrompt.authenticate(
                createCancellationSignal(),
                context.mainExecutor,
                createBiometricCallBack(callbackResult)
            )
        } else {
            throw RuntimeException("Not supported device")
        }
    }

    private fun createCancellationSignal(
        callbackCancel: (() -> Unit) = {}
    ): CancellationSignal {
        return CancellationSignal().apply {
            setOnCancelListener { callbackCancel() }
        }
    }


    private fun createBiometricCallBack(
        callbackResult: (BiometricResult) -> Unit = {}
    ) = @RequiresApi(Build.VERSION_CODES.P)
    object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            Timber.d("Error biometric: $errorCode $errString")
            when (errorCode) {
                BIOMETRIC_ERROR_LOCKOUT -> callbackResult(BiometricResult.LOCKED_TIME_OUT)
                BIOMETRIC_ERROR_LOCKOUT_PERMANENT -> callbackResult(BiometricResult.DISABLE_ALWAYS)
            }
            super.onAuthenticationError(errorCode, errString)
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            callbackResult(BiometricResult.PASSED)
            super.onAuthenticationSucceeded(result)
        }
    }

    private fun createBiometricPrompt(
        title: String,
        negativeText: String,
        callbackCancel: (() -> Unit)? = null
    ): BiometricPrompt {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricPrompt.Builder(context)
                .setTitle(title).apply {
                    setNegativeButton(
                        /* text = */ negativeText,
                        /* executor = */ context.mainExecutor
                    ) { dialog: DialogInterface?, _: Int ->
                        dialog?.cancel()
                        callbackCancel?.invoke()
                    }
                }
                .build()
        } else {
            throw RuntimeException("This implementation is only supported on Android API level 21 or above")
        }
    }

}
