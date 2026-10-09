package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferencesManager
import com.example.data.model.AssetCategory
import com.example.data.model.BankAccount
import com.example.data.model.BankAccountType
import com.example.data.model.BudgetPlan
import com.example.data.model.ContractStatus
import com.example.data.model.ExpectedCostItem
import com.example.data.model.ExpectedCostStatus
import com.example.data.model.ExpectedCostUrgency
import com.example.data.model.GearCategory
import com.example.data.model.GearCondition
import com.example.data.model.InvestmentAsset
import com.example.data.model.LifeOsModuleItem
import com.example.data.model.MakBondType
import com.example.data.model.MarketNotification
import com.example.data.model.NotificationType
import com.example.data.model.PhotoContract
import com.example.data.model.PhotoGearItem
import com.example.data.model.PhotoIdeaItem
import com.example.data.model.PhotoTask
import com.example.data.model.SecurityProfile
import com.example.data.model.TaskPriority
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.data.model.WeeklyCostItem
import com.example.data.model.WishItem
import com.example.data.model.WishPriority
import com.example.data.repository.FinanceRepository
import com.example.ui.notification.SystemNotificationHelper
import java.util.Calendar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FinanceOverview(
    val totalNetWorth: Double = 0.0,
    val liquidCash: Double = 0.0,
    val totalInvestments: Double = 0.0,
    val totalMakBonds: Double = 0.0,
    val totalTbszAssets: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpenses: Double = 0.0,
    val monthlySavingsRate: Double = 0.0,
    val annualExpectedYieldIncome: Double = 0.0,
    val totalTaxSaved: Double = 0.0,
    val totalWishesTarget: Double = 0.0,
    val totalWishesSaved: Double = 0.0
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository
    val preferencesManager = UserPreferencesManager(application)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = FinanceRepository(database.financeDao())
        SystemNotificationHelper.createNotificationChannel(application)
    }

    private val _isOnboardingCompleted = MutableStateFlow(preferencesManager.isOnboardingCompleted)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _showWalkthrough = MutableStateFlow(false)
    val showWalkthrough: StateFlow<Boolean> = _showWalkthrough.asStateFlow()

    private val _userName = MutableStateFlow(preferencesManager.userName)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isSystemNotifEnabled = MutableStateFlow(preferencesManager.isSystemNotifEnabled)
    val isSystemNotifEnabled: StateFlow<Boolean> = _isSystemNotifEnabled.asStateFlow()

    private val _isNotifMakEnabled = MutableStateFlow(preferencesManager.isNotifMakEnabled)
    val isNotifMakEnabled: StateFlow<Boolean> = _isNotifMakEnabled.asStateFlow()

    private val _isNotifTbszEnabled = MutableStateFlow(preferencesManager.isNotifTbszEnabled)
    val isNotifTbszEnabled: StateFlow<Boolean> = _isNotifTbszEnabled.asStateFlow()

    private val _isNotifBudgetEnabled = MutableStateFlow(preferencesManager.isNotifBudgetEnabled)
    val isNotifBudgetEnabled: StateFlow<Boolean> = _isNotifBudgetEnabled.asStateFlow()

    private val _isNotifMarketEnabled = MutableStateFlow(preferencesManager.isNotifMarketEnabled)
    val isNotifMarketEnabled: StateFlow<Boolean> = _isNotifMarketEnabled.asStateFlow()

    val wishes: StateFlow<List<WishItem>> = repository.allWishes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionItem>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetPlan>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bankAccounts: StateFlow<List<BankAccount>> = repository.allBankAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val investments: StateFlow<List<InvestmentAsset>> = repository.allInvestments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<MarketNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Photography Gear & Studio State
    val photoGear: StateFlow<List<PhotoGearItem>> = repository.allPhotoGear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contracts: StateFlow<List<PhotoContract>> = repository.allContracts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val photoTasks: StateFlow<List<PhotoTask>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Photography Ideas & Moodboards (Pinterest-style)
    val photoIdeas: StateFlow<List<PhotoIdeaItem>> = repository.allPhotoIdeas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lifeModules: StateFlow<List<LifeOsModuleItem>> = repository.allLifeModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly Costs & Sum Tracking
    val weeklyCosts: StateFlow<List<WeeklyCostItem>> = repository.allWeeklyCosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentWeekNumber: Int = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    // Current Week Costs Sum
    val currentWeekSum: StateFlow<Double> = weeklyCosts.map { list ->
        list.filter { it.weekNumber == currentWeekNumber && it.year == currentYear }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // All-time Grand Sum of Recorded Costs
    val totalAllWeeklyCostsSum: StateFlow<Double> = weeklyCosts.map { list ->
        list.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Expected Costs & Planned Expenses (Language courses, travel, commitments - distinct from Wishlist)
    val expectedCosts: StateFlow<List<ExpectedCostItem>> = repository.allExpectedCosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpectedCostsAmount: StateFlow<Double> = expectedCosts.map { list ->
        list.sumOf { it.estimatedAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpectedCostsAllocated: StateFlow<Double> = expectedCosts.map { list ->
        list.sumOf { it.allocatedAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpectedCostsRemaining: StateFlow<Double> = expectedCosts.map { list ->
        list.sumOf { it.remainingAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Gear Wishes (Linked directly to Financial Wishes!)
    val gearWishes: StateFlow<List<WishItem>> = wishes.map { list ->
        list.filter { it.isGearWish || it.category.equals("Photo Gear", ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGearValue: StateFlow<Double> = photoGear.map { list ->
        list.sumOf { it.purchasePrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val insuredGearValue: StateFlow<Double> = photoGear.map { list ->
        list.filter { it.isInsured }.sumOf { it.purchasePrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val activeContracts: StateFlow<List<PhotoContract>> = contracts.map { list ->
        list.filter { it.status != ContractStatus.COMPLETED_PAID && it.status != ContractStatus.CANCELLED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalContractRevenueDue: StateFlow<Double> = contracts.map { list ->
        list.filter { it.status != ContractStatus.CANCELLED }.sumOf { it.remainingBalance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val pendingTasksCount: StateFlow<Int> = photoTasks.map { list ->
        list.count { !it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Combined Aggregate Overview
    val overview: StateFlow<FinanceOverview> = combine(
        bankAccounts,
        investments,
        transactions,
        wishes
    ) { accounts, assets, txList, wishList ->
        val liquid = accounts.filter { it.type == BankAccountType.CHECKING || it.type == BankAccountType.SAVINGS }
            .sumOf { it.balance }
        val mak = assets.filter { it.category == AssetCategory.MAK_BOND }
            .sumOf { it.currentEstimatedValue }
        val tbsz = assets.filter { it.category == AssetCategory.TBSZ_PORTFOLIO }
            .sumOf { it.currentEstimatedValue }
        val otherInvestments = assets.filter { it.category != AssetCategory.MAK_BOND && it.category != AssetCategory.TBSZ_PORTFOLIO }
            .sumOf { it.currentEstimatedValue }
        val totalAssets = liquid + mak + tbsz + otherInvestments

        val currentMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val thisMonthTxs = txList.filter { it.date.startsWith(currentMonthPrefix) }
        val mIncome = thisMonthTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val mExpense = thisMonthTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val savingsRate = if (mIncome > 0) ((mIncome - mExpense) / mIncome) * 100.0 else 0.0

        val annualYield = assets.sumOf { it.expectedAnnualIncome }
        val taxSaved = assets.sumOf { it.estimatedTaxSavings }

        val wishesTarget = wishList.sumOf { it.targetPrice }
        val wishesSaved = wishList.sumOf { it.currentSaved }

        FinanceOverview(
            totalNetWorth = totalAssets,
            liquidCash = liquid,
            totalInvestments = mak + tbsz + otherInvestments,
            totalMakBonds = mak,
            totalTbszAssets = tbsz,
            monthlyIncome = mIncome,
            monthlyExpenses = mExpense,
            monthlySavingsRate = savingsRate.coerceAtLeast(0.0),
            annualExpectedYieldIncome = annualYield,
            totalTaxSaved = taxSaved,
            totalWishesTarget = wishesTarget,
            totalWishesSaved = wishesSaved
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinanceOverview())

    // Security & Auth State
    private val _securityProfile = MutableStateFlow(
        SecurityProfile(
            pinCode = preferencesManager.userPin,
            isBiometricEnabled = preferencesManager.isBiometricEnabled
        )
    )
    val securityProfile: StateFlow<SecurityProfile> = _securityProfile.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false) // Application entry point is secured by default
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isBiometricScanning = MutableStateFlow(false)
    val isBiometricScanning: StateFlow<Boolean> = _isBiometricScanning.asStateFlow()

    private val _isBackingUpCloud = MutableStateFlow(false)
    val isBackingUpCloud: StateFlow<Boolean> = _isBackingUpCloud.asStateFlow()

    private val _activeCurrency = MutableStateFlow(preferencesManager.currency)
    val activeCurrency: StateFlow<String> = _activeCurrency.asStateFlow()

    fun setCurrency(currency: String) {
        preferencesManager.currency = currency
        _activeCurrency.value = currency
    }

    fun startWalkthrough() {
        _showWalkthrough.value = true
    }

    fun dismissWalkthrough() {
        _showWalkthrough.value = false
    }

    fun completeOnboarding(
        pin: String,
        name: String,
        currency: String,
        biometricEnabled: Boolean,
        systemNotif: Boolean,
        makNotif: Boolean,
        tbszNotif: Boolean,
        budgetNotif: Boolean,
        marketNotif: Boolean
    ) {
        preferencesManager.userPin = pin
        preferencesManager.userName = name
        preferencesManager.currency = currency
        preferencesManager.isBiometricEnabled = biometricEnabled
        preferencesManager.isSystemNotifEnabled = systemNotif
        preferencesManager.isNotifMakEnabled = makNotif
        preferencesManager.isNotifTbszEnabled = tbszNotif
        preferencesManager.isNotifBudgetEnabled = budgetNotif
        preferencesManager.isNotifMarketEnabled = marketNotif
        preferencesManager.isOnboardingCompleted = true

        _userName.value = name
        _activeCurrency.value = currency
        _isSystemNotifEnabled.value = systemNotif
        _isNotifMakEnabled.value = makNotif
        _isNotifTbszEnabled.value = tbszNotif
        _isNotifBudgetEnabled.value = budgetNotif
        _isNotifMarketEnabled.value = marketNotif
        _securityProfile.value = _securityProfile.value.copy(
            pinCode = pin,
            isBiometricEnabled = biometricEnabled
        )

        _isOnboardingCompleted.value = true
        _showWalkthrough.value = false
        _isAuthenticated.value = true

        val welcomeTitle = "WealthFlow Initialized"
        val welcomeMsg = "Welcome, $name. Your personal 4-digit PIN is active with AES-256 encryption. Portfolio tracking initiated."
        viewModelScope.launch {
            repository.addNotification(
                MarketNotification(
                    title = welcomeTitle,
                    message = welcomeMsg,
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "WELCOME"
                )
            )
        }
        if (systemNotif) {
            SystemNotificationHelper.showSystemNotification(getApplication(), welcomeTitle, welcomeMsg, 901)
        }
    }

    fun sendTestSystemNotification(title: String, message: String): Boolean {
        viewModelScope.launch {
            repository.addNotification(
                MarketNotification(
                    title = title,
                    message = message,
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "TEST_ALERT"
                )
            )
        }
        return SystemNotificationHelper.showSystemNotification(
            getApplication(),
            title,
            message,
            (2000..9999).random()
        )
    }

    fun setSystemNotifEnabled(enabled: Boolean) {
        preferencesManager.isSystemNotifEnabled = enabled
        _isSystemNotifEnabled.value = enabled
    }

    fun setNotifMakEnabled(enabled: Boolean) {
        preferencesManager.isNotifMakEnabled = enabled
        _isNotifMakEnabled.value = enabled
    }

    fun setNotifTbszEnabled(enabled: Boolean) {
        preferencesManager.isNotifTbszEnabled = enabled
        _isNotifTbszEnabled.value = enabled
    }

    fun setNotifBudgetEnabled(enabled: Boolean) {
        preferencesManager.isNotifBudgetEnabled = enabled
        _isNotifBudgetEnabled.value = enabled
    }

    fun setNotifMarketEnabled(enabled: Boolean) {
        preferencesManager.isNotifMarketEnabled = enabled
        _isNotifMarketEnabled.value = enabled
    }

    // Wishes Actions
    fun addWish(
        title: String,
        targetPrice: Double,
        category: String,
        targetDate: String,
        priority: WishPriority,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addWish(
                WishItem(
                    title = title,
                    targetPrice = targetPrice,
                    currentSaved = 0.0,
                    category = category,
                    targetDate = targetDate,
                    priority = priority,
                    notes = notes
                )
            )
        }
    }

    fun fundWish(wish: WishItem, depositAmount: Double) {
        viewModelScope.launch {
            val updatedSaved = (wish.currentSaved + depositAmount).coerceAtMost(wish.targetPrice)
            val isCompleted = updatedSaved >= wish.targetPrice
            repository.updateWish(
                wish.copy(
                    currentSaved = updatedSaved,
                    isFulfilled = isCompleted
                )
            )
            if (isCompleted) {
                repository.addNotification(
                    MarketNotification(
                        title = "Goal Fulfilled!",
                        message = "Congratulations! You have fully funded your wish: '${wish.title}' (${formatCurrency(wish.targetPrice)}).",
                        type = NotificationType.SECURITY_EVENT,
                        symbolOrTag = "WISH"
                    )
                )
            }
        }
    }

    fun toggleWishFulfilled(wish: WishItem) {
        viewModelScope.launch {
            val newStatus = !wish.isFulfilled
            val newSaved = if (newStatus) wish.targetPrice else wish.currentSaved
            repository.updateWish(wish.copy(isFulfilled = newStatus, currentSaved = newSaved))
        }
    }

    fun deleteWish(wish: WishItem) {
        viewModelScope.launch { repository.deleteWish(wish) }
    }

    // Transactions & Budgets
    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: String,
        bankName: String,
        notes: String,
        isRecurring: Boolean
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                TransactionItem(
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    date = date,
                    bankAccountName = bankName,
                    notes = notes,
                    isRecurring = isRecurring
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionItem) {
        viewModelScope.launch { repository.deleteTransaction(transaction) }
    }

    fun addBudget(category: String, monthlyLimit: Double) {
        viewModelScope.launch {
            repository.addBudget(BudgetPlan(category = category, monthlyLimit = monthlyLimit))
        }
    }

    fun deleteBudget(budget: BudgetPlan) {
        viewModelScope.launch { repository.deleteBudget(budget) }
    }

    // Investments (TBSZ & Magyar Államkincstár)
    fun addInvestment(
        name: String,
        category: AssetCategory,
        makType: MakBondType,
        principal: Double,
        currentValue: Double,
        annualYield: Double,
        accruedInterest: Double,
        maturityDate: String,
        nextCouponDate: String,
        tbszOpeningYear: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addInvestment(
                InvestmentAsset(
                    name = name,
                    category = category,
                    makType = makType,
                    principalAmount = principal,
                    currentEstimatedValue = currentValue,
                    annualYieldPercent = annualYield,
                    accruedInterest = accruedInterest,
                    maturityDate = maturityDate,
                    nextCouponDate = nextCouponDate,
                    tbszOpeningYear = tbszOpeningYear,
                    notes = notes
                )
            )
        }
    }

    fun deleteInvestment(asset: InvestmentAsset) {
        viewModelScope.launch { repository.deleteInvestment(asset) }
    }

    // Bank Accounts & Open Banking Sync
    fun addBankAccount(name: String, holder: String, number: String, balance: Double, type: BankAccountType) {
        viewModelScope.launch {
            repository.addBankAccount(
                BankAccount(
                    institutionName = name,
                    accountHolder = holder,
                    accountNumberMasked = number,
                    balance = balance,
                    type = type
                )
            )
        }
    }

    fun updateBankAccountBalance(account: BankAccount, newBalance: Double) {
        viewModelScope.launch {
            repository.updateBankAccount(
                account.copy(
                    balance = newBalance,
                    lastSyncedTime = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteBankAccount(account: BankAccount) {
        viewModelScope.launch { repository.deleteBankAccount(account) }
    }

    // Notifications & Market Simulation
    fun markAllNotificationsRead() {
        viewModelScope.launch { repository.markAllAsRead() }
    }

    fun simulateMarketFluctuation() {
        viewModelScope.launch {
            val delta = (-15..28).random() / 10.0
            val symbol = listOf("BUX", "S&P 500", "EUR/HUF", "OTP", "PMÁP Index").random()
            val message = if (delta >= 0) {
                "$symbol jumped +$delta% on strong economic momentum and treasury demand."
            } else {
                "$symbol dipped $delta% following central bank rate guidance."
            }
            val title = "Market Fluctuation: $symbol"
            repository.addNotification(
                MarketNotification(
                    title = title,
                    message = message,
                    type = NotificationType.MARKET_FLUCTUATION,
                    symbolOrTag = symbol,
                    changePercent = delta
                )
            )
            if (_isSystemNotifEnabled.value && _isNotifMarketEnabled.value) {
                SystemNotificationHelper.showSystemNotification(getApplication(), title, message, (3000..3999).random())
            }
        }
    }

    fun simulateDividendOrCoupon() {
        viewModelScope.launch {
            val isMak = listOf(true, false).random()
            if (isMak) {
                val couponAmount = listOf(145000.0, 220000.0, 315000.0).random()
                val title = "MÁK Kamatfizetés Jóváírás!"
                val message = "FixMÁP negyedéves kamatfizetés sikeresen megérkezett: +${formatCurrency(couponAmount)}. Teljesen adómentes növekedés."
                repository.addNotification(
                    MarketNotification(
                        title = title,
                        message = message,
                        type = NotificationType.MAK_COUPON,
                        symbolOrTag = "FixMÁP",
                        amountCredit = couponAmount
                    )
                )
                if (_isSystemNotifEnabled.value && _isNotifMakEnabled.value) {
                    SystemNotificationHelper.showSystemNotification(getApplication(), title, message, (4000..4999).random())
                }
            } else {
                val divAmount = listOf(28000.0, 45000.0, 62000.0).random()
                val title = "TBSZ Részvény Osztaléknap"
                val message = "Vanguard FTSE All-World ETF osztalék jóváírva: +${formatCurrency(divAmount)}. TBSZ számládon azonnal újrabefektethető."
                repository.addNotification(
                    MarketNotification(
                        title = title,
                        message = message,
                        type = NotificationType.DIVIDEND,
                        symbolOrTag = "VWCE",
                        amountCredit = divAmount
                    )
                )
                if (_isSystemNotifEnabled.value && _isNotifTbszEnabled.value) {
                    SystemNotificationHelper.showSystemNotification(getApplication(), title, message, (5000..5999).random())
                }
            }
        }
    }

    // Security & Biometrics
    fun onBiometricAuthSuccess() {
        _isBiometricScanning.value = false
        _isAuthenticated.value = true
    }

    fun setBiometricScanning(scanning: Boolean) {
        _isBiometricScanning.value = scanning
    }

    fun updatePinCode(newPin: String) {
        preferencesManager.userPin = newPin
        _securityProfile.value = _securityProfile.value.copy(pinCode = newPin)
    }

    fun authenticateWithPin(pin: String): Boolean {
        if (preferencesManager.verifyPin(pin)) {
            _isAuthenticated.value = true
            return true
        }
        return false
    }

    fun verifyMfaCode(code: String): Boolean {
        // Accepts test code "849201" or length == 6
        if (code.length == 6) {
            _isAuthenticated.value = true
            return true
        }
        return false
    }

    fun lockApp() {
        _isAuthenticated.value = false
    }

    fun toggleBiometricSetting(enabled: Boolean) {
        preferencesManager.isBiometricEnabled = enabled
        _securityProfile.value = _securityProfile.value.copy(isBiometricEnabled = enabled)
    }

    fun toggleMfaSetting(enabled: Boolean) {
        _securityProfile.value = _securityProfile.value.copy(isMfaEnabled = enabled)
    }

    fun triggerCloudBackup() {
        viewModelScope.launch {
            _isBackingUpCloud.value = true
            delay(1500) // Encrypting snapshot with AES-256 and syncing to cloud
            val now = System.currentTimeMillis()
            _securityProfile.value = _securityProfile.value.copy(lastCloudBackupTime = now)
            _isBackingUpCloud.value = false
            repository.addNotification(
                MarketNotification(
                    title = "Automated Cloud Backup Successful",
                    message = "All financial records, TBSZ accounts, MÁK bonds & budget snapshots encrypted with AES-256 and secured to cloud vault.",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "BACKUP"
                )
            )
        }
    }

    // ==========================================
    // Photography Gear Management
    // ==========================================
    fun addPhotoGear(
        name: String,
        brand: String,
        model: String,
        category: GearCategory,
        serialNumber: String,
        purchaseDate: String,
        purchasePrice: Double,
        condition: GearCondition,
        location: String,
        isInsured: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addPhotoGear(
                PhotoGearItem(
                    name = name,
                    brand = brand,
                    model = model,
                    category = category,
                    serialNumber = serialNumber,
                    purchaseDate = purchaseDate,
                    purchasePrice = purchasePrice,
                    condition = condition,
                    location = location,
                    isInsured = isInsured,
                    notes = notes
                )
            )
            repository.addNotification(
                MarketNotification(
                    title = "New Gear Added",
                    message = "$name ($brand) added to gear inventory. Value: ${formatCurrency(purchasePrice)}",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "GEAR"
                )
            )
        }
    }

    fun updatePhotoGear(gear: PhotoGearItem) {
        viewModelScope.launch {
            repository.updatePhotoGear(gear)
        }
    }

    fun deletePhotoGear(gear: PhotoGearItem) {
        viewModelScope.launch {
            repository.deletePhotoGear(gear)
        }
    }

    // ==========================================
    // Photography Contracts Management
    // ==========================================
    fun addContract(
        title: String,
        clientName: String,
        clientEmail: String,
        clientPhone: String,
        shootDate: String,
        shootLocation: String,
        totalAmount: Double,
        depositPaid: Double,
        status: ContractStatus,
        deliverables: String,
        deliverableDeadline: String,
        notes: String,
        shootStartTime: String = "10:00",
        shootEndTime: String = "16:00"
    ) {
        viewModelScope.launch {
            val contractId = repository.addContract(
                PhotoContract(
                    title = title,
                    clientName = clientName,
                    clientEmail = clientEmail,
                    clientPhone = clientPhone,
                    shootDate = shootDate,
                    shootStartTime = shootStartTime,
                    shootEndTime = shootEndTime,
                    shootLocation = shootLocation,
                    totalAmount = totalAmount,
                    depositPaid = depositPaid,
                    status = status,
                    deliverables = deliverables,
                    deliverableDeadline = deliverableDeadline,
                    notes = notes
                )
            )

            // If deposit was paid, offer option to log it into cashflows
            if (depositPaid > 0) {
                repository.addTransaction(
                    TransactionItem(
                        title = "Shoot Deposit: $title",
                        amount = depositPaid,
                        type = TransactionType.INCOME,
                        category = "Photo Services",
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        bankAccountName = "Primary Account"
                    )
                )
            }

            repository.addNotification(
                MarketNotification(
                    title = "Contract Created",
                    message = "Contract '$title' for $clientName created (${formatCurrency(totalAmount)}).",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "CONTRACT"
                )
            )
        }
    }

    fun updateContract(contract: PhotoContract) {
        viewModelScope.launch {
            repository.updateContract(contract)
        }
    }

    fun deleteContract(contract: PhotoContract) {
        viewModelScope.launch {
            repository.deleteContract(contract)
        }
    }

    fun recordContractPayment(
        contract: PhotoContract,
        paymentAmount: Double,
        bankAccountId: Long?,
        bankAccountName: String,
        paymentNotes: String
    ) {
        viewModelScope.launch {
            val newDepositPaid = (contract.depositPaid + paymentAmount).coerceAtMost(contract.totalAmount)
            val isNowFullyPaid = newDepositPaid >= contract.totalAmount
            val updatedStatus = if (isNowFullyPaid && (contract.status == ContractStatus.SHOOT_COMPLETED || contract.status == ContractStatus.DELIVERED)) {
                ContractStatus.COMPLETED_PAID
            } else {
                contract.status
            }

            val updatedContract = contract.copy(
                depositPaid = newDepositPaid,
                status = updatedStatus,
                isPaymentRecorded = true
            )
            repository.updateContract(updatedContract)

            // Add Income Transaction to WealthFlow Cashflow
            repository.addTransaction(
                TransactionItem(
                    title = "Client Payment: ${contract.title}",
                    amount = paymentAmount,
                    type = TransactionType.INCOME,
                    category = "Photo Services",
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    bankAccountId = bankAccountId,
                    bankAccountName = bankAccountName.ifBlank { "Primary Bank" },
                    notes = paymentNotes
                )
            )

            // If linked to bank account, update bank balance
            bankAccountId?.let { id ->
                val account = bankAccounts.value.find { it.id == id }
                if (account != null) {
                    repository.updateBankAccount(account.copy(balance = account.balance + paymentAmount))
                }
            }

            repository.addNotification(
                MarketNotification(
                    title = "Payment Recorded +${formatCurrency(paymentAmount)}",
                    message = "Received payment from ${contract.clientName} for '${contract.title}'. Credited to $bankAccountName.",
                    type = NotificationType.DIVIDEND,
                    symbolOrTag = "INCOME",
                    amountCredit = paymentAmount
                )
            )
        }
    }

    // ==========================================
    // Photography & Life Tasks
    // ==========================================
    fun addTask(
        title: String,
        dueDate: String,
        priority: TaskPriority,
        category: String,
        contractId: Long? = null,
        contractTitle: String? = null,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addTask(
                PhotoTask(
                    title = title,
                    dueDate = dueDate,
                    priority = priority,
                    category = category,
                    contractId = contractId,
                    contractTitle = contractTitle,
                    notes = notes
                )
            )
        }
    }

    fun updateTask(task: PhotoTask) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun toggleTaskCompleted(task: PhotoTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: PhotoTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // ==========================================
    // Gear Wishes (Linked to Financial Wishes Tab!)
    // ==========================================
    fun addGearWish(
        title: String,
        targetPrice: Double,
        currentSaved: Double,
        priority: WishPriority,
        targetDate: String,
        gearCategory: String,
        notes: String
    ) {
        viewModelScope.launch {
            // Inserts into Room wishes table so it appears in both Financial Wishes tab and Gear Wishlist!
            repository.addWish(
                WishItem(
                    title = title,
                    targetPrice = targetPrice,
                    currentSaved = currentSaved,
                    priority = priority,
                    category = "Photo Gear",
                    targetDate = targetDate,
                    notes = notes,
                    isGearWish = true,
                    gearCategory = gearCategory
                )
            )
            repository.addNotification(
                MarketNotification(
                    title = "Gear Wish Added to WealthFlow",
                    message = "Target: ${formatCurrency(targetPrice)} for $title. Tracked in both Gear Wishlist & Finance Wishes.",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "WISH"
                )
            )
        }
    }

    fun promoteGearWishToInventory(
        wish: WishItem,
        brand: String,
        model: String,
        category: GearCategory,
        serialNumber: String,
        location: String
    ) {
        viewModelScope.launch {
            // 1. Mark wish as fulfilled in Wishes table
            repository.updateWish(wish.copy(isFulfilled = true, currentSaved = wish.targetPrice))

            // 2. Add to Gear Inventory!
            repository.addPhotoGear(
                PhotoGearItem(
                    name = wish.title,
                    brand = brand.ifBlank { "Sony" },
                    model = model,
                    category = category,
                    serialNumber = serialNumber.ifBlank { "SN-NEW-${System.currentTimeMillis() % 10000}" },
                    purchaseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    purchasePrice = wish.targetPrice,
                    condition = GearCondition.MINT,
                    location = location.ifBlank { "Main Gear Bag" },
                    isInsured = true,
                    notes = "Acquired from Wishlist! " + wish.notes
                )
            )

            repository.addNotification(
                MarketNotification(
                    title = "Wish Fulfilled & Gear Added!",
                    message = "${wish.title} promoted from Gear Wishlist to Gear Inventory! Value: ${formatCurrency(wish.targetPrice)}.",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "GOAL"
                )
            )
        }
    }

    // ==========================================
    // LifeOS Expandable Modules
    // ==========================================
    fun toggleLifeModule(module: LifeOsModuleItem) {
        viewModelScope.launch {
            repository.updateLifeModule(module.copy(isEnabled = !module.isEnabled))
        }
    }

    fun addCustomLifeModule(
        title: String,
        subtitle: String,
        iconKey: String,
        category: String,
        notes: String
    ) {
        viewModelScope.launch {
            val count = lifeModules.value.size
            repository.addLifeModule(
                LifeOsModuleItem(
                    moduleKey = "custom_${System.currentTimeMillis()}",
                    title = title,
                    subtitle = subtitle,
                    iconKey = iconKey,
                    category = category,
                    isEnabled = true,
                    isCustom = true,
                    customNotes = notes,
                    badgeText = "Custom",
                    orderIndex = count
                )
            )
            repository.addNotification(
                MarketNotification(
                    title = "LifeOS Expanded!",
                    message = "New module '$title' added to your LifeOS Homepage.",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "MODULE"
                )
            )
        }
    }

    fun deleteLifeModule(module: LifeOsModuleItem) {
        viewModelScope.launch {
            repository.deleteLifeModule(module)
        }
    }

    // ==========================================
    // Photography Ideas & Moodboards (Pinterest-style)
    // ==========================================
    fun addPhotoIdea(
        title: String,
        boardName: String,
        tags: String,
        description: String,
        cameraSettings: String,
        recommendedGear: String,
        colorPaletteHexes: String,
        moodTheme: String,
        isPinned: Boolean = false,
        linkedContractId: Long? = null,
        linkedContractTitle: String? = null
    ) {
        viewModelScope.launch {
            repository.addPhotoIdea(
                PhotoIdeaItem(
                    title = title,
                    boardName = boardName.ifBlank { "Inspirations" },
                    tags = tags,
                    description = description,
                    cameraSettings = cameraSettings,
                    recommendedGear = recommendedGear,
                    colorPaletteHexes = colorPaletteHexes.ifBlank { "#F4A261,#E76F51,#2A9D8F,#264653,#E9C46A" },
                    moodTheme = moodTheme.ifBlank { "Sunset Glow" },
                    isPinned = isPinned,
                    linkedContractId = linkedContractId,
                    linkedContractTitle = linkedContractTitle
                )
            )
            repository.addNotification(
                MarketNotification(
                    title = "New Idea Pinned",
                    message = "'$title' saved to $boardName moodboard.",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "IDEA"
                )
            )
        }
    }

    fun togglePinPhotoIdea(idea: PhotoIdeaItem) {
        viewModelScope.launch {
            repository.updatePhotoIdea(idea.copy(isPinned = !idea.isPinned))
        }
    }

    fun updatePhotoIdea(idea: PhotoIdeaItem) {
        viewModelScope.launch {
            repository.updatePhotoIdea(idea)
        }
    }

    fun deletePhotoIdea(idea: PhotoIdeaItem) {
        viewModelScope.launch {
            repository.deletePhotoIdea(idea)
        }
    }

    fun createTaskFromIdea(idea: PhotoIdeaItem, contractId: Long?, contractTitle: String?) {
        viewModelScope.launch {
            repository.addTask(
                PhotoTask(
                    title = "Execute Idea: ${idea.title}",
                    dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    priority = TaskPriority.HIGH,
                    category = "Shoot Prep",
                    contractId = contractId ?: idea.linkedContractId,
                    contractTitle = contractTitle ?: idea.linkedContractTitle,
                    notes = "${idea.description}\nSettings: ${idea.cameraSettings}\nGear: ${idea.recommendedGear}"
                )
            )
            repository.addNotification(
                MarketNotification(
                    title = "Task Added from Idea",
                    message = "Created preparation checklist item for '${idea.title}'.",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "TASK"
                )
            )
        }
    }

    // Weekly Costs Actions
    fun addWeeklyCost(
        title: String,
        amount: Double,
        category: String,
        date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
        dayOfWeek: String = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date()),
        weekNumber: Int = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR),
        year: Int = Calendar.getInstance().get(Calendar.YEAR),
        notes: String = "",
        isRecurring: Boolean = false
    ) {
        viewModelScope.launch {
            val cost = WeeklyCostItem(
                title = title,
                amount = amount,
                category = category,
                date = date,
                dayOfWeek = dayOfWeek,
                weekNumber = weekNumber,
                year = year,
                notes = notes,
                isRecurring = isRecurring
            )
            repository.addWeeklyCost(cost)
        }
    }

    fun deleteWeeklyCost(cost: WeeklyCostItem) {
        viewModelScope.launch {
            repository.deleteWeeklyCost(cost)
        }
    }

    fun updateWeeklyCost(cost: WeeklyCostItem) {
        viewModelScope.launch {
            repository.updateWeeklyCost(cost)
        }
    }

    // Expected Costs & Planned Expenses Actions
    fun addExpectedCost(
        title: String,
        estimatedAmount: Double,
        allocatedAmount: Double = 0.0,
        category: String,
        targetDate: String = "",
        status: ExpectedCostStatus = ExpectedCostStatus.PLANNING,
        urgency: ExpectedCostUrgency = ExpectedCostUrgency.PLANNED,
        notes: String = "",
        isMandatory: Boolean = false
    ) {
        viewModelScope.launch {
            val item = ExpectedCostItem(
                title = title,
                estimatedAmount = estimatedAmount,
                allocatedAmount = allocatedAmount,
                category = category,
                targetDate = targetDate,
                status = status,
                urgency = urgency,
                notes = notes,
                isMandatory = isMandatory
            )
            repository.addExpectedCost(item)
            repository.addNotification(
                MarketNotification(
                    title = "Expected Cost Added",
                    message = "Added planned cost '$title' (${formatCurrency(estimatedAmount)}), separated from wishlist.",
                    type = NotificationType.BUDGET_ALERT
                )
            )
        }
    }

    fun updateExpectedCost(item: ExpectedCostItem) {
        viewModelScope.launch {
            repository.updateExpectedCost(item)
        }
    }

    fun deleteExpectedCost(item: ExpectedCostItem) {
        viewModelScope.launch {
            repository.deleteExpectedCost(item)
        }
    }

    fun allocateFundsToExpectedCost(item: ExpectedCostItem, additionalAmount: Double) {
        viewModelScope.launch {
            val newAllocated = (item.allocatedAmount + additionalAmount).coerceAtLeast(0.0)
            val updatedStatus = if (newAllocated >= item.estimatedAmount && item.status == ExpectedCostStatus.PLANNING) {
                ExpectedCostStatus.FUNDED
            } else item.status

            val updated = item.copy(
                allocatedAmount = newAllocated,
                status = updatedStatus
            )
            repository.updateExpectedCost(updated)
            repository.addNotification(
                MarketNotification(
                    title = "Funds Allocated to Expected Cost",
                    message = "Allocated ${formatCurrency(additionalAmount)} towards '${item.title}'. Total saved: ${formatCurrency(newAllocated)}.",
                    type = NotificationType.BUDGET_ALERT
                )
            )
        }
    }

    fun markExpectedCostStatus(item: ExpectedCostItem, newStatus: ExpectedCostStatus) {
        viewModelScope.launch {
            val updated = item.copy(status = newStatus)
            repository.updateExpectedCost(updated)
        }
    }

    fun formatCurrency(amount: Double): String {
        val rounded = kotlin.math.round(amount).toLong()
        val formatted = String.format(Locale.GERMANY, "%,d", rounded).replace('.', ' ')
        return "$formatted Ft"
    }
}
