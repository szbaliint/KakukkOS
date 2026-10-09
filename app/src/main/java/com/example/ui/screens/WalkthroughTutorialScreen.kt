package com.example.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.notification.SystemNotificationHelper
import com.example.ui.security.BiometricAuthManager
import com.example.ui.security.BiometricStatus
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.Indigo500
import com.example.ui.theme.MakBlue
import com.example.ui.theme.TbszPurple
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun WalkthroughTutorialScreen(
    viewModel: FinanceViewModel,
    onComplete: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) } // 0: Welcome, 1: PIN Setup, 2: Notifications, 3: Profile & Currency, 4: Ready

    // Walkthrough user draft state
    var chosenPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirmingPinStep by remember { mutableStateOf(false) }
    var pinErrorMessage by remember { mutableStateOf<String?>(null) }
    var pinSetupDone by remember { mutableStateOf(false) }

    var biometricEnabled by remember { mutableStateOf(true) }
    val biometricStatus = remember(context) { BiometricAuthManager.checkBiometricStatus(context) }

    // Notifications configuration
    var systemNotifEnabled by remember { mutableStateOf(true) }
    var makNotifEnabled by remember { mutableStateOf(true) }
    var tbszNotifEnabled by remember { mutableStateOf(true) }
    var budgetNotifEnabled by remember { mutableStateOf(true) }
    var marketNotifEnabled by remember { mutableStateOf(true) }
    var testNotifSentMessage by remember { mutableStateOf<String?>(null) }

    var hasSystemPermission by remember {
        mutableStateOf(SystemNotificationHelper.hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasSystemPermission = isGranted
        if (isGranted) {
            systemNotifEnabled = true
            Toast.makeText(context, "Notifications enabled successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission was not granted.", Toast.LENGTH_SHORT).show()
        }
    }

    // Profile & Currency
    var userNameInput by remember { mutableStateOf("Bálint") }
    var selectedCurrency by remember { mutableStateOf("HUF") }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        IconButton(
                            onClick = {
                                if (currentStep == 1 && isConfirmingPinStep) {
                                    isConfirmingPinStep = false
                                    confirmPin = ""
                                    pinErrorMessage = null
                                } else {
                                    currentStep -= 1
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // Progress indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (0..4).forEach { stepIndex ->
                            Box(
                                modifier = Modifier
                                    .size(
                                        width = if (stepIndex == currentStep) 24.dp else 8.dp,
                                        height = 8.dp
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        if (stepIndex == currentStep) MaterialTheme.colorScheme.primary
                                        else if (stepIndex < currentStep) IncomeGreen
                                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }

                    if (onDismiss != null) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close Walkthrough")
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0 && currentStep < 4) {
                        OutlinedButton(
                            onClick = {
                                if (currentStep == 1 && isConfirmingPinStep) {
                                    isConfirmingPinStep = false
                                    confirmPin = ""
                                } else {
                                    currentStep -= 1
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Previous")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    when (currentStep) {
                        0 -> {
                            Button(
                                onClick = { currentStep = 1 },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("walkthrough_start_button")
                            ) {
                                Text("Get Started", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        1 -> {
                            Button(
                                onClick = {
                                    if (!pinSetupDone) {
                                        pinErrorMessage = "Please configure your 4-digit PIN first."
                                    } else {
                                        currentStep = 2
                                    }
                                },
                                enabled = pinSetupDone,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("walkthrough_pin_next_button")
                            ) {
                                Text("Continue", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        2 -> {
                            Button(
                                onClick = { currentStep = 3 },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("walkthrough_notif_next_button")
                            ) {
                                Text("Next: Profile", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        3 -> {
                            Button(
                                onClick = { currentStep = 4 },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("walkthrough_profile_next_button")
                            ) {
                                Text("Review & Finish", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        4 -> {
                            Button(
                                onClick = {
                                    val finalPin = if (chosenPin.length == 4) chosenPin else "2026"
                                    viewModel.completeOnboarding(
                                        pin = finalPin,
                                        name = userNameInput.ifBlank { "Portfolio Owner" },
                                        currency = selectedCurrency,
                                        biometricEnabled = biometricEnabled,
                                        systemNotif = systemNotifEnabled,
                                        makNotif = makNotifEnabled,
                                        tbszNotif = tbszNotifEnabled,
                                        budgetNotif = budgetNotifEnabled,
                                        marketNotif = marketNotifEnabled
                                    )
                                    onComplete()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                                modifier = Modifier.fillMaxWidth().testTag("walkthrough_finish_button")
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Launch WealthFlow Vault", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "walkthrough_step_animation"
            ) { step ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 680.dp)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (step) {
                        0 -> {
                            // Step 1: Welcome & Overview
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                                    MaterialTheme.colorScheme.surface
                                                )
                                            )
                                        )
                                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(42.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Welcome to WealthFlow",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.6).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Your private, offline-first wealth management and treasury hub. Setup your security PIN and notifications in under 60 seconds.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            item {
                                FeatureIntroCard(
                                    icon = Icons.Default.Lock,
                                    iconTint = MaterialTheme.colorScheme.primary,
                                    title = "Zero Cloud Leakage • 100% Offline Vault",
                                    description = "All financial records, balances, and savings remain encrypted on your device with local biometric and PIN protection."
                                )
                            }

                            item {
                                FeatureIntroCard(
                                    icon = Icons.Default.Savings,
                                    iconTint = MakBlue,
                                    title = "Magyar Államkincstár (MÁK) Bonds",
                                    description = "Real-time tracking of FixMÁP, PMÁP, and BMÁP Hungarian treasury securities with quarterly coupon payout schedules."
                                )
                            }

                            item {
                                FeatureIntroCard(
                                    icon = Icons.Default.Timeline,
                                    iconTint = TbszPurple,
                                    title = "TBSZ 5-Year Tax Exemption Engine",
                                    description = "Automated milestone timeline tracking for Hungarian long-term investment accounts (15% → 10% → 0% statutory capital gains tax)."
                                )
                            }

                            item {
                                FeatureIntroCard(
                                    icon = Icons.Default.NotificationsActive,
                                    iconTint = Amber500,
                                    title = "Instant System Notifications",
                                    description = "Receive timely status-bar notifications for coupon arrivals, dividend days, and budget safety threshold alerts."
                                )
                            }
                        }

                        1 -> {
                            // Step 2: Set Personal Security PIN
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = if (!isConfirmingPinStep) "Create Your 4-Digit PIN" else "Confirm Your 4-Digit PIN",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (!isConfirmingPinStep)
                                        "This replaces any demo code. Choose a secure 4-digit PIN you will remember."
                                    else
                                        "Re-enter your 4-digit PIN to confirm accuracy.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Visual PIN Dots
                                val activeLength = if (!isConfirmingPinStep) chosenPin.length else confirmPin.length
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    (0 until 4).forEach { index ->
                                        val isFilled = index < activeLength
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (pinSetupDone) IncomeGreen
                                                    else if (isFilled) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                                    CircleShape
                                                )
                                        )
                                    }
                                }

                                pinErrorMessage?.let { err ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = err,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = ExpenseRed,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                if (pinSetupDone) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = IncomeGreen.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, IncomeGreen.copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Personal PIN Set Successfully", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = IncomeGreen)
                                        }
                                    }
                                }
                            }

                            // Numeric Keypad
                            item {
                                NumericKeypad(
                                    onDigit = { digit ->
                                        if (!isConfirmingPinStep) {
                                            if (chosenPin.length < 4) {
                                                chosenPin += digit
                                                pinErrorMessage = null
                                                if (chosenPin.length == 4) {
                                                    // Move to confirm step
                                                    isConfirmingPinStep = true
                                                }
                                            }
                                        } else {
                                            if (confirmPin.length < 4) {
                                                confirmPin += digit
                                                pinErrorMessage = null
                                                if (confirmPin.length == 4) {
                                                    if (confirmPin == chosenPin) {
                                                        pinSetupDone = true
                                                        pinErrorMessage = null
                                                    } else {
                                                        pinErrorMessage = "PINs do not match. Please re-enter."
                                                        confirmPin = ""
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    onBackspace = {
                                        if (isConfirmingPinStep) {
                                            if (confirmPin.isNotEmpty()) {
                                                confirmPin = confirmPin.dropLast(1)
                                                pinSetupDone = false
                                                pinErrorMessage = null
                                            } else {
                                                isConfirmingPinStep = false
                                                pinSetupDone = false
                                            }
                                        } else {
                                            if (chosenPin.isNotEmpty()) {
                                                chosenPin = chosenPin.dropLast(1)
                                                pinSetupDone = false
                                                pinErrorMessage = null
                                            }
                                        }
                                    },
                                    onReset = {
                                        chosenPin = ""
                                        confirmPin = ""
                                        isConfirmingPinStep = false
                                        pinSetupDone = false
                                        pinErrorMessage = null
                                    }
                                )
                            }

                            // Biometric toggle
                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(horizontalAlignment = Alignment.Start) {
                                                Text("Fingerprint Quick Unlock", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                                Text(
                                                    text = if (biometricStatus == BiometricStatus.READY) "Hardware ready & enrolled" else "Fallback PIN active",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Switch(
                                            checked = biometricEnabled,
                                            onCheckedChange = { biometricEnabled = it },
                                            colors = SwitchDefaults.colors(checkedThumbColor = IncomeGreen, checkedTrackColor = IncomeGreen.copy(alpha = 0.35f))
                                        )
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Step 3: Notifications & System Permissions
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(Amber500.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Amber500,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "System Notification Setup",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Grant notification permissions so WealthFlow can send critical alerts to your device status bar.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Permission status card
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = if (hasSystemPermission) Icons.Default.CheckCircle else Icons.Default.Notifications,
                                                    contentDescription = null,
                                                    tint = if (hasSystemPermission) IncomeGreen else Amber500
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (hasSystemPermission) "System Permission Granted" else "Permission Required (Android 13+)",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (hasSystemPermission) IncomeGreen else MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            if (!hasSystemPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                Button(
                                                    onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text("Grant", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Notification Category Toggles
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Select Alert Categories",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        NotificationToggleRow(
                                            title = "MÁK Treasury Coupon Payouts",
                                            subtitle = "Alerts when FixMÁP, PMÁP, or BMÁP interest credits",
                                            checked = makNotifEnabled,
                                            onCheckedChange = { makNotifEnabled = it },
                                            accentColor = MakBlue
                                        )

                                        NotificationToggleRow(
                                            title = "TBSZ 5-Year Exemption Milestones",
                                            subtitle = "Notify when accounts reach 3-year (10%) and 5-year (0%) tax brackets",
                                            checked = tbszNotifEnabled,
                                            onCheckedChange = { tbszNotifEnabled = it },
                                            accentColor = TbszPurple
                                        )

                                        NotificationToggleRow(
                                            title = "Budget Safety Thresholds",
                                            subtitle = "Warning when category spending crosses 80% and 100% of limits",
                                            checked = budgetNotifEnabled,
                                            onCheckedChange = { budgetNotifEnabled = it },
                                            accentColor = ExpenseRed
                                        )

                                        NotificationToggleRow(
                                            title = "Market Fluctuation & Dividends",
                                            subtitle = "Stock market indices (BUX, S&P 500) and ETF dividend receipts",
                                            checked = marketNotifEnabled,
                                            onCheckedChange = { marketNotifEnabled = it },
                                            accentColor = Amber500
                                        )
                                    }
                                }
                            }

                            // Interactive Test Notification Button
                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = "Test System Notification",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Verify that Android notifications appear in your status bar right now:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasSystemPermission) {
                                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                } else {
                                                    val success = viewModel.sendTestSystemNotification(
                                                        title = "WealthFlow • Test Alert",
                                                        message = "Notifications are fully configured! You will receive FixMÁP coupon credits and budget limits."
                                                    )
                                                    testNotifSentMessage = if (success) {
                                                        "System notification posted! Check your Android status bar."
                                                    } else {
                                                        "Notification sent to app tray. (System notification blocked by OS permission)."
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Send Test Notification Now")
                                        }

                                        testNotifSentMessage?.let { msg ->
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = msg,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = IncomeGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // Step 4: Profile & Currency Personalization
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(Indigo500.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = Indigo500,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Personalize Your Vault",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Set your portfolio alias and primary denomination currency.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = userNameInput,
                                            onValueChange = { userNameInput = it },
                                            label = { Text("Your Name / Portfolio Alias") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Column(horizontalAlignment = Alignment.Start) {
                                            Text(
                                                text = "Base Currency",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                listOf("HUF", "EUR", "USD").forEach { curr ->
                                                    FilterChip(
                                                        selected = selectedCurrency == curr,
                                                        onClick = { selectedCurrency = curr },
                                                        label = { Text(curr, fontWeight = FontWeight.Bold) },
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                            selectedLabelColor = MaterialTheme.colorScheme.primary
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Default Hungarian Treasury & Bank starter portfolio will be ready for you.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        4 -> {
                            // Step 5: Review & Ready
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(IncomeGreen.copy(alpha = 0.15f))
                                        .border(2.dp, IncomeGreen.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = IncomeGreen,
                                        modifier = Modifier.size(42.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Your Vault is Ready!",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.6).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Your private financial command center is configured and secured with AES-256 local encryption.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            item {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        SummaryReviewItem(
                                            label = "Portfolio Owner",
                                            value = userNameInput.ifBlank { "Portfolio Owner" }
                                        )
                                        SummaryReviewItem(
                                            label = "Security PIN",
                                            value = "Custom 4-digit PIN configured (••••)"
                                        )
                                        SummaryReviewItem(
                                            label = "Biometrics",
                                            value = if (biometricEnabled) "Enabled (Fingerprint)" else "Disabled"
                                        )
                                        SummaryReviewItem(
                                            label = "Notifications",
                                            value = if (systemNotifEnabled) "System Status Bar & In-App Alerts" else "In-App Only"
                                        )
                                        SummaryReviewItem(
                                            label = "Currency",
                                            value = selectedCurrency
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureIntroCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = accentColor, checkedTrackColor = accentColor.copy(alpha = 0.35f))
        )
    }
}

@Composable
fun SummaryReviewItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun NumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onReset: () -> Unit
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "DEL")
    )

    Column(
        modifier = Modifier.width(260.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    when (key) {
                        "C" -> {
                            Surface(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                onClick = onReset
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("C", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                        "DEL" -> {
                            Surface(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                onClick = onBackspace
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        else -> {
                            Surface(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                onClick = { onDigit(key) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
