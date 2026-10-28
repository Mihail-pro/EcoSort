package md.ceiti.ecosort.ui.map

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import md.ceiti.ecosort.data.BinStatus
import md.ceiti.ecosort.data.FloorPlan
import md.ceiti.ecosort.data.TrashBin
import md.ceiti.ecosort.ui.components.icon
import md.ceiti.ecosort.ui.theme.Emerald
import md.ceiti.ecosort.ui.theme.Slate900
import kotlin.math.roundToInt

private const val MAX_ZOOM = 4f
private val PinTouchTarget = 40.dp

/** Background the blueprint filter produces for the light-grey plan surround. */
private val BlueprintBackground = Color(0xFF282F3D)
private val PaperBackground = Color(0xFFD9D9D9)

/**
 * Maps the light grey CEITI plans onto a dark slate "blueprint":
 * out = light + (dark - light) * luminance, so walls become light lines on a dark floor.
 */
private val BlueprintFilter: ColorFilter = run {
    val dark = floatArrayOf(11f, 18f, 32f)
    val light = floatArrayOf(203f, 213f, 225f)
    val lum = floatArrayOf(0.299f, 0.587f, 0.114f)
    val m = FloatArray(20)
    for (c in 0..2) {
        val k = (dark[c] - light[c]) / 255f
        for (i in 0..2) m[c * 5 + i] = k * lum[i]
        m[c * 5 + 4] = light[c]
    }
    m[18] = 1f
    ColorFilter.colorMatrix(ColorMatrix(m))
}

@Composable
fun FloorMap(
    plan: FloorPlan,
    bins: List<TrashBin>,
    darkMap: Boolean,
    onToggleDarkMap: () -> Unit,
    onBinClick: (TrashBin) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Shared pulse clock so every FULL pin breathes in sync.
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulse = pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "pulse",
    )

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .aspectRatio(plan.aspectRatio)
            .clip(RoundedCornerShape(24.dp))
            .background(if (darkMap) BlueprintBackground else PaperBackground),
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        // Zoom state resets whenever another floor is shown.
        var scale by remember(plan.name) { mutableFloatStateOf(1f) }
        var offset by remember(plan.name) { mutableStateOf(Offset.Zero) }

        fun clamp(o: Offset, s: Float) = Offset(
            o.x.coerceIn(widthPx - widthPx * s, 0f),
            o.y.coerceIn(heightPx - heightPx * s, 0f),
        )

        fun zoomAround(focus: Offset, newScale: Float) {
            val s = newScale.coerceIn(1f, MAX_ZOOM)
            offset = clamp(focus - (focus - offset) * (s / scale), s)
            scale = s
        }

        // Same critically-damped spring for both, so button zooms glide and pinches stay snappy.
        val animatedScale by animateFloatAsState(scale, spring(dampingRatio = 1f, stiffness = 900f), label = "scale")
        val animatedOffset by animateOffsetAsState(offset, spring(dampingRatio = 1f, stiffness = 900f), label = "offset")

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(plan.name, widthPx, heightPx) {
                    // Pinch always zooms; one-finger drags only pan once zoomed in,
                    // otherwise they fall through so the screen can still scroll.
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        do {
                            val event = awaitPointerEvent()
                            val pointers = event.changes.count { it.pressed }
                            if (event.changes.none { it.isConsumed } && (pointers >= 2 || scale > 1.01f)) {
                                val zoom = event.calculateZoom()
                                val pan = event.calculatePan()
                                val centroid = event.calculateCentroid(useCurrent = false)
                                val s = (scale * zoom).coerceIn(1f, MAX_ZOOM)
                                offset = clamp(centroid - (centroid - offset) * (s / scale) + pan, s)
                                scale = s
                                event.changes.forEach { if (it.positionChanged()) it.consume() }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
                .pointerInput(plan.name, widthPx, heightPx) {
                    detectTapGestures(onDoubleTap = { tap ->
                        if (scale > 1.5f) {
                            scale = 1f; offset = Offset.Zero
                        } else {
                            zoomAround(tap, 2.5f)
                        }
                    })
                },
        ) {
            Image(
                painter = painterResource(plan.image),
                contentDescription = "Schița ${plan.name}",
                contentScale = ContentScale.FillBounds,
                colorFilter = if (darkMap) BlueprintFilter else null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0f, 0f)
                        scaleX = animatedScale
                        scaleY = animatedScale
                        translationX = animatedOffset.x
                        translationY = animatedOffset.y
                    },
            )

            // Pins live outside the scaled layer so they keep a constant, tappable size.
            val touchPx = with(density) { PinTouchTarget.toPx() }
            bins.forEach { bin ->
                Box(
                    Modifier.offset {
                        IntOffset(
                            (bin.xRatio * widthPx * animatedScale + animatedOffset.x - touchPx / 2).roundToInt(),
                            (bin.yRatio * heightPx * animatedScale + animatedOffset.y - touchPx / 2).roundToInt(),
                        )
                    },
                ) {
                    BinPin(bin = bin, pulse = pulse, darkMap = darkMap, onClick = { onBinClick(bin) })
                }
            }
        }

        // One slim, translucent control strip instead of separate framed buttons.
        val fg = if (darkMap) Color.White else Slate900
        Column(
            Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (darkMap) Slate900.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.75f)),
        ) {
            MapButton(if (darkMap) Icons.Rounded.LightMode else Icons.Rounded.DarkMode, "Schimbă stilul hărții", fg, onToggleDarkMap)
            MapButton(Icons.Rounded.Add, "Mărește", fg) { zoomAround(Offset(widthPx / 2, heightPx / 2), scale * 1.6f) }
            MapButton(Icons.Rounded.Remove, "Micșorează", fg) { zoomAround(Offset(widthPx / 2, heightPx / 2), scale / 1.6f) }
            if (scale > 1.01f) {
                MapButton(Icons.Rounded.CenterFocusStrong, "Resetează zoom", fg) { scale = 1f; offset = Offset.Zero }
            }
        }
    }
}

@Composable
private fun MapButton(icon: ImageVector, description: String, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .size(34.dp)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint.copy(alpha = 0.85f), modifier = Modifier.size(17.dp))
    }
}

/**
 * Minimal pin: a category-coloured dot. Only FULL bins draw attention (bigger + red pulse);
 * empty ones fade back so the plan stays readable.
 */
@Composable
private fun BinPin(bin: TrashBin, pulse: State<Float>, darkMap: Boolean, onClick: () -> Unit) {
    val isFull = bin.status == BinStatus.FULL
    val dotSize by animateFloatAsState(if (isFull) 16f else 12f, tween(300, easing = FastOutSlowInEasing), label = "pin")
    val outline = if (darkMap) Slate900 else Color.White

    Box(
        Modifier
            .size(PinTouchTarget)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .semantics { contentDescription = "${bin.room}, ${bin.category.label}, ${bin.fillPercentage}%" }
            .drawBehind {
                val center = Offset(size.width / 2, size.height / 2)
                if (isFull) {
                    // Single expanding ring => soft alarm pulse.
                    val p = pulse.value
                    drawCircle(
                        color = BinStatus.FULL.color.copy(alpha = 0.45f * (1f - p)),
                        radius = dotSize.dp.toPx() / 2 * (1f + 1.6f * p),
                        center = center,
                    )
                }
                val r = dotSize.dp.toPx() / 2
                drawCircle(outline, radius = r + 2.dp.toPx(), center = center)
                drawCircle(
                    color = bin.category.color.copy(alpha = if (bin.status == BinStatus.EMPTY) 0.55f else 1f),
                    radius = r,
                    center = center,
                )
            },
    )
}
