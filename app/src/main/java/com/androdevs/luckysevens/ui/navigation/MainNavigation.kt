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

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

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
                onPlayClick = { navController.navigate(Routes.GAME) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) },
                onStatsClick = { navController.navigate(Routes.STATS) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.GAME) { GameScreen(onBackClick = goBackToMenu) }
        composable(Routes.HISTORY) { HistoryScreen(onBackClick = goBackToMenu) }
        composable(Routes.STATS) { StatsScreen(onBackClick = goBackToMenu) }
        composable(Routes.SETTINGS) { SettingsScreen(onBackClick = goBackToMenu) }
    }
}