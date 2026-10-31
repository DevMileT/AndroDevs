package com.androdevs.luckysevens.ui.navigation

/**
 *Project: LuckySevens
 *Created by: Milena
 *On: 28/10/2026 at 11:0
 *Year: 2026
 */

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androdevs.luckysevens.ui.screens.GameScreen
import com.androdevs.luckysevens.ui.screens.HistoryScreen
import com.androdevs.luckysevens.ui.screens.MenuScreen
import com.androdevs.luckysevens.ui.screens.SettingsScreen
import com.androdevs.luckysevens.ui.screens.StatsScreen
import com.androdevs.luckysevens.ui.screens.WelcomeScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// TEMPORAL (Fase 4): saldo de prueba fijo. Se sustituye por el saldo real
// del GameViewModel en las Fases 5 y 6.
private const val TEST_BALANCE = 1000
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    // TEMPORAL (Fase 4): estado de prueba del saldo y del bonus. Se sustituye por el
    // GameViewModel y la base de datos en las Fases 5 y 6.
    var testBalance by remember { mutableStateOf(TEST_BALANCE) }
    var testBonusAvailable by remember { mutableStateOf(true) }

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
                balance = testBalance,
                canClaimBonus = testBonusAvailable,
                onPlayClick = { navController.navigate(Routes.GAME) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) },
                onStatsClick = { navController.navigate(Routes.STATS) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onClaimBonusClick = {
                    testBalance += 50
                    testBonusAvailable = false
                }
            )
        }
        composable(Routes.GAME) { GameScreen(balance = testBalance, onBackClick = goBackToMenu) }
        composable(Routes.HISTORY) { HistoryScreen(onBackClick = goBackToMenu) }
        composable(Routes.STATS) { StatsScreen(onBackClick = goBackToMenu) }
        composable(Routes.SETTINGS) { SettingsScreen(onBackClick = goBackToMenu) }
    }
}