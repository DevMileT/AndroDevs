package com.androdevs.luckysevens.ui.components

/**
 *Project: LuckySevens
 *Created by: Milena
 *On: 29/10/2026 at 01:0
 *Year: 2026
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import java.text.NumberFormat
import androidx.compose.foundation.layout.size

// Barra superior reutilizable (D-09): flecha ← a la izquierda y saldo a la derecha.
// No sabe de dónde viene el saldo ni adónde vuelve la flecha: los recibe por parámetro.
@Composable
fun BalanceTopBar(
    balance: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    backEnabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick, enabled = backEnabled) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.topbar_back_description),
                modifier = Modifier.size(38.dp)
            )
        }
        Text(
            text = stringResource(
                R.string.topbar_balance,
                NumberFormat.getIntegerInstance().format(balance)
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}