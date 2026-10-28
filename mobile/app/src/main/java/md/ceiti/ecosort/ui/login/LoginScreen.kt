package md.ceiti.ecosort.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import md.ceiti.ecosort.EcoUiState
import md.ceiti.ecosort.data.MockData
import md.ceiti.ecosort.data.User
import md.ceiti.ecosort.ui.components.EcoLogo
import md.ceiti.ecosort.ui.theme.Danger
import md.ceiti.ecosort.ui.theme.Emerald
import md.ceiti.ecosort.ui.theme.EmeraldDark
import md.ceiti.ecosort.ui.theme.OverlineStyle
import md.ceiti.ecosort.ui.theme.Slate900
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

private val CardText = Slate900
private val CardMuted = Color(0xFF64748B)
private val FieldFill = Color(0xFFF0FDF4)

@Composable
fun LoginScreen(
    state: EcoUiState,
    onLogin: (String, String) -> Unit,
    onQuickLogin: (User) -> Unit,
    onInputChanged: () -> Unit,
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current

    // Shake the card on every failed attempt.
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.loginErrorCount) {
        if (state.loginErrorCount == 0) return@LaunchedEffect
        for (x in listOf(18f, -16f, 12f, -8f, 4f, 0f)) shake.animateTo(x, tween(55))
    }

    fun submit() {
        focus.clearFocus()
        onLogin(username, password)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF00332B), Color(0xFF015C48), Color(0xFF059669)), start = Offset.Zero, end = Offset.Infinite)),
    ) {
        FloatingBubbles(Modifier.fillMaxSize())

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(12.dp))
            Box(contentAlignment = Alignment.Center) {
                Box(Modifier.size(104.dp).clip(CircleShape).background(Emerald.copy(alpha = 0.18f)))
                EcoLogo(size = 76.dp)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("EcoSort", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "BETA",
                    color = Color(0xFF022C22),
                    style = OverlineStyle,
                    modifier = Modifier.clip(CircleShape).background(Color(0xFF6EE7B7)).padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
            Text("Colectare selectivă · CEITI", color = Color.White.copy(alpha = 0.75f), fontSize = 15.sp)
            Spacer(Modifier.height(28.dp))

            Column(
                Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .offset { IntOffset(shake.value.roundToInt(), 0) }
                    .shadow(32.dp, RoundedCornerShape(28.dp), ambientColor = Color.Black, spotColor = Color.Black)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .padding(24.dp),
            ) {
                Text("AUTENTIFICARE", style = OverlineStyle, color = Emerald)
                Spacer(Modifier.height(4.dp))
                Text("Bine ai revenit 👋", color = CardText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Fiecare lucrător intră cu contul propriu.", color = CardMuted, fontSize = 14.sp)
                Spacer(Modifier.height(20.dp))

                FieldLabel("NUME UTILIZATOR")
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; onInputChanged() },
                    placeholder = { Text("ex: sofer1") },
                    leadingIcon = { Icon(Icons.Rounded.Person, null) },
                    singleLine = true,
                    isError = state.loginError != null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(14.dp),
                    colors = cardFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                FieldLabel("PAROLĂ")
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; onInputChanged() },
                    placeholder = { Text("Introdu parola") },
                    leadingIcon = { Icon(Icons.Rounded.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (showPassword) "Ascunde parola" else "Arată parola",
                            )
                        }
                    },
                    singleLine = true,
                    isError = state.loginError != null,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    shape = RoundedCornerShape(14.dp),
                    colors = cardFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                )

                AnimatedVisibility(
                    visible = state.loginError != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    Row(
                        Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Danger.copy(alpha = 0.08f))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.ErrorOutline, null, tint = Danger, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(state.loginError.orEmpty(), color = Danger, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = ::submit,
                    enabled = !state.isLoggingIn && username.isNotBlank() && password.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald,
                        contentColor = Color.White,
                        disabledContainerColor = Emerald.copy(alpha = 0.35f),
                        disabledContentColor = Color.White.copy(alpha = 0.9f),
                    ),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                ) {
                    if (state.isLoggingIn) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Autentificare", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, modifier = Modifier.size(20.dp))
                    }
                }

                Row(Modifier.padding(vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE2E8F0))
                    Text("  sau  ", color = CardMuted, fontSize = 12.sp)
                    HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE2E8F0))
                }

                OutlinedButton(
                    onClick = { focus.clearFocus(); onQuickLogin(MockData.users.first()) },
                    enabled = !state.isLoggingIn,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Emerald),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldDark),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) {
                    Icon(Icons.Rounded.Bolt, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Autentificare Rapidă Demo", fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(16.dp))
                Text("CONTURI DE TEST · parola123", style = OverlineStyle, color = CardMuted)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MockData.users.forEach { user ->
                        DemoAccountChip(
                            user = user,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                username = user.username
                                password = MockData.DEMO_PASSWORD
                                onInputChanged()
                            },
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Demo offline · toate datele sunt simulate local",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, style = OverlineStyle, color = CardMuted, modifier = Modifier.padding(bottom = 6.dp, start = 2.dp))
}

@Composable
private fun cardFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = CardText,
    unfocusedTextColor = CardText,
    focusedContainerColor = FieldFill,
    unfocusedContainerColor = FieldFill,
    errorContainerColor = Color(0xFFFEF2F2),
    focusedBorderColor = Emerald,
    unfocusedBorderColor = Color(0xFFBBF7D0),
    errorBorderColor = Danger,
    cursorColor = EmeraldDark,
    focusedLeadingIconColor = EmeraldDark,
    unfocusedLeadingIconColor = CardMuted,
    errorLeadingIconColor = Danger,
    focusedTrailingIconColor = CardMuted,
    unfocusedTrailingIconColor = CardMuted,
    errorTrailingIconColor = CardMuted,
    errorTextColor = CardText,
    focusedPlaceholderColor = CardMuted.copy(alpha = 0.7f),
    unfocusedPlaceholderColor = CardMuted.copy(alpha = 0.7f),
    errorPlaceholderColor = CardMuted.copy(alpha = 0.7f),
)

@Composable
private fun DemoAccountChip(user: User, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFD1FAE5), RoundedCornerShape(12.dp))
            .background(FieldFill)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(28.dp).clip(CircleShape).background(Emerald),
            contentAlignment = Alignment.Center,
        ) {
            Text(user.initials, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(user.username, color = CardText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(user.name, color = CardMuted, fontSize = 11.sp, maxLines = 1)
        }
    }
}

/** Soft translucent circles drifting slowly, like the web login background. */
@Composable
private fun FloatingBubbles(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "bubbles")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14_000, easing = LinearEasing), RepeatMode.Restart),
        label = "t",
    )
    // x, y (relative), radius (dp), drift amplitude (dp), phase
    val bubbles = remember {
        listOf(
            floatArrayOf(0.08f, 0.10f, 36f, 10f, 0.0f),
            floatArrayOf(0.22f, 0.07f, 22f, 8f, 0.3f),
            floatArrayOf(0.86f, 0.14f, 30f, 12f, 0.6f),
            floatArrayOf(0.62f, 0.30f, 14f, 6f, 0.1f),
            floatArrayOf(0.12f, 0.46f, 48f, 14f, 0.8f),
            floatArrayOf(0.94f, 0.52f, 40f, 10f, 0.4f),
            floatArrayOf(0.74f, 0.70f, 18f, 8f, 0.2f),
            floatArrayOf(0.30f, 0.86f, 56f, 16f, 0.5f),
            floatArrayOf(0.88f, 0.92f, 28f, 10f, 0.9f),
            floatArrayOf(0.05f, 0.97f, 34f, 9f, 0.7f),
        )
    }
    Canvas(modifier) {
        bubbles.forEach { (x, y, r, amp, phase) ->
            val angle = 2 * PI * (t + phase)
            val center = Offset(
                x * size.width + (sin(angle) * amp.dp.toPx()).toFloat(),
                y * size.height + (sin(angle * 2 + 1) * amp.dp.toPx() * 0.6f).toFloat(),
            )
            drawCircle(Color.White.copy(alpha = 0.07f), radius = r.dp.toPx(), center = center)
        }
    }
}
