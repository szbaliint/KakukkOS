package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetPlan
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.ui.theme.Emerald500
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.Indigo500
import com.example.ui.theme.MakBlue
import com.example.ui.theme.TbszPurple

data class CategoryBudgetComparison(
    val category: String,
    val spent: Double,
    val budget: Double,
    val color: Color
) {
    val variance: Double get() = budget - spent // Positive: under budget, Negative: over budget
    val ratio: Float get() = if (budget > 0) (spent / budget).toFloat() else 0f
    val isOverBudget: Boolean get() = spent > budget
}

/**
 * Visual Summary Component displaying current monthly spending vs. budget goals.
 * Built with rich native Jetpack Compose Canvas charts replicating Recharts visual design language
 * (dual-bar grouped/comparative charts, radial gauge dials, interactive tooltips, and budget delta cards).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthlySpendingVsBudgetSummary(
    budgets: List<BudgetPlan>,
    transactions: List<TransactionItem>,
    modifier: Modifier = Modifier
) {
    // Distinct palette for categories (Recharts aesthetic)
    val categoryColors = listOf(
        Emerald500,
        Indigo500,
        GoldAccent,
        MakBlue,
        TbszPurple,
        Color(0xFFEC4899), // Pink
        Color(0xFF14B8A6), // Teal
        Color(0xFFF97316)  // Orange
    )

    // Compute spending vs budget per category
    val comparisonList = remember(budgets, transactions) {
        val expenseTx = transactions.filter { it.type == TransactionType.EXPENSE }
        budgets.mapIndexed { index, budget ->
            val spent = expenseTx
                .filter { it.category.contains(budget.category, ignoreCase = true) || budget.category.contains(it.category, ignoreCase = true) }
                .sumOf { it.amount }
            CategoryBudgetComparison(
                category = budget.category,
                spent = spent,
                budget = budget.monthlyLimit,
                color = categoryColors[index % categoryColors.size]
            )
        }
    }

    val totalBudget = comparisonList.sumOf { it.budget }
    val totalSpent = comparisonList.sumOf { it.spent }
    val totalRemaining = totalBudget - totalSpent
    val overallRatio = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f
    val isTotalOver = totalSpent > totalBudget

    // Animation progress
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(comparisonList) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    // Chart style mode: 0 = Grouped Bar Chart (Recharts style), 1 = Donut & Variance View
    var chartViewMode by remember { mutableIntStateOf(0) }
    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_spending_vs_budget_summary")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header: Title & View Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "ANALYTICS & GOALS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isTotalOver) ExpenseRed.copy(alpha = 0.15f) else IncomeGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isTotalOver) "OVER BUDGET" else "ON TRACK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (isTotalOver) ExpenseRed else IncomeGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Monthly Spending vs. Budget",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (chartViewMode == 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, if (chartViewMode == 0) MaterialTheme.colorScheme.primary else Color.Transparent),
                        modifier = Modifier
                            .clickable { chartViewMode = 0 }
                            .testTag("btn_chart_mode_bars")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.BarChart, contentDescription = "Bars", modifier = Modifier.size(16.dp), tint = if (chartViewMode == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bars", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (chartViewMode == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (chartViewMode == 1) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, if (chartViewMode == 1) MaterialTheme.colorScheme.primary else Color.Transparent),
                        modifier = Modifier
                            .clickable { chartViewMode = 1 }
                            .testTag("btn_chart_mode_radial")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PieChart, contentDescription = "Radial", modifier = Modifier.size(16.dp), tint = if (chartViewMode == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Radial", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (chartViewMode == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recharts-style KPI Stat Strip (Spent vs Budget Target vs Net Buffer)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Actual Spent
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.25f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ExpenseRed))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Actual Spent", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatHuf(totalSpent),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Total Budget Limit
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, Indigo500.copy(alpha = 0.25f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Indigo500))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Target Budget", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatHuf(totalBudget),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Remaining Buffer / Overrun
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = (if (isTotalOver) ExpenseRed else IncomeGreen).copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, (if (isTotalOver) ExpenseRed else IncomeGreen).copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isTotalOver) ExpenseRed else IncomeGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isTotalOver) "Overrun" else "Remaining", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = if (isTotalOver) ExpenseRed else IncomeGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${if (totalRemaining >= 0) "+" else ""}${formatHuf(totalRemaining)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = if (isTotalOver) ExpenseRed else IncomeGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Render Area
            if (chartViewMode == 0) {
                // RECHARTS-STYLE GROUPED BAR CHART
                SpendingVsBudgetGroupedBarChart(
                    comparisons = comparisonList,
                    animProgress = animProgress.value,
                    selectedIndex = selectedCategoryIndex,
                    onSelectIndex = { selectedCategoryIndex = if (selectedCategoryIndex == it) null else it }
                )
            } else {
                // RECHARTS-STYLE RADIAL / PROGRESS GAUGE VIEW
                SpendingVsBudgetRadialGaugeView(
                    comparisons = comparisonList,
                    totalSpent = totalSpent,
                    totalBudget = totalBudget,
                    overallRatio = overallRatio,
                    animProgress = animProgress.value
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend & Interactive Breakdown Chips
            Text(
                text = "CATEGORY VARIANCE & RATIOS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                comparisonList.forEachIndexed { idx, item ->
                    val isSelected = selectedCategoryIndex == idx
                    val pct = (item.ratio * 100).toInt()
                    val statusColor = if (item.isOverBudget) ExpenseRed else IncomeGreen

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) item.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) item.color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .clickable { selectedCategoryIndex = if (isSelected) null else idx }
                            .testTag("budget_chip_$idx")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(item.color))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = statusColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$pct%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    ),
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Tooltip Card if category selected
            selectedCategoryIndex?.let { idx ->
                val item = comparisonList.getOrNull(idx)
                if (item != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, item.color.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("budget_tooltip_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Spent: ${formatHuf(item.spent)} of ${formatHuf(item.budget)} limit",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (item.isOverBudget) "Over by ${formatHuf(-item.variance)}" else "Under by ${formatHuf(item.variance)}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (item.isOverBudget) ExpenseRed else IncomeGreen
                                )
                                Text(
                                    text = "${(item.ratio * 100).toInt()}% utilized",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recharts-style Comparative Grouped Bar Chart.
 * Displays Budget Goal bar (Indigo) next to Current Spending bar (Emerald/Red) per category,
 * with horizontal grid guidelines, animated height easing, and rounded caps.
 */
@Composable
fun SpendingVsBudgetGroupedBarChart(
    comparisons: List<CategoryBudgetComparison>,
    animProgress: Float,
    selectedIndex: Int?,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (comparisons.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No active budget goals configured.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val maxVal = remember(comparisons) {
        val peak = comparisons.maxOfOrNull { maxOf(it.spent, it.budget) } ?: 1.0
        peak * 1.2
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Legend indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Indigo500))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Budget Goal", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.width(14.dp))

            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(IncomeGreen))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Within Budget", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.width(14.dp))

            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(ExpenseRed))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Over Budget", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Canvas for dual bars
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val chartHeight = size.height - 24.dp.toPx()
                val totalWidth = size.width
                val groupWidth = totalWidth / comparisons.size
                val barWidth = (groupWidth * 0.32f).coerceAtMost(22.dp.toPx())
                val spacingBetweenBars = 3.dp.toPx()

                // Draw 3 horizontal Recharts grid guidelines (dotted/dashed)
                val gridLines = 3
                for (i in 1..gridLines) {
                    val y = chartHeight * (i.toFloat() / (gridLines + 1))
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(totalWidth, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                }

                // Baseline
                drawLine(
                    color = Color.Gray.copy(alpha = 0.35f),
                    start = Offset(0f, chartHeight),
                    end = Offset(totalWidth, chartHeight),
                    strokeWidth = 1.5.dp.toPx()
                )

                comparisons.forEachIndexed { index, comp ->
                    val groupCenterX = (index * groupWidth) + (groupWidth / 2f)

                    val budgetHeight = ((comp.budget / maxVal) * chartHeight * animProgress).toFloat()
                    val spentHeight = ((comp.spent / maxVal) * chartHeight * animProgress).toFloat()

                    val isSelected = selectedIndex == index
                    val isAnySelected = selectedIndex != null

                    val budgetAlpha = if (!isAnySelected || isSelected) 0.85f else 0.35f
                    val spentAlpha = if (!isAnySelected || isSelected) 1f else 0.35f

                    // 1. Budget Bar (Left bar, Indigo tone)
                    val budgetLeft = groupCenterX - barWidth - (spacingBetweenBars / 2f)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(Indigo500.copy(alpha = budgetAlpha), Indigo500.copy(alpha = budgetAlpha * 0.7f))
                        ),
                        topLeft = Offset(budgetLeft, chartHeight - budgetHeight),
                        size = Size(barWidth, budgetHeight.coerceAtLeast(2f)),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // 2. Spending Bar (Right bar, Green if under, Red if over)
                    val spentColor = if (comp.isOverBudget) ExpenseRed else IncomeGreen
                    val spentLeft = groupCenterX + (spacingBetweenBars / 2f)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(spentColor.copy(alpha = spentAlpha), spentColor.copy(alpha = spentAlpha * 0.75f))
                        ),
                        topLeft = Offset(spentLeft, chartHeight - spentHeight),
                        size = Size(barWidth, spentHeight.coerceAtLeast(2f)),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Highlight indicator if selected
                    if (isSelected) {
                        drawCircle(
                            color = comp.color,
                            radius = 3.dp.toPx(),
                            center = Offset(groupCenterX, chartHeight + 12.dp.toPx())
                        )
                    }
                }
            }
        }

        // Category labels under columns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            comparisons.forEachIndexed { index, comp ->
                val isSelected = selectedIndex == index
                Text(
                    text = comp.category.split(" ").firstOrNull() ?: comp.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                    ),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectIndex(index) }
                )
            }
        }
    }
}

/**
 * Recharts-style Radial Gauge & Segmented Progress View.
 * Shows high-level budget consumption as concentric rounded arcs with executive utilization metrics.
 */
@Composable
fun SpendingVsBudgetRadialGaugeView(
    comparisons: List<CategoryBudgetComparison>,
    totalSpent: Double,
    totalBudget: Double,
    overallRatio: Float,
    animProgress: Float,
    modifier: Modifier = Modifier
) {
    val sweepAngleMax = 240f
    val targetSweep = (overallRatio.coerceIn(0f, 1.5f) * sweepAngleMax * animProgress)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(170.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    val arcSize = size.width - strokeWidth
                    val startAngle = 150f

                    // Background Track
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.15f),
                        startAngle = startAngle,
                        sweepAngle = sweepAngleMax,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Target Budget Reference Tick at 100% (240 deg sweep)
                    val budgetTickAngle = Math.toRadians((startAngle + sweepAngleMax).toDouble())
                    val center = Offset(size.width / 2, size.height / 2)
                    val r = arcSize / 2
                    val tickStart = Offset(
                        (center.x + (r - strokeWidth * 0.6f) * Math.cos(budgetTickAngle)).toFloat(),
                        (center.y + (r - strokeWidth * 0.6f) * Math.sin(budgetTickAngle)).toFloat()
                    )
                    val tickEnd = Offset(
                        (center.x + (r + strokeWidth * 0.6f) * Math.cos(budgetTickAngle)).toFloat(),
                        (center.y + (r + strokeWidth * 0.6f) * Math.sin(budgetTickAngle)).toFloat()
                    )
                    drawLine(
                        color = Indigo500,
                        start = tickStart,
                        end = tickEnd,
                        strokeWidth = 3.dp.toPx()
                    )

                    // Active Progress Arc
                    val arcColor = if (overallRatio > 1.0f) ExpenseRed else IncomeGreen
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Indigo500,
                                if (overallRatio > 1.0f) ExpenseRed else IncomeGreen
                            )
                        ),
                        startAngle = startAngle,
                        sweepAngle = targetSweep.coerceAtLeast(1f),
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "${(overallRatio * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1.0).sp
                        ),
                        color = if (overallRatio > 1f) ExpenseRed else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (overallRatio > 1f) "BUDGET OVERRUN" else "OF TOTAL BUDGET",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = if (overallRatio > 1f) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${formatHuf(totalSpent)} spent out of ${formatHuf(totalBudget)} goal",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
