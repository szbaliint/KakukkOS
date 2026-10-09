package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TbszPurple

data class StudioWalkthroughStep(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color,
    val bullets: List<Pair<String, String>>
)

@Composable
fun PhotographerWalkthroughDialog(
    onDismiss: () -> Unit
) {
    var stepIndex by remember { mutableIntStateOf(0) }

    val steps = remember {
        listOf(
            StudioWalkthroughStep(
                title = "Welcome to Photographer Studio",
                subtitle = "Your comprehensive creative command center built for professional photographers.",
                icon = Icons.Default.CameraAlt,
                iconTint = Color(0xFF0077B6),
                bullets = listOf(
                    "Equipment & Gear" to "Track bodies, lenses, serial numbers, insurance coverage, and bag locations.",
                    "Linked Gear Wishlist" to "Plan next-level lenses and lighting with automatic WealthFlow finance sync.",
                    "Contracts & Payments" to "Manage client bookings, deliverables, and record revenue into cashflow.",
                    "Pinterest Moodboards" to "Save visual concepts, lighting setups, color palettes, and pose references."
                )
            ),
            StudioWalkthroughStep(
                title = "1. Gear Inventory & Valuation",
                subtitle = "Keep an organized audit of every camera, lens, strobe, and accessory you own.",
                icon = Icons.Default.Shield,
                iconTint = IncomeGreen,
                bullets = listOf(
                    "Serial Number Tracking" to "Essential for warranty claims, police reports, and equipment insurance policies.",
                    "Condition & Maintenance" to "Tag items as Mint, Excellent, Good, or Needs Service before upcoming weddings.",
                    "Storage Location Tags" to "Know if an item is in your 'Main Pelican 1510', 'Studio Shelf', or 'On Loan'.",
                    "Total Insured Value" to "Real-time calculation of your total capital invested in photography equipment."
                )
            ),
            StudioWalkthroughStep(
                title = "2. Gear Wishlist (Finance Sync)",
                subtitle = "Bridge the gap between creative dreams and financial discipline.",
                icon = Icons.Default.Star,
                iconTint = Amber500,
                bullets = listOf(
                    "Two-Way WealthFlow Integration" to "Any gear wish added here immediately appears in your Financial Wishes tab.",
                    "Savings Progress Tracking" to "Allocate funds gradually until you reach target retail prices.",
                    "1-Tap 'Move to Inventory'" to "Once acquired, promote the item to automatically register it into your gear bag!"
                )
            ),
            StudioWalkthroughStep(
                title = "3. Client Contracts & Cashflow",
                subtitle = "Turn client proposals into confirmed shoots and seamless revenue.",
                icon = Icons.Default.Description,
                iconTint = Color(0xFF9D4EDD),
                bullets = listOf(
                    "Shoot Logistics & Venues" to "Keep shoot date, golden hour timing, venue address, and client contacts in one place.",
                    "Deposit & Remaining Balance" to "Visual progress bars showing paid fees versus balance due upon gallery delivery.",
                    "Record Payment into Cashflow" to "One button credits client payments into your WealthFlow bank accounts and cashflow ledger."
                )
            ),
            StudioWalkthroughStep(
                title = "4. Pinterest-Style Idea Boards",
                subtitle = "Collect moodboards, pose inspirations, lighting setups, and color palettes.",
                icon = Icons.Outlined.Palette,
                iconTint = Color(0xFFE76F51),
                bullets = listOf(
                    "Visual Color Palettes" to "Every idea includes harmonious 5-color aesthetic swatches for styling mood.",
                    "Camera & Lighting Cheat Sheets" to "Store shutter speeds, aperture choices, flash ratios, and diffuser notes.",
                    "Link to Client Contracts" to "Attach an inspiration board directly to an upcoming wedding or lookbook shoot.",
                    "Turn Ideas into Shoot Tasks" to "1-tap convert any concept into an actionable shoot preparation task!"
                )
            ),
            StudioWalkthroughStep(
                title = "5. Shoot Prep & Tasks",
                subtitle = "Never forget a battery or deliver a gallery late.",
                icon = Icons.Default.TaskAlt,
                iconTint = Emerald500,
                bullets = listOf(
                    "Pre-Shoot Checklists" to "Sensor cleaning, battery charging, dual memory card formatting, moodboard scouting.",
                    "Post-Production Deadlines" to "Deliver teasers within 48 hours, high-res selections, and final galleries.",
                    "Contract Association" to "Tasks show which client contract they belong to for quick reference."
                )
            )
        )
    }

    val currentStep = steps[stepIndex]

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(currentStep.iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = currentStep.icon, contentDescription = null, tint = currentStep.iconTint, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Studio Walkthrough",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Step Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    steps.indices.forEach { idx ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (idx <= stepIndex) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }

                // Step Content with animation
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "walkthrough_step"
                ) { step ->
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = step.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        step.bullets.forEach { (heading, desc) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = step.iconTint,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = heading,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (stepIndex < steps.lastIndex) {
                Button(
                    onClick = { stepIndex++ },
                    modifier = Modifier.testTag("walkthrough_next_btn")
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                    modifier = Modifier.testTag("walkthrough_finish_btn")
                ) {
                    Text("Get Started")
                }
            }
        },
        dismissButton = {
            if (stepIndex > 0) {
                TextButton(onClick = { stepIndex-- }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Skip Tour")
                }
            }
        }
    )
}
