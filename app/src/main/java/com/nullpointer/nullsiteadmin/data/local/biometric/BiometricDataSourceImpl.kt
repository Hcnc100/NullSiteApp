package com.nullpointer.nullsiteadmin.data.local.biometric

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import timber.log.Timber

class BiometricDataSourceImpl(
    private val context: Context
) : BiometricDataSource {

    private val Context.keyguardManager
        get() = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


    override fun launchBiometricInit(
        callbackResult: (Boolean) -> Unit,
        callbackCancel: () -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val biometricPrompt = createBiometricPrompt(
                title = "Escanea tu huella para continuar",
                negativeText = "Cancelar",
                callbackCancel = callbackCancel
            )
            biometricPrompt.authenticate(
                createCancellationSignal(callbackCancel),
                context.mainExecutor,
                createBiometricCallBack(callbackResult, callbackCancel)
            )
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

    override fun enableBiometric(callbackResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val biometricPrompt = createBiometricPrompt(
                title = "Escanea tu huella para comtinuar",
                negativeText = "Cancelar",
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
        callbackResult: (Boolean) -> Unit = {},
        callbackCancel: (() -> Unit) = {},
    ) = @RequiresApi(Build.VERSION_CODES.P)
    object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            callbackResult(false)
            Timber.d("error")
            callbackCancel()
            super.onAuthenticationError(errorCode, errString)
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            callbackResult(true)
            Timber.d("success")
            super.onAuthenticationSucceeded(result)
        }

        override fun onAuthenticationFailed() {
            callbackResult(false)
            Timber.d("failed")
            super.onAuthenticationFailed()
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
