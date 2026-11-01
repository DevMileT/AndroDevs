package com.androdevs.luckysevens.ui.navigation

/**
 *Project: LuckySevens
 *Created by: Milena
 *On: 28/10/2026 at 11:0
 *Year: 2026
 */

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.screens.GameScreen
import com.androdevs.luckysevens.ui.screens.HistoryScreen
import com.androdevs.luckysevens.ui.screens.MenuScreen
import com.androdevs.luckysevens.ui.screens.SettingsScreen
import com.androdevs.luckysevens.ui.screens.StatsScreen
import com.androdevs.luckysevens.ui.screens.WelcomeScreen
import com.androdevs.luckysevens.ui.state.GameError
import com.androdevs.luckysevens.ui.state.GameViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androdevs.luckysevens.LuckySevensApp
import com.androdevs.luckysevens.ui.state.HistoryViewModel
import com.androdevs.luckysevens.ui.state.ScreenViewModelFactory
import com.androdevs.luckysevens.ui.state.StatsViewModel
@Composable
fun MainNavigation(viewModel: GameViewModel) {
    val navController = rememberNavController()
    // El saldo y el bonus vienen del GameViewModel (SQLite) y se pasan a las pantallas que los
    // muestran: Menú y Juego (D-09). Si el estado cambia, estas pantallas se repintan.
    val uiState = viewModel.uiState

    // Aviso breve si algo falla al leer o guardar (sin mockup: diseño provisional).
    val context = LocalContext.current
    // El texto se resuelve aquí, en la composición, con stringResource: así sigue al idioma del móvil.
    val errorText = uiState.error?.let { stringResource(errorTextRes(it)) }
    LaunchedEffect(uiState.error) {
        if (errorText != null) {
            Toast.makeText(context, errorText, Toast.LENGTH_LONG).show()
            viewModel.onErrorShown()
        }
    }
    // Factory de los ViewModels de Historial y Estadísticas. Cada uno se crea al entrar en su
    // pantalla y se descarta al salir, así cada visita vuelve a consultar SQLite.
    val screenViewModelFactory = remember {
        ScreenViewModelFactory(context.applicationContext as LuckySevensApp)
    }
    // Vuelve al Menú sin sacarlo nunca de la pila (evita problemas con pulsaciones dobles).
    val goBackToMenu: () -> Unit = {
        navController.popBackStack(Routes.MENU, inclusive = false)
    }

    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate(Routes.MENU) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MENU) {
            MenuScreen(
                balance = uiState.balance,
                canClaimBonus = uiState.canClaimBonus,
                onPlayClick = { navController.navigate(Routes.GAME) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) },
                onStatsClick = { navController.navigate(Routes.STATS) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onClaimBonusClick = viewModel::onClaimBonusClicked
            )
        }
        composable(Routes.GAME) {
            GameScreen(
                uiState = uiState,
                onBackClick = goBackToMenu,
                onDecreaseBet = viewModel::onDecreaseBet,
                onIncreaseBet = viewModel::onIncreaseBet,
                onSpinClick = viewModel::onSpinClicked,
                onSpinAnimationFinished = viewModel::onSpinAnimationFinished,
                onResultDismissed = viewModel::onResultDismissed
            )
        }
        composable(Routes.HISTORY) {
            val historyViewModel: HistoryViewModel = viewModel(factory = screenViewModelFactory)
            HistoryScreen(
                uiState = historyViewModel.uiState,
                onFilterSelected = historyViewModel::onFilterSelected,
                onBackClick = goBackToMenu
            )
        }
        composable(Routes.STATS) {
            val statsViewModel: StatsViewModel = viewModel(factory = screenViewModelFactory)
            StatsScreen(uiState = statsViewModel.uiState, onBackClick = goBackToMenu)
        }
        composable(Routes.SETTINGS) { SettingsScreen(onBackClick = goBackToMenu) }
    }
}

private fun errorTextRes(error: GameError): Int = when (error) {
    GameError.LOAD_FAILED -> R.string.error_load_failed
    GameError.SPIN_FAILED -> R.string.error_spin_failed
    GameError.BONUS_FAILED -> R.string.error_bonus_failed
}