package com.androdevs.luckysevens.ui.screens

/**
 *Project: LuckySevens
 *Created by: Milena
 *On: 28/10/2026 at 11:0
 *Year: 2026
 */
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.androdevs.luckysevens.R

@Composable
fun HistoryScreen(balance: Int, onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = stringResource(R.string.menu_history),
        balance = balance,
        onBackClick = onBackClick
    )
}