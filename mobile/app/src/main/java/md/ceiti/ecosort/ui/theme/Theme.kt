package md.ceiti.ecosort.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// EcoSort Web palette
val EmeraldDeep = Color(0xFF004D40)
val EmeraldDark = Color(0xFF047857)
val Emerald = Color(0xFF10B981)
val EmeraldLight = Color(0xFF6EE7B7)
val Slate950 = Color(0xFF0A1120)
val Slate900 = Color(0xFF0F172A)
val SlateCard = Color(0xFF141F33)
val SlateCardHigh = Color(0xFF1B2840)
val SlateBorder = Color(0x1AFFFFFF)
val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)
val Danger = Color(0xFFEF4444)
val Warning = Color(0xFFF59E0B)

val ScreenGradient = Brush.verticalGradient(
    0f to Color(0xFF06302A),
    0.32f to Slate900,
    1f to Slate950,
)

val EmeraldGradient = Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF10B981), Color(0xFF34D399)))

private val EcoColors = darkColorScheme(
    primary = Emerald,
    onPrimary = Color(0xFF022C22),
    primaryContainer = EmeraldDeep,
    onPrimaryContainer = EmeraldLight,
    secondary = EmeraldLight,
    background = Slate900,
    onBackground = TextPrimary,
    surface = SlateCard,
    onSurface = TextPrimary,
    surfaceVariant = SlateCardHigh,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLow = SlateCard,
    surfaceContainer = SlateCard,
    surfaceContainerHigh = SlateCardHigh,
    surfaceContainerHighest = SlateCardHigh,
    outline = Color(0xFF334155),
    outlineVariant = SlateBorder,
    error = Danger,
    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = Slate900,
    inversePrimary = EmeraldDark,
)

private val base = Typography()

private val EcoTypography = Typography(
    displaySmall = base.displaySmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp),
    headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.2).sp),
    titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelSmall = base.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp),
)

/** Tiny all-caps label used above inputs and sections, like the web app. */
val OverlineStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)

@Composable
fun EcoSortTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EcoColors, typography = EcoTypography, content = content)
}
