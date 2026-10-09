package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AssetCategory {
    MAK_BOND,      // Magyar Államkincstár (PMÁP, BMÁP, FixMÁP, DKJ, EMÁP)
    TBSZ_PORTFOLIO,// Tartós Befektetési Számla (Stocks, ETFs)
    REGULAR_SAVINGS,
    DIVIDEND_EQUITY
}

enum class MakBondType {
    PMAP,    // Prémium Magyar Állampapír (Inflation + premium)
    BMAP,    // Bónusz Magyar Állampapír (DKJ yield + discount premium)
    FIXMAP,  // FixMÁP (e.g. 7.00% fixed)
    DKJ,     // Diszkont Kincstárjegy
    EMAP,    // Euró Magyar Állampapír
    MAP_PLUSZ,// MÁP Plusz
    OTHER
}

@Entity(tableName = "investments")
data class InvestmentAsset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                    // e.g. "PMÁP 2032/I", "FixMÁP 2029/Q1", "S&P 500 ETF (TBSZ 2023)"
    val category: AssetCategory,
    val makType: MakBondType = MakBondType.OTHER,
    val principalAmount: Double,         // Initial investment / face value (Névérték)
    val currentEstimatedValue: Double,   // Current value including accrued interest or capital growth
    val annualYieldPercent: Double,      // e.g. 6.85% or 7.00%
    val accruedInterest: Double = 0.0,   // Felhalmozott kamat
    val purchaseDate: String = "",       // YYYY-MM-DD
    val maturityDate: String = "",       // YYYY-MM-DD
    val nextCouponDate: String = "",     // Következő kamatfizetési nap
    val tbszOpeningYear: Int = 0,        // e.g. 2023 for a TBSZ account
    val notes: String = "",
    val currency: String = "HUF"
) {
    // TBSZ Tax calculation: 0-3 years: 15% SZJA, 3-5 years: 10%, 5+ years: 0% (Tax free)
    // Magyar Államkincstár (MÁK) bonds are 0% SZJA and 0% szocho exempt!
    val currentTaxRatePercent: Double
        get() {
            if (category == AssetCategory.MAK_BOND) return 0.0
            if (category == AssetCategory.TBSZ_PORTFOLIO && tbszOpeningYear > 0) {
                val currentYear = 2026 // Based on environment time
                val elapsedYears = currentYear - tbszOpeningYear
                return when {
                    elapsedYears >= 5 -> 0.0
                    elapsedYears >= 3 -> 10.0
                    else -> 15.0
                }
            }
            return 15.0
        }

    val taxExemptionStatus: String
        get() = when {
            category == AssetCategory.MAK_BOND -> "MÁK 100% Tax-Free (0% SZJA / Szocho)"
            category == AssetCategory.TBSZ_PORTFOLIO -> {
                val currentYear = 2026
                val elapsed = currentYear - tbszOpeningYear
                when {
                    elapsed >= 5 -> "TBSZ 5+ Yrs: 100% Tax-Free (0% SZJA)"
                    elapsed >= 3 -> "TBSZ 3-5 Yrs: Discounted 10% SZJA"
                    else -> "TBSZ Gyűjtő / 1-3 Yrs (15% SZJA if withdrawn)"
                }
            }
            else -> "Standard Taxable (15% SZJA)"
        }

    val estimatedProfit: Double
        get() = (currentEstimatedValue - principalAmount).coerceAtLeast(0.0)

    val estimatedTaxSavings: Double
        get() {
            // Standard SZJA is 15%. If tax rate is lower, difference is saved.
            val standardTax = estimatedProfit * 0.15
            val actualTax = estimatedProfit * (currentTaxRatePercent / 100.0)
            return (standardTax - actualTax).coerceAtLeast(0.0)
        }

    val expectedAnnualIncome: Double
        get() = principalAmount * (annualYieldPercent / 100.0)
}
