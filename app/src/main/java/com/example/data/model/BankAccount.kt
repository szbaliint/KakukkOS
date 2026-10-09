package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BankAccountType {
    CHECKING,
    SAVINGS,
    INVESTMENT,
    TREASURY_MAK,
    CRYPTO_WALLET
}

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val institutionName: String, // e.g. "OTP Bank", "Magyar Államkincstár (MÁK)", "Erste Bank", "Revolut", "K&H Bank", "Wise"
    val accountHolder: String,
    val accountNumberMasked: String, // e.g. "•••• 8912"
    val balance: Double,
    val currency: String = "HUF",
    val type: BankAccountType = BankAccountType.CHECKING,
    val isAutoSyncEnabled: Boolean = false,
    val lastSyncedTime: Long = System.currentTimeMillis(),
    val connectionStatus: String = "Private Account",
    val logoTag: String = "bank"
)
