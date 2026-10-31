package com.androdevs.luckysevens.ui.components

/**
 *Project: LuckySevens
 *Created by: Milena
 *On: 29/10/2026 at 01:0
 *Year: 2026
 */

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.theme.BalanceBadgeBorder
import com.androdevs.luckysevens.ui.theme.BalanceBadgeFill
import com.androdevs.luckysevens.ui.theme.TextLight
import com.androdevs.luckysevens.ui.util.formatNumber
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
        BalanceBadge(balance = balance, modifier = Modifier.padding(end = 8.dp))
    }
}

// Píldora del saldo: moneda y número sobre fondo azul (mockup de Juego).
// TalkBack lee «Saldo: …» aunque en pantalla solo se vea el número.
@Composable
private fun BalanceBadge(balance: Int, modifier: Modifier = Modifier) {
    val formatted = formatNumber(balance)
    val description = stringResource(R.string.topbar_balance, formatted)
    Row(
        modifier = modifier
            .background(BalanceBadgeFill, RoundedCornerShape(percent = 50))
            .border(2.dp, BalanceBadgeBorder, RoundedCornerShape(percent = 50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .semantics(mergeDescendants = true) { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_coin),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formatted,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextLight
        )
    }
}