package com.example.ui.security

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

enum class BiometricStatus {
    READY,
    NOT_ENROLLED,
    NO_HARDWARE,
    HARDWARE_UNAVAILABLE,
    UNKNOWN_ERROR
}

object BiometricAuthManager {

    private const val AUTHENTICATORS = BIOMETRIC_STRONG or BIOMETRIC_WEAK

    fun checkBiometricStatus(context: Context): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(AUTHENTICATORS)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
            else -> BiometricStatus.UNKNOWN_ERROR
        }
    }

    fun getStatusLabel(status: BiometricStatus): String {
        return when (status) {
            BiometricStatus.READY -> "Sensor Ready & Enrolled"
            BiometricStatus.NOT_ENROLLED -> "No Biometrics Enrolled on Device"
            BiometricStatus.NO_HARDWARE -> "No Biometric Hardware Detected"
            BiometricStatus.HARDWARE_UNAVAILABLE -> "Sensor Currently Unavailable"
            BiometricStatus.UNKNOWN_ERROR -> "Biometrics Unsupported"
        }
    }

    fun getStatusDescription(status: BiometricStatus): String {
        return when (status) {
            BiometricStatus.READY -> "Fingerprint or face recognition is configured and ready for secure login."
            BiometricStatus.NOT_ENROLLED -> "Your Android device supports biometric security, but no fingerprint is enrolled. You can enroll in Android Settings or use your PIN."
            BiometricStatus.NO_HARDWARE -> "This device does not have biometric hardware. Secure PIN login is fully operational."
            BiometricStatus.HARDWARE_UNAVAILABLE -> "The biometric sensor is temporarily occupied or unavailable. Please use your PIN."
            BiometricStatus.UNKNOWN_ERROR -> "Biometric verification is not supported. Please use your PIN."
        }
    }

    @Suppress("DEPRECATION")
    fun openBiometricEnrollSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    AUTHENTICATORS
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Intent(Settings.ACTION_FINGERPRINT_ENROLL)
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }

        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (_: Exception) {
                // If device has no security settings activity
            }
        }
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String = "Unlock WealthFlow",
        subtitle: String = "Confirm your fingerprint to access your financial data",
        negativeButtonText: String = "Use PIN",
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (errorCode: Int, errString: CharSequence) -> Unit,
        onFailed: () -> Unit
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(AUTHENTICATORS)
            .setNegativeButtonText(negativeButtonText)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}

fun Context.findFragmentActivity(): FragmentActivity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) {
            return current
        }
        current = current.baseContext
    }
    return null
}
