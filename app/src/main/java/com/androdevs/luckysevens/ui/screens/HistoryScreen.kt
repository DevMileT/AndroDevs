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
import com.androdevs.luckysevens.ui.components.TitleTopBar
import com.androdevs.luckysevens.ui.theme.ButtonAmber
import com.androdevs.luckysevens.ui.theme.HistoryCardBorder
import com.androdevs.luckysevens.ui.theme.HistoryCardFill
import com.androdevs.luckysevens.ui.theme.OutcomeDrawFill
import com.androdevs.luckysevens.ui.theme.OutcomeLossFill
import com.androdevs.luckysevens.ui.theme.OutcomeWinFill
import com.androdevs.luckysevens.ui.theme.TextLight
import com.androdevs.luckysevens.ui.util.formatNumber

// TEMPORAL (Fase 4): datos de prueba. En la Fase 6 el historial real llegará de SQLite
// (SpinRecord: fecha en milisegundos y resultado como SpinOutcome del dominio).
private enum class TestOutcome { WIN, DRAW, LOSS }

private data class TestSpin(
    val dateText: String,
    val symbols: List<Int>,
    val outcome: TestOutcome,
    val bet: Int,
    val prize: Int,
    val balanceAfter: Int
)

// De más reciente a más antigua (RF-10).
private val TEST_HISTORY = listOf(
    TestSpin(
        "13/06/2026 17:15",
        listOf(R.drawable.symbol_bell, R.drawable.symbol_bell, R.drawable.symbol_bell),
        TestOutcome.WIN, 50, 250, 1440
    ),
    TestSpin(
        "19/05/2026 19:21",
        listOf(R.drawable.symbol_cherry, R.drawable.symbol_orange, R.drawable.symbol_grape),
        TestOutcome.DRAW, 20, 20, 1240
    ),
    TestSpin(
        "11/04/2026 14:10",
        listOf(R.drawable.symbol_bell, R.drawable.symbol_bell, R.drawable.symbol_bell),
        TestOutcome.WIN, 20, 100, 1240
    ),
    TestSpin(
        "09/03/2026 12:38",
        listOf(R.drawable.symbol_orange, R.drawable.symbol_bell, R.drawable.symbol_seven),
        TestOutcome.LOSS, 20, 0, 1160
    ),
    TestSpin(
        "10/02/2026 18:40",
        listOf(R.drawable.symbol_seven, R.drawable.symbol_seven, R.drawable.symbol_seven),
        TestOutcome.WIN, 20, 200, 1180
    )
)

// Opciones del filtro (RF-10). Cada una guarda el texto que se muestra en el menú.
private enum class HistoryFilter(val labelRes: Int) {
    ALL(R.string.history_filter_all),
    WINS(R.string.history_filter_wins),
    DRAWS(R.string.history_filter_draws),
    LOSSES(R.string.history_filter_losses)
}

// Función de extensión: indica si un resultado pasa este filtro.
private fun HistoryFilter.accepts(outcome: TestOutcome): Boolean = when (this) {
    HistoryFilter.ALL -> true
    HistoryFilter.WINS -> outcome == TestOutcome.WIN
    HistoryFilter.DRAWS -> outcome == TestOutcome.DRAW
    HistoryFilter.LOSSES -> outcome == TestOutcome.LOSS
}

@Composable
fun HistoryScreen(onBackClick: () -> Unit) {
    var filter by remember { mutableStateOf(HistoryFilter.ALL) }
    val visibleSpins = TEST_HISTORY.filter { filter.accepts(it.outcome) }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        TitleTopBar(
            title = stringResource(R.string.menu_history),
            onBackClick = onBackClick,
            actions = {
                FilterMenu(selected = filter, onSelected = { filter = it })
            }
        )
        if (visibleSpins.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextLight
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
            ) {
                items(visibleSpins) { spin ->
                    SpinCard(spin = spin, modifier = Modifier.fillMaxWidth(0.88f))
                }
            }
        }
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
                            text = stringResource(option.labelRes),
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
private fun SpinCard(spin: TestSpin, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .background(HistoryCardFill, RoundedCornerShape(16.dp))
            .border(2.dp, HistoryCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = spin.dateText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    spin.symbols.forEach { symbolRes ->
                        Image(
                            painter = painterResource(symbolRes),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            OutcomeChip(outcome = spin.outcome)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                R.string.history_row_summary,
                formatNumber(spin.bet),
                formatNumber(spin.prize),
                formatNumber(spin.balanceAfter)
            ),
            style = MaterialTheme.typography.bodySmall,
            color = TextLight.copy(alpha = 0.75f)
        )
    }
}

// Etiqueta de color: Victoria (verde), Empate (azul) o Derrota (rojo).
@Composable
private fun OutcomeChip(outcome: TestOutcome) {
    val fill = when (outcome) {
        TestOutcome.WIN -> OutcomeWinFill
        TestOutcome.DRAW -> OutcomeDrawFill
        TestOutcome.LOSS -> OutcomeLossFill
    }
    val label = when (outcome) {
        TestOutcome.WIN -> R.string.history_outcome_win
        TestOutcome.DRAW -> R.string.history_outcome_draw
        TestOutcome.LOSS -> R.string.history_outcome_loss
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