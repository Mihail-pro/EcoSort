package md.ceiti.ecosort.ui.map

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import md.ceiti.ecosort.data.BinStatus
import md.ceiti.ecosort.data.TrashBin
import md.ceiti.ecosort.ui.components.CategoryBadge
import md.ceiti.ecosort.ui.components.FillBar
import md.ceiti.ecosort.ui.components.kg
import md.ceiti.ecosort.ui.components.relativeTime
import md.ceiti.ecosort.ui.theme.Emerald
import md.ceiti.ecosort.ui.theme.EmeraldLight
import md.ceiti.ecosort.ui.theme.TextMuted
import md.ceiti.ecosort.ui.theme.TextPrimary
import md.ceiti.ecosort.ui.theme.TextSecondary

private enum class QrState { Idle, Scanning, Valid }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectBottomSheet(
    bin: TrashBin,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var qr by rememberSaveable(bin.id) { mutableStateOf(QrState.Idle) }
    val canCollect = bin.status != BinStatus.EMPTY

    LaunchedEffect(qr) {
        if (qr == QrState.Scanning) {
            delay(1400)
            qr = QrState.Valid
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF111A2C),
        scrimColor = Color.Black.copy(alpha = 0.5f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryBadge(bin.category, size = 44.dp)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(bin.room, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${bin.floor} · ${bin.category.label}", color = TextMuted, fontSize = 13.sp)
                }
                Text(bin.status.label, color = bin.status.color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))

            // Sensor readings: plain numbers, no boxes.
            Row(verticalAlignment = Alignment.Bottom) {
                Reading(value = "${bin.fillPercentage}", unit = "%", label = "Umplere", color = bin.status.color, modifier = Modifier.weight(1f))
                Reading(value = bin.weightKg.kg(), unit = "kg", label = "Greutate", color = TextPrimary, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            FillBar(bin.fillPercentage / 100f, bin.status.color, height = 6.dp)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LiveDot()
                Spacer(Modifier.width(6.dp))
                Text(
                    "Măsurat automat · golit ${relativeTime(bin.lastCollectedAt)}",
                    color = TextMuted,
                    fontSize = 12.sp,
                )
            }

            Spacer(Modifier.height(28.dp))

            if (!canCollect) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = Emerald, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Coșul este gol, nu necesită colectare.", color = EmeraldLight, fontSize = 14.sp)
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Închide", color = TextSecondary)
                }
            } else {
                QrStep(qr = qr, code = bin.qrCode, onScan = { qr = QrState.Scanning })
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onConfirm,
                    enabled = qr == QrState.Valid,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald,
                        contentColor = Color(0xFF022C22),
                        disabledContainerColor = Color.White.copy(alpha = 0.06f),
                        disabledContentColor = TextMuted,
                    ),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                ) {
                    Icon(Icons.Rounded.DoneAll, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Marchează ca Golit", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun Reading(value: String, unit: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = color, fontSize = 30.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.8).sp)
            Text(" $unit", color = color.copy(alpha = 0.7f), fontSize = 15.sp, modifier = Modifier.padding(bottom = 5.dp))
        }
        Text(label, color = TextMuted, fontSize = 12.sp)
    }
}

/** QR validation as a single quiet line that changes state in place. */
@Composable
private fun QrStep(qr: QrState, code: String, onScan: () -> Unit) {
    AnimatedContent(
        targetState = qr,
        transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
        label = "qr",
    ) { state ->
        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .clickable(enabled = state == QrState.Idle, onClick = onScan)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (state) {
                QrState.Idle -> {
                    Icon(Icons.Rounded.QrCodeScanner, null, tint = EmeraldLight, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Simulează Scanare QR", color = EmeraldLight, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text(code, color = TextMuted, fontSize = 12.sp)
                }
                QrState.Scanning -> {
                    CircularProgressIndicator(color = Emerald, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Se scanează codul…", color = TextSecondary)
                }
                QrState.Valid -> {
                    Icon(Icons.Rounded.CheckCircle, null, tint = Emerald, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("QR validat", color = EmeraldLight, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text(code, color = TextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

/** Blinking green dot meaning "reading is live from the sensor". */
@Composable
private fun LiveDot() {
    val transition = rememberInfiniteTransition(label = "live")
    val a by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "liveAlpha",
    )
    Box(Modifier.size(6.dp).alpha(a).clip(CircleShape).background(Emerald))
}
