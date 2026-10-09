package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CanonGearCatalog
import com.example.data.model.CanonGearPreset
import com.example.data.model.ContractStatus
import com.example.data.model.GearCategory
import com.example.data.model.GearCondition
import com.example.data.model.PhotoContract
import com.example.data.model.PhotoGearItem
import com.example.data.model.PhotoTask
import com.example.data.model.TaskPriority
import com.example.ui.components.formatHuf
import com.example.ui.theme.Amber500
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MakBlue
import com.example.ui.viewmodel.FinanceViewModel

/**
 * Dialog for Adding Equipment with Full Canon Database presets and custom price override.
 */
@Composable
fun AddPhotoGearWithCanonDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        brand: String,
        model: String,
        category: GearCategory,
        serialNumber: String,
        purchaseDate: String,
        purchasePrice: Double,
        condition: GearCondition,
        location: String,
        isInsured: Boolean,
        notes: String
    ) -> Unit
) {
    var isFromCanonDb by remember { mutableStateOf(true) }
    var canonSearchQuery by remember { mutableStateOf("") }
    var selectedCanonCategory by remember { mutableStateOf<GearCategory?>(null) }
    var selectedCanonPreset by remember { mutableStateOf<CanonGearPreset?>(CanonGearCatalog.items.first()) }

    var name by remember { mutableStateOf(selectedCanonPreset?.name ?: "") }
    var brand by remember { mutableStateOf("Canon") }
    var model by remember { mutableStateOf(selectedCanonPreset?.modelCode ?: "") }
    var category by remember { mutableStateOf(selectedCanonPreset?.category ?: GearCategory.CAMERA_BODY) }
    var serialNumber by remember { mutableStateOf("") }
    var purchasePriceInput by remember { mutableStateOf(selectedCanonPreset?.suggestedPriceHuf?.toInt()?.toString() ?: "1899000") }
    var condition by remember { mutableStateOf(GearCondition.MINT) }
    var location by remember { mutableStateOf("Main Pelican 1510") }
    var isInsured by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf(selectedCanonPreset?.description ?: "") }

    val filteredCanonItems = remember(canonSearchQuery, selectedCanonCategory) {
        CanonGearCatalog.items.filter { item ->
            val matchQuery = canonSearchQuery.isBlank() ||
                item.name.contains(canonSearchQuery, ignoreCase = true) ||
                item.modelCode.contains(canonSearchQuery, ignoreCase = true) ||
                item.subcategory.contains(canonSearchQuery, ignoreCase = true)
            val matchCategory = selectedCanonCategory == null || item.category == selectedCanonCategory
            matchQuery && matchCategory
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register Equipment", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Source Selector: Canon Database vs Manual Custom
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isFromCanonDb) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isFromCanonDb) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isFromCanonDb = true }
                                .testTag("tab_canon_database")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isFromCanonDb) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Canon Database",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isFromCanonDb) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isFromCanonDb) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (!isFromCanonDb) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isFromCanonDb = false }
                                .testTag("tab_custom_gear")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = if (!isFromCanonDb) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Custom / Other",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (!isFromCanonDb) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Canon Database Selector Strip
                if (isFromCanonDb) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "SELECT FROM OFFICIAL CANON CATALOG",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                OutlinedTextField(
                                    value = canonSearchQuery,
                                    onValueChange = { canonSearchQuery = it },
                                    placeholder = { Text("Search Canon models (e.g. R5 II, 24-70, EL-5)...") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    modifier = Modifier.fillMaxWidth().testTag("canon_search_input"),
                                    singleLine = true
                                )

                                // Category chips for Canon
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedCanonCategory == null,
                                        onClick = { selectedCanonCategory = null },
                                        label = { Text("All Canon (${CanonGearCatalog.items.size})") }
                                    )
                                    listOf(
                                        GearCategory.CAMERA_BODY to "Bodies",
                                        GearCategory.LENS to "Lenses",
                                        GearCategory.LIGHTING to "Lighting",
                                        GearCategory.ACCESSORY to "Accessories"
                                    ).forEach { (cat, label) ->
                                        FilterChip(
                                            selected = selectedCanonCategory == cat,
                                            onClick = { selectedCanonCategory = if (selectedCanonCategory == cat) null else cat },
                                            label = { Text(label) }
                                        )
                                    }
                                }

                                // Quick picker list (up to 8 items scrollable)
                                Text(
                                    "Tap to load specs & suggested pricing:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    filteredCanonItems.take(6).forEach { preset ->
                                        val isSelected = selectedCanonPreset?.name == preset.name
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedCanonPreset = preset
                                                    name = preset.name
                                                    brand = "Canon"
                                                    model = preset.modelCode
                                                    category = preset.category
                                                    purchasePriceInput = preset.suggestedPriceHuf.toInt().toString()
                                                    notes = preset.description
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = preset.name,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = preset.subcategory,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = IncomeGreen.copy(alpha = 0.15f)
                                                ) {
                                                    Text(
                                                        text = formatHuf(preset.suggestedPriceHuf),
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = IncomeGreen,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

                // Section 1: Equipment Details
                item {
                    Text(
                        text = "1. EQUIPMENT IDENTIFICATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Equipment Name *") },
                        placeholder = { Text("e.g. Canon EOS R5 Mark II") },
                        modifier = Modifier.fillMaxWidth().testTag("gear_name_input"),
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand *") },
                            modifier = Modifier.weight(1f).testTag("gear_brand_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Model Code") },
                            modifier = Modifier.weight(1f).testTag("gear_model_input"),
                            singleLine = true
                        )
                    }
                }

                // Category Chips
                item {
                    Text(
                        text = "Category:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GearCategory.entries.forEach { cat ->
                            val isSel = category == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = cat.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Section 2: Valuation & Custom Pricing
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "2. VALUATION & PRICING",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (selectedCanonPreset != null && isFromCanonDb) {
                                    Text(
                                        text = "Canon Ref: ${formatHuf(selectedCanonPreset!!.suggestedPriceHuf)}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = IncomeGreen
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = purchasePriceInput,
                                onValueChange = { purchasePriceInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Your Equipment Value / Purchase Price (HUF) *") },
                                trailingIcon = { Text("Ft", modifier = Modifier.padding(end = 12.dp), fontWeight = FontWeight.Bold) },
                                modifier = Modifier.fillMaxWidth().testTag("gear_price_input"),
                                singleLine = true
                            )

                            // Quick button to reload Canon suggested price or round numbers
                            if (selectedCanonPreset != null && isFromCanonDb) {
                                OutlinedButton(
                                    onClick = {
                                        purchasePriceInput = selectedCanonPreset!!.suggestedPriceHuf.toInt().toString()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reset to Canon Suggested Price (${formatHuf(selectedCanonPreset!!.suggestedPriceHuf)})", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                // Section 3: Condition & Storage
                item {
                    Text(
                        text = "3. CONDITION & STORAGE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GearCondition.entries.forEach { cond ->
                            val isSel = condition == cond
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.clickable { condition = cond }
                            ) {
                                Text(
                                    text = cond.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Storage Location / Bag") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it },
                            label = { Text("Serial Number") },
                            placeholder = { Text("Optional") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                // Section 4: Insurance & Protection
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = isInsured, onCheckedChange = { isInsured = it })
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Covered under gear insurance policy", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                Text("Include in Studio Insured Asset Valuation", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes & Specs") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = purchasePriceInput.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onConfirm(name, brand, model, category, serialNumber, "2026-09-30", price, condition, location, isInsured, notes)
                    }
                },
                modifier = Modifier.testTag("confirm_add_gear_btn")
            ) {
                Text("Register Equipment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

/**
 * Dialog for Editing existing Equipment in the Gear Inventory.
 */
@Composable
fun EditPhotoGearDialog(
    gear: PhotoGearItem,
    onDismiss: () -> Unit,
    onConfirm: (PhotoGearItem) -> Unit
) {
    var name by remember { mutableStateOf(gear.name) }
    var brand by remember { mutableStateOf(gear.brand) }
    var model by remember { mutableStateOf(gear.model) }
    var category by remember { mutableStateOf(gear.category) }
    var serialNumber by remember { mutableStateOf(gear.serialNumber) }
    var purchasePriceInput by remember { mutableStateOf(gear.purchasePrice.toInt().toString()) }
    var condition by remember { mutableStateOf(gear.condition) }
    var location by remember { mutableStateOf(gear.location) }
    var isInsured by remember { mutableStateOf(gear.isInsured) }
    var notes by remember { mutableStateOf(gear.notes) }

    // Match Canon Catalog preset if available
    val matchingCanonPreset = remember(name, model) {
        CanonGearCatalog.items.firstOrNull {
            it.name.equals(name, ignoreCase = true) || it.modelCode.equals(model, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Equipment Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (matchingCanonPreset != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Official Canon Model Recognized", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                    Text("Catalog Suggested Price: ${formatHuf(matchingCanonPreset.suggestedPriceHuf)}", style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(
                                    onClick = { purchasePriceInput = matchingCanonPreset.suggestedPriceHuf.toInt().toString() }
                                ) {
                                    Text("Apply Price")
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "1. IDENTIFICATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Equipment Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_gear_name_input"),
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Model Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                // Category Chips
                item {
                    Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GearCategory.entries.forEach { cat ->
                            val isSel = category == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.clickable { category = cat }
                            ) {
                                Text(
                                    text = cat.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Valuation
                item {
                    Text(
                        text = "2. VALUATION & REPLACEMENT VALUE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = purchasePriceInput,
                        onValueChange = { purchasePriceInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Equipment Value / Purchase Price (HUF) *") },
                        trailingIcon = { Text("Ft", modifier = Modifier.padding(end = 12.dp), fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth().testTag("edit_gear_price_input"),
                        singleLine = true
                    )
                }

                // Condition
                item {
                    Text(
                        text = "3. PHYSICAL CONDITION & STORAGE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GearCondition.entries.forEach { cond ->
                            val isSel = condition == cond
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.clickable { condition = cond }
                            ) {
                                Text(
                                    text = cond.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Storage Location / Bag") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it },
                            label = { Text("Serial Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = isInsured, onCheckedChange = { isInsured = it })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Covered under gear insurance policy", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes & Specs") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = purchasePriceInput.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onConfirm(
                            gear.copy(
                                name = name,
                                brand = brand,
                                model = model,
                                category = category,
                                serialNumber = serialNumber,
                                purchasePrice = price,
                                condition = condition,
                                location = location,
                                isInsured = isInsured,
                                notes = notes
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_edit_gear_btn")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

/**
 * Dialog for Editing existing Booking / Client Contract.
 */
@Composable
fun EditContractDialog(
    contract: PhotoContract,
    onDismiss: () -> Unit,
    onConfirm: (PhotoContract) -> Unit
) {
    var title by remember { mutableStateOf(contract.title) }
    var clientName by remember { mutableStateOf(contract.clientName) }
    var clientEmail by remember { mutableStateOf(contract.clientEmail) }
    var clientPhone by remember { mutableStateOf(contract.clientPhone) }
    var shootDate by remember { mutableStateOf(contract.shootDate) }
    var shootStartTime by remember { mutableStateOf(contract.shootStartTime) }
    var shootEndTime by remember { mutableStateOf(contract.shootEndTime) }
    var shootLocation by remember { mutableStateOf(contract.shootLocation) }
    var totalAmountInput by remember { mutableStateOf(contract.totalAmount.toInt().toString()) }
    var depositPaidInput by remember { mutableStateOf(contract.depositPaid.toInt().toString()) }
    var status by remember { mutableStateOf(contract.status) }
    var deliverables by remember { mutableStateOf(contract.deliverables) }
    var deliverableDeadline by remember { mutableStateOf(contract.deliverableDeadline) }
    var notes by remember { mutableStateOf(contract.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Booking Details", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("1. CLIENT & SHOOT INFO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Shoot / Project Title *") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_contract_title_input"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client Full Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_contract_client_input"),
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = clientEmail,
                            onValueChange = { clientEmail = it },
                            label = { Text("Client Email") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = clientPhone,
                            onValueChange = { clientPhone = it },
                            label = { Text("Client Phone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Text("2. DATE & SCHEDULE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = shootDate,
                        onValueChange = { shootDate = it },
                        label = { Text("Shoot Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = shootStartTime,
                            onValueChange = { shootStartTime = it },
                            label = { Text("Start Time") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = shootEndTime,
                            onValueChange = { shootEndTime = it },
                            label = { Text("End Time") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = shootLocation,
                        onValueChange = { shootLocation = it },
                        label = { Text("Shoot Location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("3. PRICING & DEPOSIT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = totalAmountInput,
                            onValueChange = { totalAmountInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Total Fee (HUF) *") },
                            modifier = Modifier.weight(1f).testTag("edit_contract_total_input"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = depositPaidInput,
                            onValueChange = { depositPaidInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Deposit Paid") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Text("Stage / Status:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ContractStatus.entries.forEach { st ->
                            val isSel = status == st
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.clickable { status = st }
                            ) {
                                Text(
                                    text = st.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text("4. DELIVERABLES & DEADLINE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = deliverables,
                        onValueChange = { deliverables = it },
                        label = { Text("Deliverables (e.g. 40 retouched high-res photos)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = deliverableDeadline,
                        onValueChange = { deliverableDeadline = it },
                        label = { Text("Delivery Deadline") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes & Client Requests") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val total = totalAmountInput.toDoubleOrNull() ?: 0.0
                    val deposit = depositPaidInput.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && clientName.isNotBlank()) {
                        onConfirm(
                            contract.copy(
                                title = title,
                                clientName = clientName,
                                clientEmail = clientEmail,
                                clientPhone = clientPhone,
                                shootDate = shootDate,
                                shootStartTime = shootStartTime,
                                shootEndTime = shootEndTime,
                                shootLocation = shootLocation,
                                totalAmount = total,
                                depositPaid = deposit,
                                status = status,
                                deliverables = deliverables,
                                deliverableDeadline = deliverableDeadline,
                                notes = notes
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_edit_contract_btn")
            ) {
                Text("Save Booking")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

/**
 * Dialog for Editing existing Studio Task.
 */
@Composable
fun EditStudioTaskDialog(
    task: PhotoTask,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit,
    onConfirm: (PhotoTask) -> Unit
) {
    val contracts by viewModel.contracts.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf(task.title) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var notes by remember { mutableStateOf(task.notes) }
    var selectedContractId by remember { mutableStateOf(task.contractId) }
    var selectedContractTitle by remember { mutableStateOf(task.contractTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Studio Task", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title *") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_task_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_task_due_input"),
                    singleLine = true
                )

                // Quick Date Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val today = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()) }
                    listOf("Today" to today, "Tomorrow" to "2026-10-10", "Next Week" to "2026-10-16").forEach { (lbl, dt) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (dueDate == dt) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { dueDate = dt }
                        ) {
                            Text(lbl, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Text("Priority:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskPriority.entries.forEach { p ->
                        val isSel = priority == p
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier.clickable { priority = p }
                        ) {
                            Text(
                                text = p.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Shoot Prep", "Editing", "Client Review", "Gear Maintenance", "General").forEach { cat ->
                        val isSel = category == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier.clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                if (contracts.isNotEmpty()) {
                    Text("Link to Shoot / Booking:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedContractId == null) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                selectedContractId = null
                                selectedContractTitle = null
                            }
                        ) {
                            Text("None", modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall)
                        }
                        contracts.forEach { contract ->
                            val isSel = selectedContractId == contract.id
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    selectedContractId = contract.id
                                    selectedContractTitle = contract.title
                                }
                            ) {
                                Text(contract.title, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes & Details") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            task.copy(
                                title = title,
                                dueDate = dueDate,
                                priority = priority,
                                category = category,
                                notes = notes,
                                contractId = selectedContractId,
                                contractTitle = selectedContractTitle
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_edit_task_btn")
            ) {
                Text("Save Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
