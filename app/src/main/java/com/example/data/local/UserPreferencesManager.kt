package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fortis_wealth_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
        private const val KEY_USER_PIN = "key_user_pin"
        private const val KEY_PIN_HASH = "key_pin_hash"
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_CURRENCY = "key_currency"
        private const val KEY_SYSTEM_NOTIF_ENABLED = "key_system_notif_enabled"
        private const val KEY_NOTIF_MAK = "key_notif_mak"
        private const val KEY_NOTIF_TBSZ = "key_notif_tbsz"
        private const val KEY_NOTIF_BUDGET = "key_notif_budget"
        private const val KEY_NOTIF_MARKET = "key_notif_market"

        fun hashPin(pin: String): String {
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val digest = md.digest(("lifeos_salt_2026_$pin").toByteArray(Charsets.UTF_8))
            return digest.joinToString("") { "%02x".format(it) }
        }
    }

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, true)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var userPin: String
        get() = prefs.getString(KEY_USER_PIN, "2026") ?: "2026"
        set(value) {
            prefs.edit()
                .putString(KEY_USER_PIN, value)
                .putString(KEY_PIN_HASH, hashPin(value))
                .apply()
        }

    fun verifyPin(candidatePin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null)
        return if (storedHash != null) {
            hashPin(candidatePin) == storedHash
        } else {
            candidatePin == userPin
        }
    }

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "Portfolio Owner") ?: "Portfolio Owner"
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var currency: String
        get() = prefs.getString(KEY_CURRENCY, "HUF") ?: "HUF"
        set(value) = prefs.edit().putString(KEY_CURRENCY, value).apply()

    var isSystemNotifEnabled: Boolean
        get() = prefs.getBoolean(KEY_SYSTEM_NOTIF_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SYSTEM_NOTIF_ENABLED, value).apply()

    var isNotifMakEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_MAK, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_MAK, value).apply()

    var isNotifTbszEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_TBSZ, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_TBSZ, value).apply()

    var isNotifBudgetEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_BUDGET, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_BUDGET, value).apply()

    var isNotifMarketEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_MARKET, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_MARKET, value).apply()

    fun resetPreferences() {
        prefs.edit().clear().apply()
    }
}
