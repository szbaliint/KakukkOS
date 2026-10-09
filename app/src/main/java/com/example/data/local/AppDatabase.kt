package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WishItem::class,
        TransactionItem::class,
        BudgetPlan::class,
        BankAccount::class,
        InvestmentAsset::class,
        MarketNotification::class,
        PhotoGearItem::class,
        PhotoContract::class,
        PhotoTask::class,
        PhotoIdeaItem::class,
        LifeOsModuleItem::class,
        WeeklyCostItem::class,
        ExpectedCostItem::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fortis_wealth_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.financeDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: FinanceDao) {
            // Initial Wishes (Willings with price)
            dao.insertWish(
                WishItem(
                    title = "MacBook Pro M4 Max",
                    targetPrice = 1450000.0,
                    currentSaved = 980000.0,
                    priority = WishPriority.HIGH,
                    category = "Work & Tech",
                    targetDate = "2026-12-15",
                    notes = "16-inch 48GB Unified RAM for developer productivity"
                )
            )
            dao.insertWish(
                WishItem(
                    title = "Japan Sakura Exploration Tour",
                    targetPrice = 2200000.0,
                    currentSaved = 1350000.0,
                    priority = WishPriority.MEDIUM,
                    category = "Travel",
                    targetDate = "2027-04-01",
                    notes = "Tokyo, Kyoto, Osaka & Hakone onsen stay for 2 weeks"
                )
            )
            dao.insertWish(
                WishItem(
                    title = "Emergency Living Fund (6 Months)",
                    targetPrice = 3000000.0,
                    currentSaved = 2650000.0,
                    priority = WishPriority.CRITICAL,
                    category = "Safety Buffer",
                    targetDate = "2026-10-31",
                    notes = "Liquid reserve parked in DKJ & high yield deposit"
                )
            )
            dao.insertWish(
                WishItem(
                    title = "Home Office Ergonomic Setup",
                    targetPrice = 420000.0,
                    currentSaved = 420000.0,
                    priority = WishPriority.LOW,
                    category = "Home",
                    targetDate = "2026-08-20",
                    notes = "Herman Miller Aeron & motorized standing desk",
                    isFulfilled = true
                )
            )

            // Bank Accounts
            val otpId = dao.insertBankAccount(
                BankAccount(
                    institutionName = "OTP Bank",
                    accountHolder = "Balint Szabo",
                    accountNumberMasked = "•••• 4419",
                    balance = 1420500.0,
                    currency = "HUF",
                    type = BankAccountType.CHECKING,
                    connectionStatus = "Primary Checking"
                )
            )
            val makId = dao.insertBankAccount(
                BankAccount(
                    institutionName = "Magyar Államkincstár (MÁK)",
                    accountHolder = "Balint Szabo",
                    accountNumberMasked = "•••• 9081",
                    balance = 12850000.0,
                    currency = "HUF",
                    type = BankAccountType.TREASURY_MAK,
                    connectionStatus = "WebKincstár Account"
                )
            )
            dao.insertBankAccount(
                BankAccount(
                    institutionName = "Erste TBSZ Értékpapírszámla",
                    accountHolder = "Balint Szabo",
                    accountNumberMasked = "•••• 3120",
                    balance = 6840000.0,
                    currency = "HUF",
                    type = BankAccountType.INVESTMENT,
                    connectionStatus = "Securities Custody"
                )
            )
            dao.insertBankAccount(
                BankAccount(
                    institutionName = "Revolut Premium",
                    accountHolder = "Balint Szabo",
                    accountNumberMasked = "•••• 7721",
                    balance = 890000.0,
                    currency = "HUF",
                    type = BankAccountType.CHECKING,
                    connectionStatus = "Daily Spending"
                )
            )

            // Initial Investments (TBSZ & Magyar Államkincstár MÁK bonds)
            dao.insertInvestment(
                InvestmentAsset(
                    name = "PMÁP 2032/I (Inflációkövető)",
                    category = AssetCategory.MAK_BOND,
                    makType = MakBondType.PMAP,
                    principalAmount = 5000000.0,
                    currentEstimatedValue = 5485000.0,
                    annualYieldPercent = 7.90,
                    accruedInterest = 295000.0,
                    purchaseDate = "2024-03-15",
                    maturityDate = "2032-04-20",
                    nextCouponDate = "2027-04-20",
                    notes = "Hungarian retail inflation linked bond (100% Tax Free)"
                )
            )
            dao.insertInvestment(
                InvestmentAsset(
                    name = "FixMÁP 2029/Q1 (7.00% Fix Kamat)",
                    category = AssetCategory.MAK_BOND,
                    makType = MakBondType.FIXMAP,
                    principalAmount = 4000000.0,
                    currentEstimatedValue = 4210000.0,
                    annualYieldPercent = 7.00,
                    accruedInterest = 140000.0,
                    purchaseDate = "2025-01-10",
                    maturityDate = "2029-01-22",
                    nextCouponDate = "2027-01-22",
                    notes = "Guaranteed 7.00% annual coupon paid quarterly"
                )
            )
            dao.insertInvestment(
                InvestmentAsset(
                    name = "BMÁP 2027/N (3M DKJ + Bónusz)",
                    category = AssetCategory.MAK_BOND,
                    makType = MakBondType.BMAP,
                    principalAmount = 2500000.0,
                    currentEstimatedValue = 2625000.0,
                    annualYieldPercent = 6.45,
                    accruedInterest = 82000.0,
                    purchaseDate = "2024-11-05",
                    maturityDate = "2027-05-25",
                    nextCouponDate = "2026-11-25",
                    notes = "Tied to 3-month Discount Treasury Bill yield"
                )
            )
            dao.insertInvestment(
                InvestmentAsset(
                    name = "S&P 500 Core ETF (TBSZ 2023)",
                    category = AssetCategory.TBSZ_PORTFOLIO,
                    principalAmount = 3500000.0,
                    currentEstimatedValue = 4480000.0,
                    annualYieldPercent = 11.2,
                    purchaseDate = "2023-02-10",
                    maturityDate = "2028-12-31",
                    tbszOpeningYear = 2023,
                    notes = "3 years elapsed: already qualifies for reduced 10% tax, 0% after 2028!"
                )
            )
            dao.insertInvestment(
                InvestmentAsset(
                    name = "MSCI World Quality ETF (TBSZ 2024)",
                    category = AssetCategory.TBSZ_PORTFOLIO,
                    principalAmount = 2000000.0,
                    currentEstimatedValue = 2360000.0,
                    annualYieldPercent = 9.8,
                    purchaseDate = "2024-04-12",
                    maturityDate = "2029-12-31",
                    tbszOpeningYear = 2024,
                    notes = "Accumulating ETF, sheltered from 15% SZJA & 13% Szocho"
                )
            )

            // Monthly Budgets
            dao.insertBudget(BudgetPlan(category = "Housing & Rent", monthlyLimit = 220000.0))
            dao.insertBudget(BudgetPlan(category = "Groceries & Supermarket", monthlyLimit = 150000.0))
            dao.insertBudget(BudgetPlan(category = "Dining & Leisure", monthlyLimit = 80000.0))
            dao.insertBudget(BudgetPlan(category = "Transport & Fuel", monthlyLimit = 55000.0))
            dao.insertBudget(BudgetPlan(category = "Subscriptions & Software", monthlyLimit = 35000.0))
            dao.insertBudget(BudgetPlan(category = "Health & Fitness", monthlyLimit = 40000.0))

            // Recent Transactions
            dao.insertTransaction(
                TransactionItem(
                    title = "Monthly Salary (Senior Software Dev)",
                    amount = 1450000.0,
                    type = TransactionType.INCOME,
                    category = "Salary",
                    date = "2026-09-05",
                    bankAccountId = otpId,
                    bankAccountName = "OTP Bank",
                    isRecurring = true
                )
            )
            dao.insertTransaction(
                TransactionItem(
                    title = "PMÁP 2032/I Annual Coupon Payout",
                    amount = 395000.0,
                    type = TransactionType.INCOME,
                    category = "Treasury Coupon",
                    date = "2026-09-12",
                    bankAccountId = makId,
                    bankAccountName = "Magyar Államkincstár",
                    isAutoSynced = true
                )
            )
            dao.insertTransaction(
                TransactionItem(
                    title = "Apartment Rent & Utility Fee",
                    amount = 210000.0,
                    type = TransactionType.EXPENSE,
                    category = "Housing & Rent",
                    date = "2026-09-08",
                    bankAccountId = otpId,
                    bankAccountName = "OTP Bank",
                    isRecurring = true
                )
            )
            dao.insertTransaction(
                TransactionItem(
                    title = "Spar & Aldi Weekly Groceries",
                    amount = 43200.0,
                    type = TransactionType.EXPENSE,
                    category = "Groceries & Supermarket",
                    date = "2026-09-18",
                    bankAccountName = "Revolut"
                )
            )
            dao.insertTransaction(
                TransactionItem(
                    title = "BKK Monthly Pass & Fuel",
                    amount = 28500.0,
                    type = TransactionType.EXPENSE,
                    category = "Transport & Fuel",
                    date = "2026-09-14",
                    bankAccountName = "OTP Bank"
                )
            )
            dao.insertTransaction(
                TransactionItem(
                    title = "Gourmet Dinner with Friends",
                    amount = 24800.0,
                    type = TransactionType.EXPENSE,
                    category = "Dining & Leisure",
                    date = "2026-09-22",
                    bankAccountName = "Revolut"
                )
            )

            // Real-Time Notifications
            dao.insertNotification(
                MarketNotification(
                    title = "MÁK Coupon Payout Credited!",
                    message = "PMÁP 2032/I interest payout of +395,000 Ft was automatically credited to your Magyar Államkincstár account. 100% tax-free!",
                    type = NotificationType.MAK_COUPON,
                    symbolOrTag = "PMÁP 2032/I",
                    amountCredit = 395000.0
                )
            )
            dao.insertNotification(
                MarketNotification(
                    title = "Market Fluctuation Alert: S&P 500",
                    message = "S&P 500 climbed +1.84% to a new record high. Your TBSZ 2023 portfolio gained approximately +82,000 Ft today.",
                    type = NotificationType.MARKET_FLUCTUATION,
                    symbolOrTag = "SPX",
                    changePercent = 1.84
                )
            )
            dao.insertNotification(
                MarketNotification(
                    title = "Dividend Payout: Apple Inc. (AAPL)",
                    message = "Quarterly dividend (+15 200 Ft) deposited into your brokerage sub-account.",
                    type = NotificationType.DIVIDEND,
                    symbolOrTag = "AAPL",
                    amountCredit = 15200.0
                )
            )
            dao.insertNotification(
                MarketNotification(
                    title = "Encrypted Cloud Backup Completed",
                    message = "Automated AES-256 cloud snapshot saved to LifeOS Private Vault. All budgets, wishes, gear & contracts synced.",
                    type = NotificationType.SECURITY_EVENT,
                    symbolOrTag = "CLOUD"
                )
            )

            // Initial Photography Gear Inventory
            val a7iv = dao.insertPhotoGear(
                PhotoGearItem(
                    name = "Sony Alpha 7 IV (A7M4)",
                    brand = "Sony",
                    model = "ILCE-7M4",
                    category = GearCategory.CAMERA_BODY,
                    serialNumber = "SN-3904812",
                    purchaseDate = "2023-04-10",
                    purchasePrice = 920000.0,
                    condition = GearCondition.MINT,
                    location = "Main Pelican 1510",
                    isInsured = true,
                    notes = "33MP Full Frame, primary wedding & portrait workhorse."
                )
            )
            val lens2470 = dao.insertPhotoGear(
                PhotoGearItem(
                    name = "Sony FE 24-70mm f/2.8 GM II",
                    brand = "Sony",
                    model = "SEL2470GM2",
                    category = GearCategory.LENS,
                    serialNumber = "SN-8820194",
                    purchaseDate = "2023-06-18",
                    purchasePrice = 880000.0,
                    condition = GearCondition.MINT,
                    location = "Main Pelican 1510",
                    isInsured = true,
                    notes = "G Master standard zoom, super sharp edge-to-edge."
                )
            )
            dao.insertPhotoGear(
                PhotoGearItem(
                    name = "Sony FE 85mm f/1.4 GM",
                    brand = "Sony",
                    model = "SEL85F14GM",
                    category = GearCategory.LENS,
                    serialNumber = "SN-1029482",
                    purchaseDate = "2023-08-02",
                    purchasePrice = 620000.0,
                    condition = GearCondition.EXCELLENT,
                    location = "Lens Pouch #2",
                    isInsured = true,
                    notes = "Creamy bokeh prime for intimate portraits and couple shoots."
                )
            )
            dao.insertPhotoGear(
                PhotoGearItem(
                    name = "Godox AD200 Pro TTL Strobe",
                    brand = "Godox",
                    model = "AD200Pro",
                    category = GearCategory.LIGHTING,
                    serialNumber = "SN-GD-99120",
                    purchaseDate = "2024-02-14",
                    purchasePrice = 145000.0,
                    condition = GearCondition.EXCELLENT,
                    location = "Lighting Kit Bag",
                    isInsured = true,
                    notes = "200Ws portable strobe with round head adapter and MagMod."
                )
            )
            dao.insertPhotoGear(
                PhotoGearItem(
                    name = "DJI RS 3 Pro Gimbal Combo",
                    brand = "DJI",
                    model = "RS3-PRO",
                    category = GearCategory.DRONE_GIMBAL,
                    serialNumber = "SN-DJ-44102",
                    purchaseDate = "2024-05-20",
                    purchasePrice = 360000.0,
                    condition = GearCondition.GOOD,
                    location = "Studio Shelf A",
                    isInsured = true,
                    notes = "Carbon fiber motorized 3-axis stabilizer for cinematic video."
                )
            )
            dao.insertPhotoGear(
                PhotoGearItem(
                    name = "Lexar Pro 128GB 2000x UHS-II SD (Pair)",
                    brand = "Lexar",
                    model = "V90 SDXC",
                    category = GearCategory.STORAGE,
                    serialNumber = "SN-SD-29910",
                    purchaseDate = "2024-01-11",
                    purchasePrice = 88000.0,
                    condition = GearCondition.MINT,
                    location = "Pelican Memory Wallet",
                    isInsured = false,
                    notes = "Dual slot redundancy, 300MB/s write speed for burst RAW."
                )
            )

            // Initial Photographer Contracts
            val weddingContractId = dao.insertContract(
                PhotoContract(
                    title = "Emma & David Wedding Photography",
                    clientName = "Emma Watson & David Kovacs",
                    clientEmail = "emma.kovacs.wed@gmail.com",
                    clientPhone = "+36 30 492 8819",
                    shootDate = "2026-10-18",
                    shootLocation = "Gresham Palace & Fisherman's Bastion, Budapest",
                    totalAmount = 520000.0,
                    depositPaid = 200000.0,
                    status = ContractStatus.SIGNED,
                    deliverables = "10 hours coverage, 550+ edited high-res images, wooden USB keepsake box, online password-protected web gallery for 1 year.",
                    deliverableDeadline = "2026-11-20",
                    notes = "Sunset ceremony at 16:30. Need second shooter for cocktail hour. Drone shots requested weather permitting."
                )
            )

            val lookbookContractId = dao.insertContract(
                PhotoContract(
                    title = "Autumn Fashion Lookbook Campaign",
                    clientName = "Atelier Noir Fashion Studio",
                    clientEmail = "contact@ateliernoir.hu",
                    clientPhone = "+36 20 811 5532",
                    shootDate = "2026-10-06",
                    shootLocation = "Kelenföld Industrial Hall, Budapest",
                    totalAmount = 380000.0,
                    depositPaid = 380000.0,
                    status = ContractStatus.SHOOT_COMPLETED,
                    deliverables = "40 retouched editorial editorial shots for print catalog and Instagram reels visuals.",
                    deliverableDeadline = "2026-10-16",
                    notes = "Shoot completed successfully. Currently in colour grading and skin retouching phase."
                )
            )

            dao.insertContract(
                PhotoContract(
                    title = "Executive Architecture & Interior Series",
                    clientName = "Nordic Living Real Estate",
                    clientEmail = "projects@nordicliving.eu",
                    clientPhone = "+36 70 330 9012",
                    shootDate = "2026-11-04",
                    shootLocation = "Buda Hills Luxury Villa, District XII",
                    totalAmount = 290000.0,
                    depositPaid = 100000.0,
                    status = ContractStatus.SENT,
                    deliverables = "25 HDR architectural twilight & daylight frames, commercial licensing rights.",
                    deliverableDeadline = "2026-11-12",
                    notes = "Client requested blue hour twilight exterior bracketed shots."
                )
            )

            // Initial Photo Tasks (Linked to contracts & general)
            dao.insertTask(
                PhotoTask(
                    contractId = weddingContractId,
                    contractTitle = "Emma & David Wedding Photography",
                    title = "Scout sunset light angles at Fisherman's Bastion",
                    dueDate = "2026-10-12",
                    priority = TaskPriority.HIGH,
                    isCompleted = true,
                    category = "Shoot Prep",
                    notes = "Checked tourist flow, best vantage point is beside the northern tower at 16:45."
                )
            )
            dao.insertTask(
                PhotoTask(
                    contractId = weddingContractId,
                    contractTitle = "Emma & David Wedding Photography",
                    title = "Clean A7 IV sensor & pack 4x fully charged NP-FZ100 batteries",
                    dueDate = "2026-10-17",
                    priority = TaskPriority.URGENT,
                    isCompleted = false,
                    category = "Shoot Prep",
                    notes = "Also wipe front elements and clean dual SD card contacts."
                )
            )
            dao.insertTask(
                PhotoTask(
                    contractId = lookbookContractId,
                    contractTitle = "Autumn Fashion Lookbook Campaign",
                    title = "Export high-res TIFF proof gallery for Art Director approval",
                    dueDate = "2026-10-10",
                    priority = TaskPriority.HIGH,
                    isCompleted = false,
                    category = "Editing",
                    notes = "Upload first 20 selects to client proof portal with watermark."
                )
            )
            dao.insertTask(
                PhotoTask(
                    title = "Annual Gear Insurance Policy Review with Allianz",
                    dueDate = "2026-10-25",
                    priority = TaskPriority.MEDIUM,
                    isCompleted = false,
                    category = "Gear Maintenance",
                    notes = "Add recently acquired 24-70 GM II and update scheduled equipment values."
                )
            )

            // Photo Gear Wishes (Linked to Financial Wishes Tab!)
            dao.insertWish(
                WishItem(
                    title = "Sony FE 70-200mm f/2.8 GM OSS II",
                    targetPrice = 1120000.0,
                    currentSaved = 740000.0,
                    priority = WishPriority.HIGH,
                    category = "Photo Gear",
                    targetDate = "2026-11-30",
                    notes = "Lightweight Gen II telephoto essential for distant ceremony moments and outdoor portrait compression.",
                    isGearWish = true,
                    gearCategory = "Lens"
                )
            )
            dao.insertWish(
                WishItem(
                    title = "Profoto B10X Plus 500Ws Studio Duo Kit",
                    targetPrice = 1680000.0,
                    currentSaved = 520000.0,
                    priority = WishPriority.MEDIUM,
                    category = "Photo Gear",
                    targetDate = "2027-02-15",
                    notes = "High-speed sync battery monobloc with continuous bi-color modeling light for high-end studio fashion.",
                    isGearWish = true,
                    gearCategory = "Lighting & Flash"
                )
            )

            // Initial LifeOS Modules (Customizable & Expandable)
            dao.insertLifeModule(
                LifeOsModuleItem(
                    moduleKey = "photographer_studio",
                    title = "Photographer Studio",
                    subtitle = "Gear Inventory, Gear Wishlist, Contracts & Shoots",
                    iconKey = "camera",
                    category = "Creative Work",
                    isEnabled = true,
                    badgeText = "3 Active",
                    orderIndex = 0
                )
            )
            dao.insertLifeModule(
                LifeOsModuleItem(
                    moduleKey = "wealth_flow",
                    title = "WealthFlow Finance",
                    subtitle = "Bank Accounts, Budgets, Cashflow & Treasury Bonds",
                    iconKey = "account_balance",
                    category = "Financial Engine",
                    isEnabled = true,
                    badgeText = "27.5M Ft",
                    orderIndex = 1
                )
            )
            dao.insertLifeModule(
                LifeOsModuleItem(
                    moduleKey = "life_tasks",
                    title = "Tasks & Schedule",
                    subtitle = "Contract Shoots, Client Deadlines & Life Focus",
                    iconKey = "task",
                    category = "Productivity",
                    isEnabled = true,
                    badgeText = "4 Due",
                    orderIndex = 2
                )
            )
            dao.insertLifeModule(
                LifeOsModuleItem(
                    moduleKey = "location_scout",
                    title = "Location Scout & Golden Hour",
                    subtitle = "Saved photography spots, sunset angles & GPS pins",
                    iconKey = "place",
                    category = "Photography Field",
                    isEnabled = true,
                    badgeText = "8 Spots",
                    orderIndex = 3
                )
            )
            dao.insertLifeModule(
                LifeOsModuleItem(
                    moduleKey = "client_crm",
                    title = "Client CRM & Invoicing",
                    subtitle = "Client relationships, invoice generation & tax receipts",
                    iconKey = "people",
                    category = "Business",
                    isEnabled = false,
                    badgeText = "Expandable",
                    orderIndex = 4
                )
            )
            dao.insertLifeModule(
                LifeOsModuleItem(
                    moduleKey = "health_wellness",
                    title = "Health & Energy",
                    subtitle = "Shoot endurance, hydration & recovery tracker",
                    iconKey = "fitness",
                    category = "Wellness",
                    isEnabled = false,
                    badgeText = "Expandable",
                    orderIndex = 5
                )
            )

            // Seed Photography Ideas (Pinterest-style Moodboards)
            dao.insertPhotoIdea(
                PhotoIdeaItem(
                    title = "Sunset Vineyard Silhouette & Backlight Flare",
                    boardName = "Weddings",
                    tags = "#sunset #goldenhour #backlight #85mm #warmglow",
                    description = "Position the couple directly between camera and low-angled sun behind hilltop trees. Expose for highlight rim on bride's hair, let foreground fall into soft romantic silhouette.",
                    cameraSettings = "85mm f/1.4 • 1/800s • ISO 100 • WB: 5800K",
                    recommendedGear = "Sony A7 IV + 85mm f/1.4 GM",
                    colorPaletteHexes = "#F4A261,#E76F51,#2A9D8F,#264653,#E9C46A",
                    moodTheme = "Warm Sunset Glow",
                    isPinned = true,
                    linkedContractId = weddingContractId,
                    linkedContractTitle = "Emma & David Wedding Photography"
                )
            )
            dao.insertPhotoIdea(
                PhotoIdeaItem(
                    title = "Film Noir Moody Editorial with High Contrast Rim",
                    boardName = "Editorial & Fashion",
                    tags = "#filmnoir #highcontrast #shadowplay #bw #fashion",
                    description = "Single Godox strobe with grid snoot from 45-degree angle behind model. Heavy shadow falloff across cheekbones. Black silk background.",
                    cameraSettings = "24-70mm at 50mm f/4.0 • 1/200s • ISO 100 • Gridded Softbox",
                    recommendedGear = "Sony A7 IV + 24-70mm GM II + Godox AD200",
                    colorPaletteHexes = "#1B1B1E,#373F51,#58A4B0,#D8DBE2,#A9BCD0",
                    moodTheme = "Moody Monochromatic",
                    isPinned = true,
                    linkedContractId = lookbookContractId,
                    linkedContractTitle = "Autumn Fashion Lookbook Campaign"
                )
            )
            dao.insertPhotoIdea(
                PhotoIdeaItem(
                    title = "Clamshell Beauty Lighting Setup with Silver Reflector",
                    boardName = "Portraits",
                    tags = "#beauty #clamshell #softbox #catchlights #portraits",
                    description = "Beauty dish 45 degrees above subject eye level, silver reflector below chin resting on subject's lap. Creates sparkling catchlights top and bottom of iris.",
                    cameraSettings = "85mm f/2.8 • 1/160s • ISO 100 • Strobe 1/8 power",
                    recommendedGear = "Sony FE 85mm GM + Godox AD200 + Beauty Dish",
                    colorPaletteHexes = "#FDE2E4,#FFCAD4,#B5E2FA,#EDDEA4,#F7A072",
                    moodTheme = "Pastel Ethereal",
                    isPinned = false
                )
            )
            dao.insertPhotoIdea(
                PhotoIdeaItem(
                    title = "Blue Hour Architectural Twilight Long Exposure",
                    boardName = "Architecture",
                    tags = "#bluehour #longexposure #interior #hdr #symmetry",
                    description = "Bracket 5 frames (-2, -1, 0, +1, +2 EV) 15 minutes after official sunset. Capture warm indoor tungsten lights contrasting cool cobalt sky.",
                    cameraSettings = "24mm f/8.0 • 4.0s exposure • ISO 100 • Tripod locked",
                    recommendedGear = "Sony A7 IV + 24-70mm GM II at 24mm + Sturdy Tripod",
                    colorPaletteHexes = "#03045E,#0077B6,#00B4D8,#90E0EF,#CAF0F8",
                    moodTheme = "Cobalt Twilight",
                    isPinned = false
                )
            )

            // Initial Weekly Costs with Realistic Categories & Days for Sum Testing
            dao.insertWeeklyCost(
                WeeklyCostItem(
                    title = "Weekly Fresh Groceries & Meal Prep",
                    amount = 21500.0,
                    category = "Groceries",
                    date = "2026-10-05",
                    dayOfWeek = "Monday",
                    weekNumber = 41,
                    year = 2026,
                    notes = "Organic vegetables, salmon, dairy & artisan sourdough"
                )
            )
            dao.insertWeeklyCost(
                WeeklyCostItem(
                    title = "Budapest Metro & Tram Weekly Transit Pass",
                    amount = 6500.0,
                    category = "Transport",
                    date = "2026-10-05",
                    dayOfWeek = "Monday",
                    weekNumber = 41,
                    year = 2026,
                    notes = "BKK city transit travel pass"
                )
            )
            dao.insertWeeklyCost(
                WeeklyCostItem(
                    title = "Studio Seamless Backdrop Paper & Gaffer Tape",
                    amount = 14800.0,
                    category = "Studio & Work",
                    date = "2026-10-06",
                    dayOfWeek = "Tuesday",
                    weekNumber = 41,
                    year = 2026,
                    notes = "White seamless 2.7m roll for commercial shoots"
                )
            )
            dao.insertWeeklyCost(
                WeeklyCostItem(
                    title = "Espresso & Client Pre-Shoot Lunch",
                    amount = 5400.0,
                    category = "Dining",
                    date = "2026-10-07",
                    dayOfWeek = "Wednesday",
                    weekNumber = 41,
                    year = 2026,
                    notes = "Coffee & lunch meeting with fashion client"
                )
            )
            dao.insertWeeklyCost(
                WeeklyCostItem(
                    title = "High-Speed Studio Fiber Internet",
                    amount = 8200.0,
                    category = "Utilities",
                    date = "2026-10-08",
                    dayOfWeek = "Thursday",
                    weekNumber = 41,
                    year = 2026,
                    notes = "Gigabit fiber upload bandwidth for raw client delivery"
                )
            )
            dao.insertWeeklyCost(
                WeeklyCostItem(
                    title = "Saturday Local Artisan Market",
                    amount = 11200.0,
                    category = "Groceries",
                    date = "2026-10-10",
                    dayOfWeek = "Saturday",
                    weekNumber = 41,
                    year = 2026,
                    notes = "Fresh fruit, local cheeses and honey"
                )
            )
            dao.insertWeeklyCost(
                WeeklyCostItem(
                    title = "Supermarket Pantry Restock",
                    amount = 19800.0,
                    category = "Groceries",
                    date = "2026-09-28",
                    dayOfWeek = "Monday",
                    weekNumber = 40,
                    year = 2026,
                    notes = "Prior week grocery baseline"
                )
            )
            dao.insertWeeklyCost(
                WeeklyCostItem(
                    title = "Lens Cleaning Fluid & Microfiber Kits",
                    amount = 7200.0,
                    category = "Studio & Work",
                    date = "2026-09-30",
                    dayOfWeek = "Wednesday",
                    weekNumber = 40,
                    year = 2026,
                    notes = "Optics maintenance supplies"
                )
            )

            // Initial Expected Costs (Explicitly distinguished from discretionary Wishlist)
            dao.insertExpectedCost(
                ExpectedCostItem(
                    title = "Spanish B2 Intensive Language Course",
                    estimatedAmount = 145000.0,
                    allocatedAmount = 60000.0,
                    category = "Language & Education",
                    targetDate = "2026-11-20",
                    status = ExpectedCostStatus.PLANNING,
                    urgency = ExpectedCostUrgency.REQUIRED,
                    notes = "Instituto Cervantes semester tuition + workbook package",
                    isMandatory = true
                )
            )
            dao.insertExpectedCost(
                ExpectedCostItem(
                    title = "Japan Autumn Photography & Cultural Trip",
                    estimatedAmount = 980000.0,
                    allocatedAmount = 420000.0,
                    category = "Travel & Trips",
                    targetDate = "2027-10-15",
                    status = ExpectedCostStatus.PLANNING,
                    urgency = ExpectedCostUrgency.PLANNED,
                    notes = "Flights, 14-day JR Rail Pass, Kyoto ryokan bookings, and local permits",
                    isMandatory = false
                )
            )
            dao.insertExpectedCost(
                ExpectedCostItem(
                    title = "Adobe Certified Professional Examination",
                    estimatedAmount = 55000.0,
                    allocatedAmount = 55000.0,
                    category = "Career & Certifications",
                    targetDate = "2026-12-10",
                    status = ExpectedCostStatus.FUNDED,
                    urgency = ExpectedCostUrgency.REQUIRED,
                    notes = "Photoshop & Lightroom official certification voucher",
                    isMandatory = true
                )
            )
            dao.insertExpectedCost(
                ExpectedCostItem(
                    title = "Annual Commercial Equipment Insurance",
                    estimatedAmount = 185000.0,
                    allocatedAmount = 80000.0,
                    category = "Living & Commitments",
                    targetDate = "2027-01-15",
                    status = ExpectedCostStatus.COMMITTED,
                    urgency = ExpectedCostUrgency.REQUIRED,
                    notes = "Worldwide all-risk gear coverage policy renewal",
                    isMandatory = true
                )
            )
        }
    }
}
