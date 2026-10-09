package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.FinanceViewModel

enum class WealthNavDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("dashboard", "Overview", Icons.Filled.Home, Icons.Outlined.Home),
    EXPECTED_COSTS("expected_costs", "Expected Costs", Icons.Filled.DateRange, Icons.Outlined.DateRange),
    CASHFLOW("cashflows", "Budgets & Cashflow", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong),
    WISHES("wishes", "Financial Wishes", Icons.Filled.CardGiftcard, Icons.Outlined.CardGiftcard),
    BANK_ACCOUNTS("bank_accounts", "Bank Accounts", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance),
    SAVINGS("savings", "Bonds & TBSZ", Icons.Filled.Savings, Icons.Outlined.Savings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WealthFlowFinanceModuleScreen(
    viewModel: FinanceViewModel,
    initialSubTab: String?,
    onNavigateBackToLifeOs: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit
) {
    fun routeToDest(route: String?): WealthNavDestination {
        return when (route) {
            "wishes" -> WealthNavDestination.WISHES
            "expected_costs" -> WealthNavDestination.EXPECTED_COSTS
            "bank_accounts" -> WealthNavDestination.BANK_ACCOUNTS
            "cashflows", "weekly_costs" -> WealthNavDestination.CASHFLOW
            "savings" -> WealthNavDestination.SAVINGS
            else -> WealthNavDestination.DASHBOARD
        }
    }

    var currentScreen by remember { mutableStateOf(routeToDest(initialSubTab)) }
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()

    LaunchedEffect(initialSubTab) {
        if (initialSubTab != null) {
            currentScreen = routeToDest(initialSubTab)
        }
    }

    // Back handling: If on a sub-tab, return to Overview/Dashboard; if on Dashboard, return to LifeOS Home!
    BackHandler {
        if (currentScreen != WealthNavDestination.DASHBOARD) {
            currentScreen = WealthNavDestination.DASHBOARD
        } else {
            onNavigateBackToLifeOs()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "WealthFlow Finances",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Net Worth, Bank Accounts, Budgets & Bonds",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (currentScreen != WealthNavDestination.DASHBOARD) {
                                    currentScreen = WealthNavDestination.DASHBOARD
                                } else {
                                    onNavigateBackToLifeOs()
                                }
                            },
                            modifier = Modifier.testTag("wealth_back_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to LifeOS")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.lockApp() },
                            modifier = Modifier.testTag("wealth_lock_btn")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock Finances")
                        }
                        IconButton(
                            onClick = onOpenNotifications,
                            modifier = Modifier.testTag("wealth_notif_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifications > 0) {
                                        Badge { Text(unreadNotifications.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Outlined.Notifications, contentDescription = "Alerts")
                            }
                        }
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("wealth_settings_btn")
                        ) {
                            Icon(Icons.Outlined.Settings, contentDescription = "Settings & Security")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                // Sub-feature tab row (matching Photographer Studio mental model)
                ScrollableTabRow(
                    selectedTabIndex = WealthNavDestination.entries.indexOf(currentScreen),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 16.dp,
                    modifier = Modifier.fillMaxWidth().testTag("wealth_sub_tabs")
                ) {
                    WealthNavDestination.entries.forEach { dest ->
                        val isSelected = currentScreen == dest
                        Tab(
                            selected = isSelected,
                            onClick = { currentScreen = dest },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = dest.title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            modifier = Modifier.testTag("nav_${dest.route}")
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.widthIn(max = 900.dp).fillMaxSize()) {
                WealthContentRouter(
                    currentScreen = currentScreen,
                    viewModel = viewModel,
                    onNavigateTo = { currentScreen = it }
                )
            }
        }
    }
}

@Composable
fun WealthContentRouter(
    currentScreen: WealthNavDestination,
    viewModel: FinanceViewModel,
    onNavigateTo: (WealthNavDestination) -> Unit
) {
    when (currentScreen) {
        WealthNavDestination.DASHBOARD -> DashboardScreen(
            viewModel = viewModel,
            onNavigateToWishes = { onNavigateTo(WealthNavDestination.WISHES) },
            onNavigateToCashflow = { onNavigateTo(WealthNavDestination.CASHFLOW) },
            onNavigateToTreasury = { onNavigateTo(WealthNavDestination.SAVINGS) },
            onNavigateToBanks = { onNavigateTo(WealthNavDestination.BANK_ACCOUNTS) },
            onNavigateToExpectedCosts = { onNavigateTo(WealthNavDestination.EXPECTED_COSTS) }
        )
        WealthNavDestination.EXPECTED_COSTS -> ExpectedCostsScreen(
            viewModel = viewModel,
            onNavigateToWishes = { onNavigateTo(WealthNavDestination.WISHES) }
        )
        WealthNavDestination.BANK_ACCOUNTS -> BankAccountsScreen(viewModel = viewModel)
        WealthNavDestination.CASHFLOW -> CashflowBudgetScreen(viewModel = viewModel)
        WealthNavDestination.SAVINGS -> TreasuryTbszScreen(viewModel = viewModel)
        WealthNavDestination.WISHES -> WishesScreen(
            viewModel = viewModel,
            onNavigateToExpectedCosts = { onNavigateTo(WealthNavDestination.EXPECTED_COSTS) }
        )
    }
}
