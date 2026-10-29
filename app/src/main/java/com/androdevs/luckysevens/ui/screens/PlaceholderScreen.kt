package com.androdevs.luckysevens.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.components.BalanceTopBar

@Composable
fun PlaceholderScreen(title: String, balance: Int, onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        BalanceTopBar(balance = balance, onBackClick = onBackClick)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.placeholder_under_construction))
        }
    }
}