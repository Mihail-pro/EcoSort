package md.ceiti.ecosort.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Hardware
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Recycling
import androidx.compose.material.icons.rounded.WineBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import md.ceiti.ecosort.data.BinStatus
import md.ceiti.ecosort.data.Category
import md.ceiti.ecosort.ui.theme.EmeraldGradient
import md.ceiti.ecosort.ui.theme.SlateBorder
import md.ceiti.ecosort.ui.theme.SlateCard
import md.ceiti.ecosort.ui.theme.TextSecondary
import java.util.Locale

val Category.icon: ImageVector
    get() = when (this) {
        Category.PLASTIC -> Icons.Rounded.LocalDrink
        Category.PAPER -> Icons.Rounded.Description
        Category.GLASS -> Icons.Rounded.WineBar
        Category.METAL -> Icons.Rounded.Hardware
        Category.BATTERIES -> Icons.Rounded.BatteryChargingFull
    }

fun Double.kg(): String = String.format(Locale.US, if (this % 1.0 == 0.0) "%.0f" else "%.1f", this)

fun relativeTime(timestamp: Long, now: Long = System.currentTimeMillis()): String {
    val minutes = ((now - timestamp) / 60_000).coerceAtLeast(0)
    return when {
        minutes < 1 -> "chiar acum"
        minutes < 60 -> "acum $minutes min"
        minutes < 24 * 60 -> "acum ${minutes / 60} h"
        minutes < 48 * 60 -> "ieri"
        else -> "acum ${minutes / (24 * 60)} zile"
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    color: Color = SlateCard,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(color)
            .border(1.dp, SlateBorder, shape)
            .padding(contentPadding),
        content = content,
    )
}

@Composable
fun EcoLogo(size: Dp = 48.dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(EmeraldGradient),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Rounded.Recycling, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.56f))
    }
}

@Composable
fun CategoryBadge(category: Category, size: Dp = 44.dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(category.color.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(category.icon, contentDescription = category.label, tint = category.color, modifier = Modifier.size(size * 0.48f))
    }
}

@Composable
fun StatusChip(status: BinStatus, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(status.color.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(status.color))
        Text(status.label, color = status.color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, trailing: @Composable (() -> Unit)? = null) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        trailing?.invoke()
    }
}

@Composable
fun FillBar(fraction: Float, color: Color, modifier: Modifier = Modifier, height: Dp = 8.dp) {
    val animated by animateFloatAsState(fraction.coerceIn(0f, 1f), tween(700), label = "fill")
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.07f)),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(color.copy(alpha = 0.65f), color))),
        )
    }
}

@Composable
fun MutedText(text: String, modifier: Modifier = Modifier) {
    Text(text, color = TextSecondary, style = MaterialTheme.typography.bodySmall, modifier = modifier)
}
