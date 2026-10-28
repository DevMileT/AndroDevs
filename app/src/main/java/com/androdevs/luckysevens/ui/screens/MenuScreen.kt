package com.androdevs.luckysevens.ui.screens

/**
 *Project: LuckySevens
 *Created by: Milena
 *On: 28/10/2026 at 11:0
 *Year: 2026
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.androdevs.luckysevens.R

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onPlayClick) { Text(stringResource(R.string.menu_play)) }
        Button(onClick = onHistoryClick) { Text(stringResource(R.string.menu_history)) }
        Button(onClick = onStatsClick) { Text(stringResource(R.string.menu_stats)) }
        Button(onClick = onSettingsClick) { Text(stringResource(R.string.menu_settings)) }
    }
}