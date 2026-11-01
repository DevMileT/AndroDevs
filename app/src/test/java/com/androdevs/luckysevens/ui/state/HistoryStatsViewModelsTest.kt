package com.androdevs.luckysevens.ui.state

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 23:0
 *Year: 2026
 */
import com.androdevs.luckysevens.domain.GameStats
import com.androdevs.luckysevens.domain.HistoryFilter
import com.androdevs.luckysevens.domain.HistoryRepository
import com.androdevs.luckysevens.domain.SpinOutcome
import com.androdevs.luckysevens.domain.SpinRecord
import com.androdevs.luckysevens.domain.SpinResult
import com.androdevs.luckysevens.domain.Symbol
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de HistoryViewModel y StatsViewModel en el PC (repositorio falso, planificador síncrono). */
class HistoryStatsViewModelsTest {

    private class FakeHistoryRepository : HistoryRepository {
        val requestedFilters = mutableListOf<HistoryFilter>()
        var records: Map<HistoryFilter, List<SpinRecord>> = emptyMap()
        var stats = GameStats(wins = 0, draws = 0, losses = 0, netBalance = 0, record = 1000)
        var fail = false

        override fun getHistory(filter: HistoryFilter): Single<List<SpinRecord>> {
            requestedFilters.add(filter)
            return if (fail) {
                Single.error(IllegalStateException("fallo de prueba"))
            } else {
                Single.just(records[filter] ?: emptyList())
            }
        }

        override fun getStats(): Single<GameStats> =
            if (fail) Single.error(IllegalStateException("fallo de prueba")) else Single.just(stats)
    }

    private val repository = FakeHistoryRepository()

    private fun record(playedAt: Long, outcome: SpinOutcome) = SpinRecord(
        playedAt = playedAt,
        result = SpinResult(
            bet = 10,
            symbols = listOf(Symbol.CHERRY, Symbol.LEMON, Symbol.GRAPE),
            prize = 0,
            outcome = outcome,
            balanceAfter = 990
        )
    )

    @Test
    fun history_onCreation_loadsAllRecords() {
        val win = record(2, SpinOutcome.WIN)
        val loss = record(1, SpinOutcome.LOSS)
        repository.records = mapOf(HistoryFilter.ALL to listOf(win, loss))

        val viewModel = HistoryViewModel(repository, Schedulers.trampoline())

        assertEquals(HistoryFilter.ALL, viewModel.uiState.filter)
        assertEquals(listOf(win, loss), viewModel.uiState.records)
        assertFalse(viewModel.uiState.isLoading)
        assertFalse(viewModel.uiState.hasError)
    }

    @Test
    fun history_filterSelected_requestsThatFilterAndShowsItsRecords() {
        // CA-22: cada filtro pide al repositorio solo las partidas de ese resultado.
        val win = record(2, SpinOutcome.WIN)
        val loss = record(1, SpinOutcome.LOSS)
        repository.records = mapOf(
            HistoryFilter.ALL to listOf(win, loss),
            HistoryFilter.WINS to listOf(win)
        )
        val viewModel = HistoryViewModel(repository, Schedulers.trampoline())

        viewModel.onFilterSelected(HistoryFilter.WINS)

        assertEquals(HistoryFilter.WINS, viewModel.uiState.filter)
        assertEquals(listOf(win), viewModel.uiState.records)
        assertEquals(listOf(HistoryFilter.ALL, HistoryFilter.WINS), repository.requestedFilters)
    }

    @Test
    fun history_error_setsErrorAndStopsLoading() {
        repository.fail = true

        val viewModel = HistoryViewModel(repository, Schedulers.trampoline())

        assertTrue(viewModel.uiState.hasError)
        assertFalse(viewModel.uiState.isLoading)
    }

    @Test
    fun stats_onCreation_loadsStats() {
        repository.stats = GameStats(wins = 1, draws = 1, losses = 2, netBalance = 150, record = 1250)

        val viewModel = StatsViewModel(repository, Schedulers.trampoline())

        assertEquals(repository.stats, viewModel.uiState.stats)
        assertEquals(4, viewModel.uiState.stats?.total)
        assertFalse(viewModel.uiState.hasError)
    }

    @Test
    fun stats_error_setsErrorAndNoStats() {
        repository.fail = true

        val viewModel = StatsViewModel(repository, Schedulers.trampoline())

        assertTrue(viewModel.uiState.hasError)
        assertNull(viewModel.uiState.stats)
    }
}