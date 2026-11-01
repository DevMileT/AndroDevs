package com.androdevs.luckysevens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.components.TitleTopBar
import com.androdevs.luckysevens.ui.theme.ButtonAmber
import com.androdevs.luckysevens.ui.theme.HistoryCardBorder
import com.androdevs.luckysevens.ui.theme.HistoryCardFill
import com.androdevs.luckysevens.ui.theme.StatNegative
import com.androdevs.luckysevens.ui.theme.StatPositive
import com.androdevs.luckysevens.ui.theme.TextLight
import com.androdevs.luckysevens.ui.util.formatNumber
import com.androdevs.luckysevens.ui.state.StatsUiState

@Composable
fun StatsScreen(uiState: StatsUiState, onBackClick: () -> Unit) {
    val stats = uiState.stats
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        TitleTopBar(title = stringResource(R.string.menu_stats), onBackClick = onBackClick)
        if (uiState.hasError) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.error_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextLight
                )
            }
        } else if (stats != null) {
            // Mientras llegan los datos de SQLite no se pintan tarjetas: así no se ven ceros falsos.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        iconRes = R.drawable.ic_gamepad2,
                        label = stringResource(R.string.stats_total),
                        value = formatNumber(stats.total),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        iconRes = R.drawable.ic_trophy,
                        label = stringResource(R.string.stats_wins),
                        value = formatNumber(stats.wins),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        iconRes = R.drawable.ic_handshake,
                        label = stringResource(R.string.stats_draws),
                        value = formatNumber(stats.draws),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        iconRes = R.drawable.ic_coin_stack,
                        label = stringResource(R.string.stats_net_balance),
                        value = netBalanceText(stats.netBalance),
                        valueColor = netBalanceColor(stats.netBalance),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        iconRes = R.drawable.ic_defeat,
                        label = stringResource(R.string.stats_losses),
                        value = formatNumber(stats.losses),
                        modifier = Modifier.weight(1f)
                    )
                    // Hueco vacío a la derecha, como en el mockup.
                    Spacer(modifier = Modifier.weight(1f))
                }
                RecordCard(record = stats.record)
            }
        }
    }
}
// Texto del balance neto: con «+» si es positivo (P-07).
private fun netBalanceText(net: Int): String =
    if (net > 0) "+" + formatNumber(net) else formatNumber(net)

// Verde si es positivo, rojo si es negativo y blanco si es cero (P-07).
private fun netBalanceColor(net: Int): Color = when {
    net > 0 -> StatPositive
    net < 0 -> StatNegative
    else -> TextLight
}

// Tarjeta algo más ancha que alta: icono arriba, etiqueta y valor debajo.
@Composable
private fun StatCard(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified,
    iconSize: Dp = 40.dp,
    valueColor: Color = TextLight
) {
    Column(
        modifier = modifier
            .aspectRatio(1.1f)
            .background(HistoryCardFill, RoundedCornerShape(16.dp))
            .border(2.dp, HistoryCardBorder, RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Caja de alto fijo para el icono: así las etiquetas de todas las tarjetas quedan a la
        // misma altura aunque un icono se dibuje más grande que otro (requiredHeight).
        Box(modifier = Modifier.height(44.dp), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.requiredHeight(iconSize)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TextLight,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

// Tarjeta ancha del récord personal: corona a la izquierda y tres líneas de texto.
@Composable
private fun RecordCard(record: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp)
            .background(HistoryCardFill, RoundedCornerShape(16.dp))
            .border(2.dp, HistoryCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_crown),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.height(44.dp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Column {
            Text(
                text = stringResource(R.string.stats_record_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.stats_record_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.stats_record_value, formatNumber(record)),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ButtonAmber
            )
        }
    }
}