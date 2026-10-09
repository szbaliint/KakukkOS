package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.security.BiometricAuthManager
import com.example.ui.security.BiometricStatus
import com.example.ui.security.findFragmentActivity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun LockScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier,
    onDismissOrBack: (() -> Unit)? = null
) {
    if (onDismissOrBack != null) {
        BackHandler {
            onDismissOrBack()
        }
    }

    val context = LocalContext.current
    val activity = remember(context) { context.findFragmentActivity() }
    val securityProfile by viewModel.securityProfile.collectAsStateWithLifecycle()
    val isBiometricScanning by viewModel.isBiometricScanning.collectAsStateWithLifecycle()

    var pinEntered by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var showEnrollDialog by remember { mutableStateOf(false) }

    val biometricStatus = remember(context) {
        BiometricAuthManager.checkBiometricStatus(context)
    }

    var hasAttemptedInitialPrompt by remember { mutableStateOf(false) }

    val triggerBiometricAuthentication = {
        if (activity != null) {
            when (biometricStatus) {
                BiometricStatus.READY -> {
                    viewModel.setBiometricScanning(true)
                    errorMessage = null
                    statusMessage = "Awaiting fingerprint sensor..."
                    BiometricAuthManager.showBiometricPrompt(
                        activity = activity,
                        title = "Unlock WealthFlow Finance Hub",
                        subtitle = "Confirm your fingerprint or face to access financial data",
                        negativeButtonText = "Use PIN",
                        onSuccess = {
                            viewModel.onBiometricAuthSuccess()
                            statusMessage = "Authenticated successfully!"
                        },
                        onError = { errorCode, errString ->
                            viewModel.setBiometricScanning(false)
                            when (errorCode) {
                                BiometricPrompt.ERROR_USER_CANCELED,
                                BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                BiometricPrompt.ERROR_CANCELED -> {
                                    // User deliberately dismissed or chose PIN
                                    statusMessage = null
                                }
                                BiometricPrompt.ERROR_LOCKOUT,
                                BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                                    errorMessage = "Sensor locked due to attempts. Please enter your PIN."
                                    statusMessage = null
                                }
                                else -> {
                                    errorMessage = errString.toString()
                                    statusMessage = null
                                }
                            }
                        },
                        onFailed = {
                            viewModel.setBiometricScanning(false)
                            errorMessage = "Fingerprint not recognized. Try again."
                        }
                    )
                }
                BiometricStatus.NOT_ENROLLED -> {
                    showEnrollDialog = true
                }
                BiometricStatus.NO_HARDWARE -> {
                    errorMessage = "No biometric hardware detected on this device. Use PIN."
                }
                BiometricStatus.HARDWARE_UNAVAILABLE -> {
                    errorMessage = "Biometric sensor is temporarily unavailable. Use PIN."
                }
                BiometricStatus.UNKNOWN_ERROR -> {
                    errorMessage = "Biometric login unavailable. Use PIN."
                }
            }
        } else {
            errorMessage = "Android activity not available. Use PIN."
        }
    }

    // Automatically trigger biometric prompt on initial screen entry if enabled and ready
    LaunchedEffect(biometricStatus, securityProfile.isBiometricEnabled) {
        if (!hasAttemptedInitialPrompt && securityProfile.isBiometricEnabled && biometricStatus == BiometricStatus.READY) {
            hasAttemptedInitialPrompt = true
            triggerBiometricAuthentication()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .testTag("app_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .widthIn(max = 440.dp)
                .padding(24.dp)
        ) {
            if (onDismissOrBack != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(
                        onClick = onDismissOrBack,
                        modifier = Modifier.testTag("auth_return_home_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Back to LifeOS Home",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "FINANCE HUB BIOMETRIC LOCK",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "WealthFlow Finance Hub",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Biometric authentication required to protect net worth, bank accounts, government bonds & cashflows",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Prominent Biometric Login Button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(
                    1.dp,
                    if (biometricStatus == BiometricStatus.READY) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                ),
                onClick = { triggerBiometricAuthentication() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lock_biometric_action_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (biometricStatus == BiometricStatus.READY) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBiometricScanning) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Login",
                                tint = if (biometricStatus == BiometricStatus.READY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Log In with Biometrics",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = when (biometricStatus) {
                                BiometricStatus.READY -> "Tap to scan fingerprint or face"
                                BiometricStatus.NOT_ENROLLED -> "Tap to set up biometrics in Android Settings"
                                BiometricStatus.NO_HARDWARE -> "No sensor on device (use PIN below)"
                                BiometricStatus.HARDWARE_UNAVAILABLE -> "Sensor busy (use PIN below)"
                                BiometricStatus.UNKNOWN_ERROR -> "Biometrics unavailable"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (biometricStatus == BiometricStatus.READY) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PIN Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < pinEntered.length
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = ExpenseRed,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            } else if (statusMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusMessage ?: "",
                    color = IncomeGreen,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Numeric Keypad
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("bio", "0", "del")
            )

            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    row.forEach { key ->
                        when (key) {
                            "bio" -> {
                                IconButton(
                                    onClick = { triggerBiometricAuthentication() },
                                    modifier = Modifier
                                        .size(60.dp)
                                        .testTag("lock_biometric_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Trigger Biometrics",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                            "del" -> {
                                IconButton(
                                    onClick = {
                                        if (pinEntered.isNotEmpty()) {
                                            pinEntered = pinEntered.dropLast(1)
                                            errorMessage = null
                                            statusMessage = null
                                        }
                                    },
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            else -> {
                                Surface(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                                    onClick = {
                                        if (pinEntered.length < 4) {
                                            val newPin = pinEntered + key
                                            pinEntered = newPin
                                            if (newPin.length == 4) {
                                                if (!viewModel.authenticateWithPin(newPin)) {
                                                    val isDefault = securityProfile.pinCode == "2026"
                                                    errorMessage = if (isDefault) "Incorrect PIN. Default is '2026'" else "Incorrect PIN. Please try again."
                                                    pinEntered = ""
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (securityProfile.pinCode == "2026") {
                    TextButton(
                        onClick = {
                            viewModel.authenticateWithPin("2026")
                        }
                    ) {
                        Text("Quick Demo Unlock (2026)", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (onDismissOrBack != null) {
                    TextButton(onClick = onDismissOrBack) {
                        Text("Return to Home", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    // Dialog shown when biometrics are supported but not enrolled on the device
    if (showEnrollDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showEnrollDialog = false },
            title = {
                Text(
                    text = "Biometric Setup Required",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Your Android device has biometric hardware, but no fingerprint or face has been enrolled yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Would you like to open Android Security Settings now to register your fingerprint, or use the 4-digit PIN ('2026') to log in?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEnrollDialog = false
                        BiometricAuthManager.openBiometricEnrollSettings(context)
                    }
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open Android Settings")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEnrollDialog = false }) {
                    Text("Use PIN")
                }
            }
        )
    }
}
