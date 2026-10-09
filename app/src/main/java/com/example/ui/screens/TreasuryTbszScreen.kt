package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AssetCategory
import com.example.data.model.InvestmentAsset
import com.example.data.model.MakBondType
import com.example.ui.components.formatHuf
import com.example.ui.components.formatHufCompact
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MakBlue
import com.example.ui.theme.TbszPurple
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun TreasuryTbszScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val investments by viewModel.investments.collectAsStateWithLifecycle()
    val overview by viewModel.overview.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: MÁK Treasury Bonds, 1: TBSZ Portfolios, 2: 5-Year Exemption Timeline
    var isAddDialogOpen by remember { mutableStateOf(false) }

    val makBonds = investments.filter { it.category == AssetCategory.MAK_BOND }
    val tbszAssets = investments.filter { it.category == AssetCategory.TBSZ_PORTFOLIO }

    val totalMakValue = makBonds.sumOf { it.currentEstimatedValue }
    val totalTbszValue = tbszAssets.sumOf { it.currentEstimatedValue }
    val totalInvested = totalMakValue + totalTbszValue
    val totalAnnualCoupon = makBonds.sumOf { it.expectedAnnualIncome }

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddDialogOpen = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_asset_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Investment")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1100.dp)
                    .testTag("treasury_tbsz_screen"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Executive Treasury & TBSZ Overview Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            MakBlue.copy(alpha = 0.08f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(
                                            text = "TOTAL SAVINGS & BONDS",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.2.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Start
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = formatHuf(totalInvested),
                                            style = MaterialTheme.typography.displayMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = (-1.0).sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Start
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = IncomeGreen.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, IncomeGreen.copy(alpha = 0.35f))
                                    ) {
                                        Text(
                                            text = "0% SZJA Exempt",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = IncomeGreen,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // 3 Metric Badges
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, MakBlue.copy(alpha = 0.25f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text("MÁK Bonds", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(formatHufCompact(totalMakValue), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MakBlue)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, TbszPurple.copy(alpha = 0.25f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text("TBSZ Accounts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(formatHufCompact(totalTbszValue), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TbszPurple)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, IncomeGreen.copy(alpha = 0.25f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text("Annual Yield", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("+${formatHufCompact(totalAnnualCoupon)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = IncomeGreen)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab Switcher
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("MÁK Treasury (${makBonds.size})", style = MaterialTheme.typography.titleSmall) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("TBSZ Accounts (${tbszAssets.size})", style = MaterialTheme.typography.titleSmall) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Tax Timeline", style = MaterialTheme.typography.titleSmall) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> {
                        // MÁK Bonds List
                        items(makBonds) { bond ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth().testTag("mak_bond_card_${bond.id}")
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
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(MakBlue.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Savings, contentDescription = null, tint = MakBlue, modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(horizontalAlignment = Alignment.Start) {
                                                Text(
                                                    text = bond.name,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    textAlign = TextAlign.Start
                                                )
                                                Text(
                                                    text = "Maturity: ${bond.maturityDate}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    textAlign = TextAlign.Start
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = IncomeGreen.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${bond.annualYieldPercent}% Yield",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = IncomeGreen,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.Start) {
                                            Text("Current Value", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(formatHuf(bond.currentEstimatedValue), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Expected Coupon", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("+${formatHuf(bond.expectedAnnualIncome)}/yr", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = IncomeGreen)
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteInvestment(bond) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // TBSZ Portfolios List
                        items(tbszAssets) { asset ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth().testTag("tbsz_card_${asset.id}")
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
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(TbszPurple.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Timeline, contentDescription = null, tint = TbszPurple, modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(horizontalAlignment = Alignment.Start) {
                                                Text(
                                                    text = asset.name,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    textAlign = TextAlign.Start
                                                )
                                                Text(
                                                    text = "Opened in ${asset.tbszOpeningYear} • Maturity: ${asset.tbszOpeningYear + 5}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    textAlign = TextAlign.Start
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = TbszPurple.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "TBSZ ${asset.tbszOpeningYear}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = TbszPurple,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.Start) {
                                            Text("Portfolio Value", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(formatHuf(asset.currentEstimatedValue), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Estimated Tax Saved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("+${formatHuf(asset.estimatedTaxSavings)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = IncomeGreen)
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteInvestment(asset) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // 5-Year Exemption Timeline Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "Hungarian TBSZ Tax Exemption Schedule",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        text = "By holding investments across the 3rd and 5th year milestones, personal capital gains tax drops from 15% to 0%.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Start
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    TimelineMilestoneItem(
                                        yearLabel = "Years 0 – 3",
                                        taxRate = "15% SZJA",
                                        status = "Standard capital gains tax rate applies upon premature liquidation."
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    TimelineMilestoneItem(
                                        yearLabel = "Years 3 – 5",
                                        taxRate = "10% SZJA",
                                        status = "Reduced statutory tax rate milestone reached."
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    TimelineMilestoneItem(
                                        yearLabel = "Year 5+",
                                        taxRate = "0% SZJA (100% Tax-Free)",
                                        status = "Full legal tax exemption. All dividend and capital gains are completely tax-free."
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Add Asset Dialog
    if (isAddDialogOpen) {
        var name by remember { mutableStateOf("") }
        var principalStr by remember { mutableStateOf("") }
        var yieldStr by remember { mutableStateOf("6.8") }
        var isMakSelected by remember { mutableStateOf(true) }
        var maturityDate by remember { mutableStateOf("2029-04-20") }
        var tbszYearStr by remember { mutableStateOf("2024") }

        AlertDialog(
            onDismissRequest = { isAddDialogOpen = false },
            title = {
                Text(
                    text = "Add Savings / Investment Asset",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isMakSelected = true },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = if (isMakSelected) MakBlue else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isMakSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("MÁK Bond", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { isMakSelected = false },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = if (!isMakSelected) TbszPurple else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (!isMakSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("TBSZ Account", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (isMakSelected) "Bond Name (e.g. FixMÁP 2029/Q1)" else "Portfolio Name (e.g. All-World ETF)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = principalStr,
                        onValueChange = { principalStr = it },
                        label = { Text("Invested Amount (HUF)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = yieldStr,
                        onValueChange = { yieldStr = it },
                        label = { Text("Annual Yield % (e.g. 7.5)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isMakSelected) {
                        OutlinedTextField(
                            value = maturityDate,
                            onValueChange = { maturityDate = it },
                            label = { Text("Maturity Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = tbszYearStr,
                            onValueChange = { tbszYearStr = it },
                            label = { Text("TBSZ Opening Year (e.g. 2024)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val principal = principalStr.toDoubleOrNull() ?: 0.0
                        val yieldPercent = yieldStr.toDoubleOrNull() ?: 5.0
                        if (name.isNotBlank() && principal > 0) {
                            viewModel.addInvestment(
                                name = name.trim(),
                                category = if (isMakSelected) AssetCategory.MAK_BOND else AssetCategory.TBSZ_PORTFOLIO,
                                makType = if (isMakSelected) MakBondType.FIXMAP else MakBondType.OTHER,
                                principal = principal,
                                currentValue = principal,
                                annualYield = yieldPercent,
                                accruedInterest = 0.0,
                                maturityDate = if (isMakSelected) maturityDate else "${tbszYearStr.toIntOrNull() ?: 2024 + 5}-12-31",
                                nextCouponDate = "2026-10-15",
                                tbszOpeningYear = if (isMakSelected) 0 else (tbszYearStr.toIntOrNull() ?: 2024),
                                notes = ""
                            )
                            isAddDialogOpen = false
                        }
                    }
                ) {
                    Text("Add Asset")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddDialogOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TimelineMilestoneItem(
    yearLabel: String,
    taxRate: String,
    status: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Text(
                    text = taxRate,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = yearLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
