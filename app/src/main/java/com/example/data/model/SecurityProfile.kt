package com.example.data.model

data class SecurityProfile(
    val isBiometricEnabled: Boolean = true,
    val isMfaEnabled: Boolean = true,
    val mfaSecret: String = "JBSWY3DPEHPK3PXP", // Standard base32 TOTP secret for authenticator
    val pinCode: String = "2026", // Default secure PIN
    val isCloudBackupActive: Boolean = true,
    val autoLockDurationSeconds: Int = 180,
    val lastCloudBackupTime: Long = System.currentTimeMillis() - 3600000,
    val cloudBackupProvider: String = "WealthFlow Cloud Encrypted Vault (AES-256)",
    val securityLevel: String = "Fort Knox (Maximum 2FA & Biometrics)"
)
