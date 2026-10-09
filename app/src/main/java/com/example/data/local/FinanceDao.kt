package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BankAccount
import com.example.data.model.BudgetPlan
import com.example.data.model.ExpectedCostItem
import com.example.data.model.InvestmentAsset
import com.example.data.model.MarketNotification
import com.example.data.model.TransactionItem
import com.example.data.model.WeeklyCostItem
import com.example.data.model.WishItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    // Wishes
    @Query("SELECT * FROM wishes ORDER BY isFulfilled ASC, priority DESC, targetPrice DESC")
    fun getAllWishes(): Flow<List<WishItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWish(wish: WishItem): Long

    @Update
    suspend fun updateWish(wish: WishItem)

    @Delete
    suspend fun deleteWish(wish: WishItem)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionItem>>

    @Query("SELECT * FROM transactions WHERE date LIKE :monthPrefix || '%' ORDER BY timestamp DESC")
    fun getTransactionsForMonth(monthPrefix: String): Flow<List<TransactionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionItem): Long

    @Delete
    suspend fun deleteTransaction(transaction: TransactionItem)

    // Budgets
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetPlan): Long

    @Update
    suspend fun updateBudget(budget: BudgetPlan)

    @Delete
    suspend fun deleteBudget(budget: BudgetPlan)

    // Bank Accounts
    @Query("SELECT * FROM bank_accounts ORDER BY balance DESC")
    fun getAllBankAccounts(): Flow<List<BankAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccount): Long

    @Update
    suspend fun updateBankAccount(account: BankAccount)

    @Delete
    suspend fun deleteBankAccount(account: BankAccount)

    // Investments
    @Query("SELECT * FROM investments ORDER BY currentEstimatedValue DESC")
    fun getAllInvestments(): Flow<List<InvestmentAsset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(asset: InvestmentAsset): Long

    @Update
    suspend fun updateInvestment(asset: InvestmentAsset)

    @Delete
    suspend fun deleteInvestment(asset: InvestmentAsset)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<MarketNotification>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: MarketNotification): Long

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    // Photography Gear
    @Query("SELECT * FROM photo_gear ORDER BY category ASC, purchasePrice DESC")
    fun getAllPhotoGear(): Flow<List<com.example.data.model.PhotoGearItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoGear(gear: com.example.data.model.PhotoGearItem): Long

    @Update
    suspend fun updatePhotoGear(gear: com.example.data.model.PhotoGearItem)

    @Delete
    suspend fun deletePhotoGear(gear: com.example.data.model.PhotoGearItem)

    // Photography Contracts
    @Query("SELECT * FROM photo_contracts ORDER BY createdAt DESC")
    fun getAllContracts(): Flow<List<com.example.data.model.PhotoContract>>

    @Query("SELECT * FROM photo_contracts WHERE id = :contractId LIMIT 1")
    fun getContractById(contractId: Long): Flow<com.example.data.model.PhotoContract?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContract(contract: com.example.data.model.PhotoContract): Long

    @Update
    suspend fun updateContract(contract: com.example.data.model.PhotoContract)

    @Delete
    suspend fun deleteContract(contract: com.example.data.model.PhotoContract)

    // Photography & Life Tasks
    @Query("SELECT * FROM photo_tasks ORDER BY isCompleted ASC, priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<com.example.data.model.PhotoTask>>

    @Query("SELECT * FROM photo_tasks WHERE contractId = :contractId ORDER BY isCompleted ASC, createdAt DESC")
    fun getTasksForContract(contractId: Long): Flow<List<com.example.data.model.PhotoTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: com.example.data.model.PhotoTask): Long

    @Update
    suspend fun updateTask(task: com.example.data.model.PhotoTask)

    @Delete
    suspend fun deleteTask(task: com.example.data.model.PhotoTask)

    // Expandable LifeOS Modules
    @Query("SELECT * FROM life_modules ORDER BY orderIndex ASC")
    fun getAllLifeModules(): Flow<List<com.example.data.model.LifeOsModuleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLifeModule(module: com.example.data.model.LifeOsModuleItem): Long

    @Update
    suspend fun updateLifeModule(module: com.example.data.model.LifeOsModuleItem)

    @Delete
    suspend fun deleteLifeModule(module: com.example.data.model.LifeOsModuleItem)

    // Photography Ideas & Moodboards (Pinterest Style)
    @Query("SELECT * FROM photo_ideas ORDER BY isPinned DESC, createdAt DESC")
    fun getAllPhotoIdeas(): Flow<List<com.example.data.model.PhotoIdeaItem>>

    @Query("SELECT * FROM photo_ideas WHERE boardName = :board ORDER BY isPinned DESC, createdAt DESC")
    fun getIdeasByBoard(board: String): Flow<List<com.example.data.model.PhotoIdeaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoIdea(idea: com.example.data.model.PhotoIdeaItem): Long

    @Update
    suspend fun updatePhotoIdea(idea: com.example.data.model.PhotoIdeaItem)

    @Delete
    suspend fun deletePhotoIdea(idea: com.example.data.model.PhotoIdeaItem)

    // Weekly Costs & Sum Operations
    @Query("SELECT * FROM weekly_costs ORDER BY date DESC, id DESC")
    fun getAllWeeklyCosts(): Flow<List<WeeklyCostItem>>

    @Query("SELECT * FROM weekly_costs WHERE weekNumber = :weekNumber AND year = :year ORDER BY date DESC, id DESC")
    fun getWeeklyCostsByWeek(weekNumber: Int, year: Int): Flow<List<WeeklyCostItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyCost(cost: WeeklyCostItem): Long

    @Update
    suspend fun updateWeeklyCost(cost: WeeklyCostItem)

    @Delete
    suspend fun deleteWeeklyCost(cost: WeeklyCostItem)

    @Query("DELETE FROM weekly_costs WHERE id = :id")
    suspend fun deleteWeeklyCostById(id: Long)

    // Expected Costs & Planned Expenses (Distinguished from Wishlist)
    @Query("SELECT * FROM expected_costs ORDER BY status ASC, isMandatory DESC, id DESC")
    fun getAllExpectedCosts(): Flow<List<ExpectedCostItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpectedCost(item: ExpectedCostItem): Long

    @Update
    suspend fun updateExpectedCost(item: ExpectedCostItem)

    @Delete
    suspend fun deleteExpectedCost(item: ExpectedCostItem)

    @Query("DELETE FROM expected_costs WHERE id = :id")
    suspend fun deleteExpectedCostById(id: Long)
}
