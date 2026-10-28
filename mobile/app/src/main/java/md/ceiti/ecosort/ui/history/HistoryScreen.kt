package md.ceiti.ecosort.ui.history

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Scale
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import md.ceiti.ecosort.EcoUiState
import md.ceiti.ecosort.data.BinStatus
import md.ceiti.ecosort.data.Category
import md.ceiti.ecosort.data.CollectionRecord
import md.ceiti.ecosort.ui.components.CategoryBadge
import md.ceiti.ecosort.ui.components.FillBar
import md.ceiti.ecosort.ui.components.GlassCard
import md.ceiti.ecosort.ui.components.kg
import md.ceiti.ecosort.ui.theme.Emerald
import md.ceiti.ecosort.ui.theme.EmeraldLight
import md.ceiti.ecosort.ui.theme.OverlineStyle
import md.ceiti.ecosort.ui.theme.TextMuted
import md.ceiti.ecosort.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(state: EcoUiState, contentPadding: PaddingValues, onGoToMap: () -> Unit) {
    val kgByCategory = state.kgByCategory
    val total = state.totalKg

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(Modifier.padding(horizontal = 4.dp)) {
                Text("Sesiunea curentă · ${today()}", color = TextSecondary, fontSize = 14.sp)
                Text("Istoric & Statistici", style = MaterialTheme.typography.headlineMedium)
            }
        }

        item { TotalsCard(total = total, collections = state.history.size, fullLeft = state.count(BinStatus.FULL)) }

        item {
            GlassCard(Modifier.fillMaxWidth()) {
                Text("Distribuție pe categorii", style = MaterialTheme.typography.titleMedium)
                Text("Kg colectate în această sesiune", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Donut(values = kgByCategory, total = total, modifier = Modifier.size(132.dp))
                    Spacer(Modifier.width(18.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Category.entries.forEach { c ->
                            val value = kgByCategory[c] ?: 0.0
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(10.dp).clip(CircleShape).background(c.color))
                                Spacer(Modifier.width(8.dp))
                                Text(c.label, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Text(
                                    if (total > 0) "${(value / total * 100).toInt()}%" else "–",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                val max = kgByCategory.values.maxOrNull()?.takeIf { it > 0 } ?: 1.0
                Category.entries.forEach { c ->
                    val value = kgByCategory[c] ?: 0.0
                    Column(Modifier.padding(vertical = 5.dp)) {
                        Row {
                            Text(c.label, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                            Text("${value.kg()} kg", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(5.dp))
                        FillBar((value / max).toFloat(), c.color, height = 7.dp)
                    }
                }
            }
        }

        item {
            Row(Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Înregistrări", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text("${state.history.size}", color = TextSecondary)
            }
        }

        if (state.history.isEmpty()) {
            item { EmptyHistory(onGoToMap) }
        } else {
            items(state.history, key = { it.id }) { record ->
                RecordRow(record, Modifier.animateItem())
            }
        }
    }
}

@Composable
private fun TotalsCard(total: Double, collections: Int, fullLeft: Int) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF065F46), Color(0xFF047857), Color(0xFF10B981))))
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("TOTAL COLECTAT", style = OverlineStyle, color = Color.White.copy(alpha = 0.75f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(total.kg(), color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1.5).sp)
                    Text(" kg", color = Color.White.copy(alpha = 0.8f), fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
            Box(
                Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Scale, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniStat("Colectări", "$collections", Modifier.weight(1f))
            MiniStat("Pline rămase", "$fullLeft", Modifier.weight(1f))
            MiniStat("CO₂ evitat", "~${(total * 1.5).kg()} kg", Modifier.weight(1f))
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1)
        Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun Donut(values: Map<Category, Double>, total: Double, modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(total) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 18.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(inset, inset)
            drawArc(Color.White.copy(alpha = 0.06f), 0f, 360f, false, topLeft, arcSize, style = Stroke(stroke))
            if (total <= 0) return@Canvas
            val gap = 4f
            var start = -90f
            Category.entries.forEach { c ->
                val sweep = ((values[c] ?: 0.0) / total * 360f).toFloat()
                if (sweep > 0f) {
                    val visibleSweep = (sweep - gap).coerceAtLeast(0.5f) * progress.value
                    drawArc(c.color, start + gap / 2, visibleSweep, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
                }
                start += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(total.kg(), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text("kg total", color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun RecordRow(record: CollectionRecord, modifier: Modifier = Modifier) {
    GlassCard(modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryBadge(record.category)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Coș ${record.category.label} · ${record.room}", fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(
                    "${record.collectorName} · ${record.floor} · ${time(record.timestamp)}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "${record.kg.kg()} kg",
                color = EmeraldLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Emerald.copy(alpha = 0.14f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
private fun EmptyHistory(onGoToMap: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(28.dp)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(Emerald.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Inventory2, null, tint = Emerald, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Nicio colectare încă", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                "Golește un coș plin de pe hartă și va apărea aici.",
                color = TextMuted,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onGoToMap) {
                Icon(Icons.Rounded.Map, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Deschide harta")
            }
        }
    }
}

private val roLocale = Locale.forLanguageTag("ro-RO")
private fun today(): String = SimpleDateFormat("d MMMM yyyy", roLocale).format(Date())
private fun time(timestamp: Long): String = SimpleDateFormat("HH:mm", roLocale).format(Date(timestamp))
