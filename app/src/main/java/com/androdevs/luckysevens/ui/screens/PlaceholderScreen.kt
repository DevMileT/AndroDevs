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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.androdevs.luckysevens.R

@Composable
fun PlaceholderScreen(title: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.placeholder_under_construction))
        Button(onClick = onBackClick) {
            Text(stringResource(R.string.placeholder_back))
        }
    }
}