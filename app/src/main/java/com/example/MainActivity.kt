package com.example

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.LifeOsHomeScreen
import com.example.ui.screens.LifeTasksScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.NotificationSheet
import com.example.ui.screens.PhotographerStudioScreen
import com.example.ui.screens.PhotographerWalkthroughDialog
import com.example.ui.screens.SettingsSecuritySheet
import com.example.ui.screens.WalkthroughTutorialScreen
import com.example.ui.screens.WealthFlowFinanceModuleScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FinanceViewModel

enum class LifeOsScreen {
    HOME,
    PHOTOGRAPHER_STUDIO,
    WEALTH_FLOW,
    LIFE_TASKS
}

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LifeOsApp()
            }
        }
    }
}

@Composable
fun LifeOsApp(viewModel: FinanceViewModel = viewModel()) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val showWalkthrough by viewModel.showWalkthrough.collectAsStateWithLifecycle()
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()

    val activeContracts by viewModel.activeContracts.collectAsStateWithLifecycle()
    val pendingTasksCount by viewModel.pendingTasksCount.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(LifeOsScreen.HOME) }
    var studioInitialTab by remember { mutableIntStateOf(0) }
    var wealthSubTab by remember { mutableStateOf<String?>(null) }
    var isNotificationSheetOpen by remember { mutableStateOf(false) }
    var isSettingsSecuritySheetOpen by remember { mutableStateOf(false) }
    var isStudioWalkthroughOpen by remember { mutableStateOf(false) }

    // Security Check: Auto-lock financial vault when app enters background (ON_STOP)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.lockApp()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Global BackHandler: If on any sub-screen (Studio, WealthFlow, Tasks), return to LifeOS Home!
    if (currentScreen != LifeOsScreen.HOME) {
        BackHandler {
            currentScreen = LifeOsScreen.HOME
            wealthSubTab = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (showWalkthrough) {
            WalkthroughTutorialScreen(
                viewModel = viewModel,
                onComplete = {
                    viewModel.dismissWalkthrough()
                },
                onDismiss = { viewModel.dismissWalkthrough() }
            )
        } else {
            Scaffold(
                bottomBar = {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp,
                            windowInsets = NavigationBarDefaults.windowInsets,
                            modifier = Modifier.testTag("main_bottom_nav")
                        ) {
                            // 1. Home
                            NavigationBarItem(
                                selected = currentScreen == LifeOsScreen.HOME,
                                onClick = { currentScreen = LifeOsScreen.HOME },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == LifeOsScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Home",
                                        fontWeight = if (currentScreen == LifeOsScreen.HOME) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("nav_home")
                            )

                            // 2. Studio (with active bookings badge)
                            NavigationBarItem(
                                selected = currentScreen == LifeOsScreen.PHOTOGRAPHER_STUDIO,
                                onClick = {
                                    studioInitialTab = 0
                                    currentScreen = LifeOsScreen.PHOTOGRAPHER_STUDIO
                                },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (activeContracts.isNotEmpty()) {
                                                Badge { Text(activeContracts.size.toString()) }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (currentScreen == LifeOsScreen.PHOTOGRAPHER_STUDIO) Icons.Filled.CameraAlt else Icons.Outlined.CameraAlt,
                                            contentDescription = "Studio"
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = "Studio",
                                        fontWeight = if (currentScreen == LifeOsScreen.PHOTOGRAPHER_STUDIO) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("nav_studio")
                            )

                            // 3. Finances (WealthFlow)
                            NavigationBarItem(
                                selected = currentScreen == LifeOsScreen.WEALTH_FLOW,
                                onClick = {
                                    wealthSubTab = null
                                    currentScreen = LifeOsScreen.WEALTH_FLOW
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentScreen == LifeOsScreen.WEALTH_FLOW) Icons.Filled.AccountBalance else Icons.Outlined.AccountBalance,
                                        contentDescription = "Finances"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Finances",
                                        fontWeight = if (currentScreen == LifeOsScreen.WEALTH_FLOW) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("nav_finances")
                            )

                            // 4. Tasks (with pending tasks badge)
                            NavigationBarItem(
                                selected = currentScreen == LifeOsScreen.LIFE_TASKS,
                                onClick = { currentScreen = LifeOsScreen.LIFE_TASKS },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (pendingTasksCount > 0) {
                                                Badge { Text(pendingTasksCount.toString()) }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (currentScreen == LifeOsScreen.LIFE_TASKS) Icons.Filled.TaskAlt else Icons.Outlined.TaskAlt,
                                            contentDescription = "Tasks"
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = "Tasks",
                                        fontWeight = if (currentScreen == LifeOsScreen.LIFE_TASKS) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("nav_tasks")
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        LifeOsScreen.HOME -> LifeOsHomeScreen(
                            viewModel = viewModel,
                            onNavigateToStudio = { tab ->
                                studioInitialTab = tab
                                currentScreen = LifeOsScreen.PHOTOGRAPHER_STUDIO
                            },
                            onNavigateToWealthFlow = { subTab ->
                                wealthSubTab = subTab
                                currentScreen = LifeOsScreen.WEALTH_FLOW
                            },
                            onNavigateToTasks = { currentScreen = LifeOsScreen.LIFE_TASKS },
                            onOpenNotifications = { isNotificationSheetOpen = true },
                            onOpenSettings = { isSettingsSecuritySheetOpen = true },
                            onOpenStudioWalkthrough = { isStudioWalkthroughOpen = true }
                        )

                        LifeOsScreen.PHOTOGRAPHER_STUDIO -> PhotographerStudioScreen(
                            viewModel = viewModel,
                            initialTab = studioInitialTab,
                            onNavigateBack = { currentScreen = LifeOsScreen.HOME },
                            onNavigateToFinancesWishes = {
                                wealthSubTab = "wishes"
                                currentScreen = LifeOsScreen.WEALTH_FLOW
                            }
                        )

                        LifeOsScreen.WEALTH_FLOW -> {
                            if (!isAuthenticated) {
                                LockScreen(
                                    viewModel = viewModel,
                                    onDismissOrBack = {
                                        currentScreen = LifeOsScreen.HOME
                                        wealthSubTab = null
                                    }
                                )
                            } else {
                                WealthFlowFinanceModuleScreen(
                                    viewModel = viewModel,
                                    initialSubTab = wealthSubTab,
                                    onNavigateBackToLifeOs = { currentScreen = LifeOsScreen.HOME },
                                    onOpenNotifications = { isNotificationSheetOpen = true },
                                    onOpenSettings = { isSettingsSecuritySheetOpen = true }
                                )
                            }
                        }

                        LifeOsScreen.LIFE_TASKS -> LifeTasksScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = LifeOsScreen.HOME }
                        )
                    }
                }
            }

            // Real-time Market Alerts & System Notifications Sheet
            if (isNotificationSheetOpen) {
                NotificationSheet(
                    viewModel = viewModel,
                    onDismiss = { isNotificationSheetOpen = false }
                )
            }

            // Security & Settings Modal Bottom Sheet
            if (isSettingsSecuritySheetOpen) {
                SettingsSecuritySheet(
                    viewModel = viewModel,
                    onDismiss = { isSettingsSecuritySheetOpen = false }
                )
            }

            // Studio Walkthrough Tour (Triggerable from Home or Studio)
            if (isStudioWalkthroughOpen) {
                PhotographerWalkthroughDialog(
                    onDismiss = { isStudioWalkthroughOpen = false }
                )
            }
        }
    }
}
