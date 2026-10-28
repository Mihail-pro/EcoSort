package md.ceiti.ecosort.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import md.ceiti.ecosort.EcoUiState
import md.ceiti.ecosort.data.BinStatus
import md.ceiti.ecosort.ui.components.GlassCard
import md.ceiti.ecosort.ui.components.kg
import md.ceiti.ecosort.ui.theme.Danger
import md.ceiti.ecosort.ui.theme.Emerald
import md.ceiti.ecosort.ui.theme.EmeraldGradient
import md.ceiti.ecosort.ui.theme.EmeraldLight
import md.ceiti.ecosort.ui.theme.OverlineStyle
import md.ceiti.ecosort.ui.theme.SlateBorder
import md.ceiti.ecosort.ui.theme.SlateCardHigh
import md.ceiti.ecosort.ui.theme.TextMuted
import md.ceiti.ecosort.ui.theme.TextSecondary
import md.ceiti.ecosort.ui.theme.Warning

@Composable
fun ProfileScreen(
    state: EcoUiState,
    contentPadding: PaddingValues,
    onLiveSensors: (Boolean) -> Unit,
    onSimulateFill: () -> Unit,
    onReset: () -> Unit,
    onLogout: () -> Unit,
) {
    val user = state.user ?: return
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Profil Demo", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 4.dp))

        GlassCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        Modifier.size(72.dp).clip(CircleShape).background(EmeraldGradient),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(user.initials, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Emerald)
                            .border(3.dp, Color(0xFF141F33), CircleShape),
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(user.role, color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "@${user.username}",
                        color = EmeraldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Emerald.copy(alpha = 0.14f))
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("${state.history.size}", "Colectări", Emerald, Modifier.weight(1f))
                StatTile(state.totalKg.kg(), "Kg adunate", EmeraldLight, Modifier.weight(1f))
                StatTile("${state.count(BinStatus.FULL)}", "Pline", Danger, Modifier.weight(1f))
            }
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Text("TURA DE AZI", style = OverlineStyle, color = TextMuted)
            Spacer(Modifier.height(10.dp))
            InfoLine(Icons.Rounded.Schedule, "Program", "08:00 – 16:00")
            InfoLine(Icons.Rounded.Apartment, "Zonă alocată", "CEITI · Etajele 1–3")
            InfoLine(Icons.Rounded.Sensors, "Puncte monitorizate", "${state.bins.size} coșuri")
        }

        GlassCard(Modifier.fillMaxWidth()) {
            Text("INSTRUMENTE DEMO", style = OverlineStyle, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Senzori live (simulare)", fontWeight = FontWeight.SemiBold)
                    Text("La fiecare 5 s un coș aleator se umple", color = TextSecondary, fontSize = 12.sp)
                }
                Switch(
                    checked = state.liveSensors,
                    onCheckedChange = onLiveSensors,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Emerald,
                        uncheckedTrackColor = SlateCardHigh,
                        uncheckedBorderColor = SlateBorder,
                        uncheckedThumbColor = TextSecondary,
                    ),
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DemoAction(Icons.Rounded.AutoAwesome, "Umple 3 coșuri", Warning, onSimulateFill, Modifier.weight(1f))
                DemoAction(Icons.Rounded.RestartAlt, "Resetează demo", EmeraldLight, onReset, Modifier.weight(1f))
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Info, null, tint = TextMuted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "EcoSort Mobile · Beta 0.1. Prototip demo: fără backend, toate datele sunt simulate local și se resetează la repornire.",
                color = TextMuted,
                fontSize = 12.sp,
            )
        }

        OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Danger.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Danger),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Icon(Icons.AutoMirrored.Rounded.Logout, null)
            Spacer(Modifier.width(8.dp))
            Text("Deconectare", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun StatTile(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SlateCardHigh)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun InfoLine(icon: ImageVector, label: String, value: String) {
    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(Emerald.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = Emerald, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun DemoAction(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(tint.copy(alpha = 0.10f))
            .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, color = tint, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
