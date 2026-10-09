package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.FinanceViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhotoContract
import com.example.data.model.PhotoIdeaItem
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.IncomeGreen

fun parseHexColor(hex: String, fallback: Color = Color.Gray): Color {
    return try {
        val clean = hex.trim().removePrefix("#")
        if (clean.length == 6) {
            Color(android.graphics.Color.parseColor("#$clean"))
        } else if (clean.length == 8) {
            Color(android.graphics.Color.parseColor("#$clean"))
        } else {
            fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

val PRESET_PALETTES = listOf(
    "Warm Sunset Glow" to "#F4A261,#E76F51,#2A9D8F,#264653,#E9C46A",
    "Moody Film Noir" to "#1B1B1E,#373F51,#58A4B0,#D8DBE2,#A9BCD0",
    "Pastel Ethereal" to "#FDE2E4,#FFCAD4,#B5E2FA,#EDDEA4,#F7A072",
    "Boho Terracotta" to "#9B2226,#AE2012,#BB3E03,#CA6702,#EE9B00",
    "Emerald Forest" to "#2D6A4F,#40916C,#52B788,#74C69D,#D8F3DC",
    "Cobalt Twilight" to "#03045E,#0077B6,#00B4D8,#90E0EF,#CAF0F8"
)

@Composable
fun PinterestIdeaCard(
    idea: PhotoIdeaItem,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paletteColors = remember(idea.colorPaletteHexes) {
        idea.colorPaletteHexes.split(",").map { parseHexColor(it) }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (idea.isPinned) Amber500.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("idea_card_${idea.id}")
    ) {
        Column {
            // Pinterest-style color palette strip / mood bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
            ) {
                if (paletteColors.isNotEmpty()) {
                    paletteColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .background(color)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = idea.boardName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = idea.moodTheme,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = idea.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(32.dp).testTag("pin_btn_${idea.id}")
                    ) {
                        Icon(
                            imageVector = if (idea.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (idea.isPinned) "Unpin" else "Pin",
                            tint = if (idea.isPinned) Amber500 else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (idea.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = idea.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (idea.cameraSettings.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = idea.cameraSettings,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (idea.linkedContractTitle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Linked: ${idea.linkedContractTitle}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                            color = IncomeGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (idea.tags.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = idea.tags,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoIdeaDetailDialog(
    idea: PhotoIdeaItem,
    onDismiss: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    onCreateTask: () -> Unit
) {
    val paletteColors = remember(idea.colorPaletteHexes) {
        idea.colorPaletteHexes.split(",").map { parseHexColor(it) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Large Color Palette Swatch Banner
                item {
                    Column {
                        Text(
                            text = "COLOR PALETTE & MOOD: ${idea.moodTheme.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            paletteColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .background(color)
                                )
                            }
                        }
                    }
                }

                // Board & Tags
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Board: ${idea.boardName}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (idea.tags.isNotBlank()) {
                            Text(
                                text = idea.tags,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Full concept description
                if (idea.description.isNotBlank()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "CREATIVE CONCEPT & LIGHTING SETUP",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = idea.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Camera & Lens Specs
                if (idea.cameraSettings.isNotBlank() || idea.recommendedGear.isNotBlank()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "RECOMMENDED GEAR & EXPOSURE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (idea.cameraSettings.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Settings: ${idea.cameraSettings}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                                if (idea.recommendedGear.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Gear: ${idea.recommendedGear}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Linked Contract
                if (idea.linkedContractTitle != null) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IncomeGreen.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Assigned Shoot: ${idea.linkedContractTitle}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = IncomeGreen
                                )
                            }
                        }
                    }
                }

                // One-tap action: Turn idea into Shoot Task
                item {
                    Button(
                        onClick = {
                            onCreateTask()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("idea_create_task_btn")
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Shoot Preparation Checklist")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onTogglePin) {
                Text(if (idea.isPinned) "Unpin Idea" else "Pin to Top")
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Delete Idea", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
fun AddPhotoIdeaDialog(
    contracts: List<PhotoContract>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String, String, Long?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var boardName by remember { mutableStateOf("Weddings") }
    var selectedPaletteIndex by remember { mutableIntStateOf(0) }
    var tags by remember { mutableStateOf("#goldenhour #backlight #flare") }
    var description by remember { mutableStateOf("") }
    var cameraSettings by remember { mutableStateOf("85mm f/1.4 • 1/500s • ISO 100") }
    var recommendedGear by remember { mutableStateOf("Sony A7 IV + 85mm GM") }
    var selectedContractId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Photo Idea / Moodboard") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Idea / Concept Title *") },
                        placeholder = { Text("e.g. Sunset Vineyard Silhouette & Flare") },
                        modifier = Modifier.fillMaxWidth().testTag("idea_title_input")
                    )
                }

                item {
                    Text("Moodboard Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Weddings", "Portraits", "Editorial & Fashion", "Lighting & Strobes", "Posing", "Architecture & Travel").forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (boardName == cat) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { boardName = cat }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (boardName == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Color Palette & Aesthetic:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PRESET_PALETTES.forEachIndexed { idx, (themeName, hexes) ->
                            val isSelected = selectedPaletteIndex == idx
                            val colors = hexes.split(",").map { parseHexColor(it) }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPaletteIndex = idx }
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .width(90.dp)
                                            .height(20.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    ) {
                                        colors.forEach { Box(modifier = Modifier.weight(1f).height(20.dp).background(it)) }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = themeName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Pose, Mood & Lighting Setup Notes") },
                        placeholder = { Text("e.g. Place sun directly behind hair, softbox 45 degrees") },
                        modifier = Modifier.fillMaxWidth().testTag("idea_desc_input")
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cameraSettings,
                            onValueChange = { cameraSettings = it },
                            label = { Text("Settings (f/1.4, ISO)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = recommendedGear,
                            onValueChange = { recommendedGear = it },
                            label = { Text("Suggested Gear") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags (#sunset #portrait)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (contracts.isNotEmpty()) {
                    item {
                        Text("Link to Upcoming Shoot (Optional):", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        contracts.forEach { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedContractId = if (selectedContractId == c.id) null else c.id
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.RadioButton(
                                    selected = selectedContractId == c.id,
                                    onClick = {
                                        selectedContractId = if (selectedContractId == c.id) null else c.id
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(c.title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val (moodTheme, hexes) = PRESET_PALETTES[selectedPaletteIndex]
                        val contractTitle = contracts.find { it.id == selectedContractId }?.title
                        onConfirm(title, boardName, tags, description, cameraSettings, recommendedGear, hexes, moodTheme, selectedContractId, contractTitle)
                    }
                },
                modifier = Modifier.testTag("confirm_idea_btn")
            ) {
                Text("Save Idea")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun StudioIdeasTabContent(
    viewModel: FinanceViewModel,
    onIdeaClick: (PhotoIdeaItem) -> Unit,
    onAddIdeaClick: () -> Unit
) {
    val ideas by viewModel.photoIdeas.collectAsStateWithLifecycle()
    var selectedBoard by remember { mutableStateOf("All") }

    val filteredIdeas = remember(ideas, selectedBoard) {
        when (selectedBoard) {
            "All" -> ideas
            "Pinned" -> ideas.filter { it.isPinned }
            else -> ideas.filter { it.boardName.equals(selectedBoard, ignoreCase = true) }
        }
    }

    val pinnedCount = remember(ideas) { ideas.count { it.isPinned } }
    val linkedCount = remember(ideas) { ideas.count { it.linkedContractId != null } }

    val boards = remember(ideas) {
        val standard = listOf("All", "Pinned", "Weddings", "Portraits", "Editorial & Fashion", "Lighting & Strobes", "Posing", "Architecture & Travel")
        val custom = ideas.map { it.boardName }.filterNot { standard.contains(it) }.distinct()
        standard + custom
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Idea Board Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pinterest Idea Board",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Save creative concepts, lighting setups, color palettes & pose inspirations.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = onAddIdeaClick,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_idea_header_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Idea", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Total Saved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${ideas.size} Ideas", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Pinned", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$pinnedCount Pinned", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Amber500)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Linked Shoots", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$linkedCount Shoots", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = IncomeGreen)
                            }
                        }
                    }
                }
            }
        }

        // Board Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                boards.forEach { b ->
                    val isSelected = selectedBoard == b
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedBoard = b },
                        label = {
                            val count = when (b) {
                                "All" -> ideas.size
                                "Pinned" -> pinnedCount
                                else -> ideas.count { it.boardName.equals(b, ignoreCase = true) }
                            }
                            Text(if (count > 0 && b != "All") "$b ($count)" else b)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Idea Cards List
        if (filteredIdeas.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedBoard == "All") "No Ideas Saved Yet" else "No Ideas in '$selectedBoard'",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Save lighting setups, posing inspiration, and moodboard color palettes just like in Pinterest.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddIdeaClick,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("empty_add_idea_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Your First Idea")
                        }
                    }
                }
            }
        } else {
            items(filteredIdeas, key = { it.id }) { idea ->
                PinterestIdeaCard(
                    idea = idea,
                    onClick = { onIdeaClick(idea) },
                    onTogglePin = { viewModel.togglePinPhotoIdea(idea) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
