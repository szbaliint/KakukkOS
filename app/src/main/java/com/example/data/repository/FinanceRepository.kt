package com.example.data.repository

import com.example.data.local.FinanceDao
import com.example.data.model.BankAccount
import com.example.data.model.BudgetPlan
import com.example.data.model.InvestmentAsset
import com.example.data.model.MarketNotification
import com.example.data.model.NotificationType
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.data.model.WishItem
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceRepository(private val dao: FinanceDao) {

    // Wishes
    val allWishes: Flow<List<WishItem>> = dao.getAllWishes()
    suspend fun addWish(wish: WishItem) = dao.insertWish(wish)
    suspend fun updateWish(wish: WishItem) = dao.updateWish(wish)
    suspend fun deleteWish(wish: WishItem) = dao.deleteWish(wish)

    // Transactions
    val allTransactions: Flow<List<TransactionItem>> = dao.getAllTransactions()
    fun getTransactionsForMonth(month: String): Flow<List<TransactionItem>> =
        dao.getTransactionsForMonth(month)
    suspend fun addTransaction(transaction: TransactionItem) = dao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionItem) = dao.deleteTransaction(transaction)

    // Budgets
    val allBudgets: Flow<List<BudgetPlan>> = dao.getAllBudgets()
    suspend fun addBudget(budget: BudgetPlan) = dao.insertBudget(budget)
    suspend fun updateBudget(budget: BudgetPlan) = dao.updateBudget(budget)
    suspend fun deleteBudget(budget: BudgetPlan) = dao.deleteBudget(budget)

    // Bank Accounts
    val allBankAccounts: Flow<List<BankAccount>> = dao.getAllBankAccounts()
    suspend fun addBankAccount(account: BankAccount) = dao.insertBankAccount(account)
    suspend fun updateBankAccount(account: BankAccount) = dao.updateBankAccount(account)
    suspend fun deleteBankAccount(account: BankAccount) = dao.deleteBankAccount(account)

    // Investments
    val allInvestments: Flow<List<InvestmentAsset>> = dao.getAllInvestments()
    suspend fun addInvestment(asset: InvestmentAsset) = dao.insertInvestment(asset)
    suspend fun updateInvestment(asset: InvestmentAsset) = dao.updateInvestment(asset)
    suspend fun deleteInvestment(asset: InvestmentAsset) = dao.deleteInvestment(asset)

    // Notifications
    val allNotifications: Flow<List<MarketNotification>> = dao.getAllNotifications()
    val unreadCount: Flow<Int> = dao.getUnreadNotificationsCount()
    suspend fun addNotification(notification: MarketNotification) = dao.insertNotification(notification)
    suspend fun markAllAsRead() = dao.markAllNotificationsAsRead()
    suspend fun deleteNotification(id: Long) = dao.deleteNotificationById(id)

    // Photography Gear
    val allPhotoGear: Flow<List<com.example.data.model.PhotoGearItem>> = dao.getAllPhotoGear()
    suspend fun addPhotoGear(gear: com.example.data.model.PhotoGearItem) = dao.insertPhotoGear(gear)
    suspend fun updatePhotoGear(gear: com.example.data.model.PhotoGearItem) = dao.updatePhotoGear(gear)
    suspend fun deletePhotoGear(gear: com.example.data.model.PhotoGearItem) = dao.deletePhotoGear(gear)

    // Photography Contracts
    val allContracts: Flow<List<com.example.data.model.PhotoContract>> = dao.getAllContracts()
    fun getContractById(contractId: Long): Flow<com.example.data.model.PhotoContract?> = dao.getContractById(contractId)
    suspend fun addContract(contract: com.example.data.model.PhotoContract) = dao.insertContract(contract)
    suspend fun updateContract(contract: com.example.data.model.PhotoContract) = dao.updateContract(contract)
    suspend fun deleteContract(contract: com.example.data.model.PhotoContract) = dao.deleteContract(contract)

    // Photo & Life Tasks
    val allTasks: Flow<List<com.example.data.model.PhotoTask>> = dao.getAllTasks()
    fun getTasksForContract(contractId: Long): Flow<List<com.example.data.model.PhotoTask>> = dao.getTasksForContract(contractId)
    suspend fun addTask(task: com.example.data.model.PhotoTask) = dao.insertTask(task)
    suspend fun updateTask(task: com.example.data.model.PhotoTask) = dao.updateTask(task)
    suspend fun deleteTask(task: com.example.data.model.PhotoTask) = dao.deleteTask(task)

    // LifeOS Expandable Modules
    val allLifeModules: Flow<List<com.example.data.model.LifeOsModuleItem>> = dao.getAllLifeModules()
    suspend fun addLifeModule(module: com.example.data.model.LifeOsModuleItem) = dao.insertLifeModule(module)
    suspend fun updateLifeModule(module: com.example.data.model.LifeOsModuleItem) = dao.updateLifeModule(module)
    suspend fun deleteLifeModule(module: com.example.data.model.LifeOsModuleItem) = dao.deleteLifeModule(module)

    // Photography Ideas & Moodboards
    val allPhotoIdeas: Flow<List<com.example.data.model.PhotoIdeaItem>> = dao.getAllPhotoIdeas()
    fun getIdeasByBoard(board: String): Flow<List<com.example.data.model.PhotoIdeaItem>> = dao.getIdeasByBoard(board)
    suspend fun addPhotoIdea(idea: com.example.data.model.PhotoIdeaItem) = dao.insertPhotoIdea(idea)
    suspend fun updatePhotoIdea(idea: com.example.data.model.PhotoIdeaItem) = dao.updatePhotoIdea(idea)
    suspend fun deletePhotoIdea(idea: com.example.data.model.PhotoIdeaItem) = dao.deletePhotoIdea(idea)

    // Weekly Costs & Sum Operations
    val allWeeklyCosts: Flow<List<com.example.data.model.WeeklyCostItem>> = dao.getAllWeeklyCosts()
    fun getWeeklyCostsByWeek(weekNumber: Int, year: Int): Flow<List<com.example.data.model.WeeklyCostItem>> =
        dao.getWeeklyCostsByWeek(weekNumber, year)
    suspend fun addWeeklyCost(cost: com.example.data.model.WeeklyCostItem) = dao.insertWeeklyCost(cost)
    suspend fun updateWeeklyCost(cost: com.example.data.model.WeeklyCostItem) = dao.updateWeeklyCost(cost)
    suspend fun deleteWeeklyCost(cost: com.example.data.model.WeeklyCostItem) = dao.deleteWeeklyCost(cost)
    suspend fun deleteWeeklyCostById(id: Long) = dao.deleteWeeklyCostById(id)

    // Expected Costs & Planned Expenses
    val allExpectedCosts: Flow<List<com.example.data.model.ExpectedCostItem>> = dao.getAllExpectedCosts()
    suspend fun addExpectedCost(item: com.example.data.model.ExpectedCostItem) = dao.insertExpectedCost(item)
    suspend fun updateExpectedCost(item: com.example.data.model.ExpectedCostItem) = dao.updateExpectedCost(item)
    suspend fun deleteExpectedCost(item: com.example.data.model.ExpectedCostItem) = dao.deleteExpectedCost(item)
    suspend fun deleteExpectedCostById(id: Long) = dao.deleteExpectedCostById(id)
}
