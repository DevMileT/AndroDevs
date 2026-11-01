package com.androdevs.luckysevens.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.domain.HistoryFilter
import com.androdevs.luckysevens.domain.SpinOutcome
import com.androdevs.luckysevens.domain.SpinRecord
import com.androdevs.luckysevens.ui.components.TitleTopBar
import com.androdevs.luckysevens.ui.state.HistoryUiState
import com.androdevs.luckysevens.ui.theme.ButtonAmber
import com.androdevs.luckysevens.ui.theme.HistoryCardBorder
import com.androdevs.luckysevens.ui.theme.HistoryCardFill
import com.androdevs.luckysevens.ui.theme.OutcomeDrawFill
import com.androdevs.luckysevens.ui.theme.OutcomeLossFill
import com.androdevs.luckysevens.ui.theme.OutcomeWinFill
import com.androdevs.luckysevens.ui.theme.TextLight
import com.androdevs.luckysevens.ui.util.drawableRes
import com.androdevs.luckysevens.ui.util.formatDateTime
import com.androdevs.luckysevens.ui.util.formatNumber

// Texto de cada opción del filtro (RF-10). El filtro en sí vive en el dominio (HistoryFilter).
private fun HistoryFilter.labelRes(): Int = when (this) {
    HistoryFilter.ALL -> R.string.history_filter_all
    HistoryFilter.WINS -> R.string.history_filter_wins
    HistoryFilter.DRAWS -> R.string.history_filter_draws
    HistoryFilter.LOSSES -> R.string.history_filter_losses
}

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onFilterSelected: (HistoryFilter) -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        TitleTopBar(
            title = stringResource(R.string.menu_history),
            onBackClick = onBackClick,
            actions = {
                FilterMenu(selected = uiState.filter, onSelected = onFilterSelected)
            }
        )
        when {
            uiState.hasError -> CenteredMessage(R.string.error_load_failed)
            uiState.records.isEmpty() -> {
                // Mientras carga no se muestra «No hay partidas», para no parpadear.
                if (!uiState.isLoading) {
                    CenteredMessage(R.string.history_empty)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
                ) {
                    items(uiState.records) { record ->
                        SpinCard(record = record, modifier = Modifier.fillMaxWidth(0.88f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CenteredMessage(textRes: Int) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.bodyLarge,
            color = TextLight
        )
    }
}

// Embudo con su menú desplegable. La opción activa se marca en negrita y color ámbar.
@Composable
private fun FilterMenu(selected: HistoryFilter, onSelected: (HistoryFilter) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = stringResource(R.string.history_filter_description),
                modifier = Modifier.size(32.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            HistoryFilter.entries.forEach { option ->
                val isSelected = option == selected
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(option.labelRes()),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ButtonAmber else Color.Unspecified
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    }
                )
            }
        }
    }
}

// Tarjeta de una partida: fecha, combinación, etiqueta de resultado y resumen económico.
@Composable
private fun SpinCard(record: SpinRecord, modifier: Modifier = Modifier) {
    val result = record.result
    Column(
        modifier = modifier
            .background(HistoryCardFill, RoundedCornerShape(16.dp))
            .border(2.dp, HistoryCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDateTime(record.playedAt),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    result.symbols.forEach { symbol ->
                        Image(
                            painter = painterResource(symbol.drawableRes()),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            OutcomeChip(outcome = result.outcome)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                R.string.history_row_summary,
                formatNumber(result.bet),
                formatNumber(result.prize),
                formatNumber(result.balanceAfter)
            ),
            style = MaterialTheme.typography.bodySmall,
            color = TextLight.copy(alpha = 0.75f)
        )
    }
}

// Etiqueta de color: Victoria (verde), Empate (azul) o Derrota (rojo).
@Composable
private fun OutcomeChip(outcome: SpinOutcome) {
    val fill = when (outcome) {
        SpinOutcome.WIN -> OutcomeWinFill
        SpinOutcome.DRAW -> OutcomeDrawFill
        SpinOutcome.LOSS -> OutcomeLossFill
    }
    val label = when (outcome) {
        SpinOutcome.WIN -> R.string.history_outcome_win
        SpinOutcome.DRAW -> R.string.history_outcome_draw
        SpinOutcome.LOSS -> R.string.history_outcome_loss
    }
    Text(
        text = stringResource(label),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = TextLight,
        modifier = Modifier
            .background(fill, RoundedCornerShape(percent = 50))
            .padding(horizontal = 16.dp, vertical = 7.dp)
    )
}