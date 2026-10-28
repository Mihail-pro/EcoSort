package md.ceiti.ecosort.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import md.ceiti.ecosort.EcoSortViewModel
import md.ceiti.ecosort.EcoUiState
import md.ceiti.ecosort.data.BinStatus
import md.ceiti.ecosort.ui.components.kg
import md.ceiti.ecosort.ui.history.HistoryScreen
import md.ceiti.ecosort.ui.login.LoginScreen
import md.ceiti.ecosort.ui.map.MapScreen
import md.ceiti.ecosort.ui.profile.ProfileScreen
import md.ceiti.ecosort.ui.theme.Emerald
import md.ceiti.ecosort.ui.theme.EmeraldLight
import md.ceiti.ecosort.ui.theme.ScreenGradient
import md.ceiti.ecosort.ui.theme.Slate950
import md.ceiti.ecosort.ui.theme.SlateBorder
import md.ceiti.ecosort.ui.theme.SlateCardHigh
import md.ceiti.ecosort.ui.theme.TextMuted
import md.ceiti.ecosort.ui.theme.TextPrimary

private enum class Tab(val label: String, val icon: ImageVector) {
    Map("Hartă", Icons.Rounded.Map),
    History("Istoric", Icons.Rounded.Insights),
    Profile("Profil", Icons.Rounded.Person),
}

@Composable
fun EcoSortApp(viewModel: EcoSortViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = state.user != null,
        transitionSpec = { (fadeIn(tween(450)) + scaleIn(initialScale = 0.96f)) togetherWith fadeOut(tween(250)) },
        label = "auth",
    ) { loggedIn ->
        if (loggedIn) {
            MainShell(state, viewModel)
        } else {
            LoginScreen(
                state = state,
                onLogin = viewModel::login,
                onQuickLogin = viewModel::quickLogin,
                onInputChanged = { if (state.loginError != null) viewModel.clearLoginError() },
            )
        }
    }
}

@Composable
private fun MainShell(state: EcoUiState, viewModel: EcoSortViewModel) {
    var tab by rememberSaveable { mutableStateOf(Tab.Map) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize().background(ScreenGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            snackbarHost = {
                SnackbarHost(snackbar) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = SlateCardHigh,
                        contentColor = TextPrimary,
                        actionColor = EmeraldLight,
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Slate950,
                    tonalElevation = 0.dp,
                    modifier = Modifier.drawBehind {
                        drawLine(SlateBorder, Offset.Zero, Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
                    },
                ) {
                    Tab.entries.forEach { t ->
                        val badge = when (t) {
                            Tab.Map -> state.count(BinStatus.FULL)
                            Tab.History -> state.history.size
                            Tab.Profile -> 0
                        }
                        NavigationBarItem(
                            selected = tab == t,
                            onClick = { tab = t },
                            icon = {
                                BadgedBox(badge = {
                                    if (badge > 0) {
                                        Badge(
                                            containerColor = if (t == Tab.Map) Color(0xFFEF4444) else Emerald,
                                            contentColor = Color.White,
                                        ) { Text("$badge") }
                                    }
                                }) { Icon(t.icon, contentDescription = t.label) }
                            },
                            label = { Text(t.label, fontWeight = if (tab == t) FontWeight.SemiBold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF022C22),
                                selectedTextColor = EmeraldLight,
                                indicatorColor = Emerald,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                            ),
                        )
                    }
                }
            },
        ) { padding ->
            AnimatedContent(
                targetState = tab,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                label = "tab",
            ) { current ->
                when (current) {
                    Tab.Map -> MapScreen(
                        state = state,
                        contentPadding = padding,
                        onSelectFloor = viewModel::selectFloor,
                        onOnlyFull = viewModel::setOnlyFull,
                        onToggleCategory = viewModel::toggleCategory,
                        onCollect = { binId ->
                            val record = viewModel.collect(binId) ?: return@MapScreen
                            scope.launch {
                                snackbar.currentSnackbarData?.dismiss()
                                val result = snackbar.showSnackbar(
                                    message = "✓ ${record.room} golit · +${record.kg.kg()} kg ${record.category.label}",
                                    actionLabel = "Anulează",
                                    duration = SnackbarDuration.Short,
                                )
                                if (result == SnackbarResult.ActionPerformed) viewModel.undoCollect(record.id)
                            }
                        },
                        onOpenProfile = { tab = Tab.Profile },
                    )
                    Tab.History -> HistoryScreen(state = state, contentPadding = padding, onGoToMap = { tab = Tab.Map })
                    Tab.Profile -> ProfileScreen(
                        state = state,
                        contentPadding = padding,
                        onLiveSensors = viewModel::setLiveSensors,
                        onSimulateFill = {
                            viewModel.simulateFill()
                            scope.launch { snackbar.showSnackbar("3 coșuri au fost umplute. Verifică harta!") }
                        },
                        onReset = {
                            viewModel.resetDemo()
                            scope.launch { snackbar.showSnackbar("Datele demo au fost resetate") }
                        },
                        onLogout = viewModel::logout,
                    )
                }
            }
        }
        // Solid band behind the transparent status bar so scrolled content doesn't run under the clock.
        Box(
            Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(Color(0xFF06302A)),
        )
    }
}
