package md.ceiti.ecosort.ui.map

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import md.ceiti.ecosort.EcoUiState
import md.ceiti.ecosort.data.BinStatus
import md.ceiti.ecosort.data.Category
import md.ceiti.ecosort.data.MockData
import md.ceiti.ecosort.data.TrashBin
import md.ceiti.ecosort.ui.components.CategoryBadge
import md.ceiti.ecosort.ui.components.FillBar
import md.ceiti.ecosort.ui.components.kg
import md.ceiti.ecosort.ui.components.relativeTime
import md.ceiti.ecosort.ui.theme.Danger
import md.ceiti.ecosort.ui.theme.Emerald
import md.ceiti.ecosort.ui.theme.EmeraldGradient
import md.ceiti.ecosort.ui.theme.EmeraldLight
import md.ceiti.ecosort.ui.theme.TextMuted
import md.ceiti.ecosort.ui.theme.TextPrimary
import md.ceiti.ecosort.ui.theme.TextSecondary

/** Very soft surface used instead of bordered cards, to keep the screen airy. */
private val SoftSurface = Color.White.copy(alpha = 0.04f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    state: EcoUiState,
    contentPadding: PaddingValues,
    onSelectFloor: (String) -> Unit,
    onOnlyFull: (Boolean) -> Unit,
    onToggleCategory: (Category) -> Unit,
    onCollect: (binId: Int) -> Unit,
    onOpenProfile: () -> Unit,
) {
    var selectedBinId by rememberSaveable { mutableStateOf<Int?>(null) }
    var darkMap by rememberSaveable { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val plan = MockData.floors.first { it.name == state.selectedFloor }
    val visible = state.visibleBins
    val listBins = visible.sortedWith(compareByDescending<TrashBin> { it.fillPercentage }.thenBy { it.room })
    val fullTotal = state.count(BinStatus.FULL)

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Header(
                greeting = "Bună, ${state.user?.firstName ?: ""} 👋",
                initials = state.user?.initials ?: "?",
                fullTotal = fullTotal,
                onAlertClick = {
                    state.bins.filter { it.status == BinStatus.FULL }.minByOrNull { it.lastCollectedAt }
                        ?.let { onSelectFloor(it.floor) }
                    onOnlyFull(true)
                },
                onAvatarClick = onOpenProfile,
            )
        }

        item {
            FloorTabs(
                floors = MockData.floors.map { it.name },
                selected = state.selectedFloor,
                hasFull = { state.countOn(it, BinStatus.FULL) > 0 },
                onSelect = onSelectFloor,
            )
        }

        item {
            Crossfade(
                targetState = plan,
                animationSpec = tween(350),
                label = "floor",
                modifier = Modifier.padding(horizontal = 16.dp),
            ) { p ->
                FloorMap(
                    plan = p,
                    bins = state.bins.filter { b -> b.floor == p.name && b in visible },
                    darkMap = darkMap,
                    onToggleDarkMap = { darkMap = !darkMap },
                    onBinClick = { selectedBinId = it.id },
                )
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item {
                    FilterPill(
                        label = "Doar pline",
                        dot = Danger,
                        active = state.onlyFull,
                        activeColor = Danger,
                        onClick = { onOnlyFull(!state.onlyFull) },
                    )
                }
                items(Category.entries) { category ->
                    FilterPill(
                        label = category.label,
                        dot = category.color,
                        active = category !in state.hiddenCategories,
                        activeColor = TextPrimary,
                        onClick = { onToggleCategory(category) },
                    )
                }
            }
        }

        item {
            Row(Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Puncte de colectare", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text("${listBins.size}", color = TextMuted, fontSize = 13.sp)
            }
        }

        if (listBins.isEmpty()) {
            item {
                Text(
                    if (state.onlyFull) "Niciun coș plin pe acest etaj 🎉" else "Niciun coș pentru filtrele alese",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
        }

        items(listBins, key = { it.id }) { bin ->
            BinRow(
                bin = bin,
                onClick = { selectedBinId = bin.id },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .animateItem(),
            )
        }
    }

    val selected = state.bins.firstOrNull { it.id == selectedBinId }
    if (selected != null) {
        CollectBottomSheet(
            bin = selected,
            sheetState = sheetState,
            onDismiss = { selectedBinId = null },
            onConfirm = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    selectedBinId = null
                    onCollect(selected.id)
                }
            },
        )
    }
}

@Composable
private fun Header(
    greeting: String,
    initials: String,
    fullTotal: Int,
    onAlertClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(greeting, color = TextSecondary, fontSize = 14.sp)
            Text("Harta coșurilor", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            // One quiet status line replaces the old alert card.
            Row(
                Modifier
                    .clip(CircleShape)
                    .clickable(enabled = fullTotal > 0, onClick = onAlertClick)
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(if (fullTotal > 0) Danger else Emerald))
                Spacer(Modifier.width(7.dp))
                Text(
                    when (fullTotal) {
                        0 -> "Toate coșurile sunt în regulă"
                        1 -> "1 coș de golit"
                        else -> "$fullTotal coșuri de golit"
                    },
                    color = if (fullTotal > 0) TextPrimary else EmeraldLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (fullTotal > 0) {
                    Text("  · vezi", color = TextMuted, fontSize = 14.sp)
                }
            }
        }
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(EmeraldGradient)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FloorTabs(
    floors: List<String>,
    selected: String,
    hasFull: (String) -> Boolean,
    onSelect: (String) -> Unit,
) {
    Row(
        Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        floors.forEach { floor ->
            val isSelected = floor == selected
            val bg by animateColorAsState(if (isSelected) Emerald.copy(alpha = 0.16f) else Color.Transparent, tween(250), label = "floorBg")
            val fg by animateColorAsState(if (isSelected) EmeraldLight else TextSecondary, tween(250), label = "floorFg")
            Row(
                Modifier
                    .clip(CircleShape)
                    .background(bg)
                    .clickable { onSelect(floor) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(floor, color = fg, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium, fontSize = 14.sp)
                if (hasFull(floor)) {
                    Spacer(Modifier.width(6.dp))
                    Box(Modifier.size(6.dp).clip(CircleShape).background(Danger))
                }
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, dot: Color, active: Boolean, activeColor: Color, onClick: () -> Unit) {
    val bg by animateColorAsState(if (active) activeColor.copy(alpha = 0.10f) else Color.Transparent, label = "pillBg")
    Row(
        Modifier
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(if (active) dot else dot.copy(alpha = 0.3f)))
        Spacer(Modifier.width(7.dp))
        Text(label, color = if (active) activeColor else TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BinRow(bin: TrashBin, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SoftSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryBadge(bin.category, size = 38.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(bin.room, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "${bin.weightKg.kg()} kg · golit ${relativeTime(bin.lastCollectedAt)}",
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.width(52.dp), horizontalAlignment = Alignment.End) {
            Text("${bin.fillPercentage}%", color = bin.status.color, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(5.dp))
            FillBar(bin.fillPercentage / 100f, bin.status.color, height = 4.dp)
        }
        Spacer(Modifier.width(4.dp))
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = TextMuted.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
    }
}
