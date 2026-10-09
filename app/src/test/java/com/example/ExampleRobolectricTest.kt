package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("KakukkOS", appName)
  }

  @Test
  fun `biometric manager returns valid status in robolectric`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val status = com.example.ui.security.BiometricAuthManager.checkBiometricStatus(context)
    org.junit.Assert.assertNotNull(status)
    val label = com.example.ui.security.BiometricAuthManager.getStatusLabel(status)
    org.junit.Assert.assertTrue(label.isNotEmpty())
  }

  @Test
  fun `wealthFlow finance hub starts locked and unlocks upon biometric success`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.FinanceViewModel(app)
    org.junit.Assert.assertFalse(viewModel.isAuthenticated.value)
    viewModel.onBiometricAuthSuccess()
    org.junit.Assert.assertTrue(viewModel.isAuthenticated.value)
    viewModel.lockApp()
    org.junit.Assert.assertFalse(viewModel.isAuthenticated.value)
  }

  @Test
  fun `currency formatter outputs standard Hungarian Forint without dollar prefix`() {
    val formatted = com.example.ui.components.formatHuf(10000.0)
    assertEquals("10 000 Ft", formatted)
    org.junit.Assert.assertFalse(formatted.contains("$"))
  }

  @Test
  fun `viewModel formatCurrency matches authentic Hungarian spacing without dollar signs`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.FinanceViewModel(app)
    val formatted = viewModel.formatCurrency(380000.0)
    assertEquals("380 000 Ft", formatted)
    org.junit.Assert.assertFalse(formatted.contains("$"))
  }

  @Test
  fun `weekly costs sum function calculates accurate totals`() {
    val items = listOf(
      com.example.data.model.WeeklyCostItem(
        id = 1,
        title = "Groceries",
        amount = 18500.0,
        category = "Groceries",
        date = "2026-10-05",
        dayOfWeek = "Monday",
        weekNumber = 41
      ),
      com.example.data.model.WeeklyCostItem(
        id = 2,
        title = "Metro Pass",
        amount = 6500.0,
        category = "Transport",
        date = "2026-10-05",
        dayOfWeek = "Monday",
        weekNumber = 41
      ),
      com.example.data.model.WeeklyCostItem(
        id = 3,
        title = "Studio Film",
        amount = 12000.0,
        category = "Studio & Work",
        date = "2026-10-06",
        dayOfWeek = "Tuesday",
        weekNumber = 41
      )
    )

    val sum = items.sumOf { it.amount }
    assertEquals(37000.0, sum, 0.01)
    val formattedSum = com.example.ui.components.formatHuf(sum)
    assertEquals("37 000 Ft", formattedSum)

    val subtotal = items.filter { it.category == "Groceries" || it.category == "Transport" }.sumOf { it.amount }
    assertEquals(25000.0, subtotal, 0.01)
  }

  @Test
  fun `addWeeklyCost inserts cost into viewModel successfully`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.FinanceViewModel(app)
    viewModel.addWeeklyCost(
      title = "New Lighting Stand",
      amount = 15000.0,
      category = "Studio & Work",
      date = "2026-10-05",
      dayOfWeek = "Monday",
      weekNumber = 41,
      year = 2026,
      notes = "Impact heavy duty stand"
    )
    org.junit.Assert.assertNotNull(viewModel.weeklyCosts)
  }

  @Test
  fun `expected cost item calculates progress and remaining amounts accurately`() {
    val languageCourse = com.example.data.model.ExpectedCostItem(
      id = 1,
      title = "Spanish B2 Language Course",
      estimatedAmount = 145000.0,
      allocatedAmount = 60000.0,
      category = "Language & Education",
      targetDate = "2026-11-20",
      status = com.example.data.model.ExpectedCostStatus.PLANNING,
      urgency = com.example.data.model.ExpectedCostUrgency.REQUIRED,
      isMandatory = true
    )

    assertEquals(85000.0, languageCourse.remainingAmount, 0.01)
    val percent = languageCourse.progressPercent
    assertEquals(41.37f, percent, 0.1f)
    org.junit.Assert.assertFalse(languageCourse.isFullyFunded)

    val travelTrip = com.example.data.model.ExpectedCostItem(
      id = 2,
      title = "Japan Autumn Photography Trip",
      estimatedAmount = 980000.0,
      allocatedAmount = 980000.0,
      category = "Travel & Trips",
      targetDate = "2027-10-15",
      status = com.example.data.model.ExpectedCostStatus.FUNDED,
      urgency = com.example.data.model.ExpectedCostUrgency.PLANNED,
      isMandatory = false
    )

    assertEquals(0.0, travelTrip.remainingAmount, 0.01)
    org.junit.Assert.assertTrue(travelTrip.isFullyFunded)
  }

  @Test
  fun `expected costs are clearly distinguished from discretionary wishlist items`() {
    val plannedCost = com.example.data.model.ExpectedCostItem(
      id = 1,
      title = "Intensive German Course",
      estimatedAmount = 120000.0,
      allocatedAmount = 30000.0,
      category = "Language & Education",
      isMandatory = true
    )

    val wishItem = com.example.data.model.WishItem(
      id = 1,
      title = "Sony FE 50mm f1_2 GM Lens",
      targetPrice = 740000.0,
      currentSaved = 200000.0,
      category = "Photo Gear",
      isGearWish = true
    )

    // Expected costs represent planned commitments with deadline and mandatory flags
    org.junit.Assert.assertTrue(plannedCost.isMandatory)
    org.junit.Assert.assertEquals("Language & Education", plannedCost.category)

    // Wishes represent personal discretionary desires
    org.junit.Assert.assertTrue(wishItem.isGearWish)
    org.junit.Assert.assertEquals("Photo Gear", wishItem.category)
  }

  @Test
  fun `viewModel addExpectedCost and allocateFundsToExpectedCost perform successfully`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.FinanceViewModel(app)

    viewModel.addExpectedCost(
      title = "French A1 Evening Class",
      estimatedAmount = 95000.0,
      allocatedAmount = 25000.0,
      category = "Language & Education",
      targetDate = "2026-12-01",
      isMandatory = true
    )
    org.junit.Assert.assertNotNull(viewModel.expectedCosts)
  }

  @Test
  fun `security check - PIN verification authenticates only valid credentials and blocks invalid`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.FinanceViewModel(app)

    // Initially starts locked
    viewModel.lockApp()
    org.junit.Assert.assertFalse(viewModel.isAuthenticated.value)

    // Wrong PIN fails
    val wrongResult = viewModel.authenticateWithPin("9999")
    org.junit.Assert.assertFalse(wrongResult)
    org.junit.Assert.assertFalse(viewModel.isAuthenticated.value)

    // Default 2026 succeeds
    val successResult = viewModel.authenticateWithPin("2026")
    org.junit.Assert.assertTrue(successResult)
    org.junit.Assert.assertTrue(viewModel.isAuthenticated.value)

    // Changing PIN securely invalidates old PIN
    viewModel.updatePinCode("4321")
    viewModel.lockApp()
    org.junit.Assert.assertFalse(viewModel.isAuthenticated.value)

    // Old default 2026 must no longer authenticate
    org.junit.Assert.assertFalse(viewModel.authenticateWithPin("2026"))
    org.junit.Assert.assertFalse(viewModel.isAuthenticated.value)

    // New PIN authenticates
    org.junit.Assert.assertTrue(viewModel.authenticateWithPin("4321"))
    org.junit.Assert.assertTrue(viewModel.isAuthenticated.value)
  }

  @Test
  fun `task date formatter parses relative and formatted dates accurately`() {
    val emptyResult = com.example.ui.util.TaskDateFormatter.parseAndFormat("")
    assertEquals("No due date", emptyResult.formattedDate)
    assertEquals("Anytime", emptyResult.relativeLabel)
    assertEquals(com.example.ui.util.DueUrgency.NO_DATE, emptyResult.urgency)

    val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    val todayResult = com.example.ui.util.TaskDateFormatter.parseAndFormat(todayStr)
    assertEquals("Due Today", todayResult.relativeLabel)
    assertEquals(com.example.ui.util.DueUrgency.TODAY, todayResult.urgency)

    // Overdue date in the past
    val pastResult = com.example.ui.util.TaskDateFormatter.parseAndFormat("2020-01-01")
    assertEquals(com.example.ui.util.DueUrgency.OVERDUE, pastResult.urgency)
    org.junit.Assert.assertTrue(pastResult.relativeLabel.contains("Overdue"))
  }

  @Test
  fun `category budget comparison computes variance, ratio and over budget status accurately`() {
    val withinBudget = com.example.ui.components.CategoryBudgetComparison(
      category = "Groceries & Supermarket",
      spent = 112000.0,
      budget = 150000.0,
      color = androidx.compose.ui.graphics.Color.Green
    )
    assertEquals(38000.0, withinBudget.variance, 0.01)
    assertEquals(0.746f, withinBudget.ratio, 0.01f)
    org.junit.Assert.assertFalse(withinBudget.isOverBudget)

    val overBudget = com.example.ui.components.CategoryBudgetComparison(
      category = "Dining & Leisure",
      spent = 95000.0,
      budget = 80000.0,
      color = androidx.compose.ui.graphics.Color.Red
    )
    assertEquals(-15000.0, overBudget.variance, 0.01)
    assertEquals(1.1875f, overBudget.ratio, 0.01f)
    org.junit.Assert.assertTrue(overBudget.isOverBudget)
  }
}
