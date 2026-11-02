package com.androdevs.luckysevens.data

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 22:0
 *Year: 2026
 */

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.androdevs.luckysevens.domain.GameStats
import com.androdevs.luckysevens.domain.HistoryFilter
import com.androdevs.luckysevens.domain.SpinOutcome
import com.androdevs.luckysevens.domain.SpinRecord
import com.androdevs.luckysevens.domain.SpinResult
import com.androdevs.luckysevens.domain.Symbol
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryRepositoryImplTest {

    private lateinit var helper: GameDatabaseHelper
    private lateinit var repository: HistoryRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        helper = GameDatabaseHelper(context, null)
        repository = HistoryRepositoryImpl(helper)
    }

    @After
    fun tearDown() {
        helper.close()
    }

    private fun insertSpin(
        playedAt: Long, bet: Int, symbols: String, prize: Int, outcome: String, balanceAfter: Int
    ) {
        helper.writableDatabase.execSQL(
            "INSERT INTO spin_history (played_at, bet, symbols, prize, outcome, balance_after) " +
                    "VALUES (?, ?, ?, ?, ?, ?)",
            arrayOf<Any>(playedAt, bet, symbols, prize, outcome, balanceAfter)
        )
    }

    /** Cuatro partidas: una victoria, un empate y dos derrotas la última, la más reciente. */
    private fun insertFourSpins() {
        insertSpin(1, 20, "SEVEN,SEVEN,SEVEN", 200, "WIN", 1180)
        insertSpin(2, 20, "CHERRY,LEMON,GRAPE", 20, "DRAW", 1180)
        insertSpin(3, 20, "LEMON,LEMON,GRAPE", 0, "LOSS", 1160)
        insertSpin(4, 10, "ORANGE,BELL,SEVEN", 0, "LOSS", 1150)
    }

    @Test
    fun getHistory_onNewDatabase_returnsEmptyList() {
        // primer arranque, sin historial.
        assertTrue(repository.getHistory(HistoryFilter.ALL).blockingGet().isEmpty())
    }

    @Test
    fun getHistory_mapsEveryField() {
        insertSpin(1234, 20, "BELL,BELL,CHERRY", 40, "WIN", 1020)

        val history = repository.getHistory(HistoryFilter.ALL).blockingGet()

        assertEquals(
            listOf(
                SpinRecord(
                    playedAt = 1234,
                    result = SpinResult(
                        bet = 20,
                        symbols = listOf(Symbol.BELL, Symbol.BELL, Symbol.CHERRY),
                        prize = 40,
                        outcome = SpinOutcome.WIN,
                        balanceAfter = 1020
                    )
                )
            ),
            history
        )
    }

    @Test
    fun getHistory_returnsMostRecentFirst() {
        // de la más reciente a la más antigua.
        insertFourSpins()

        val history = repository.getHistory(HistoryFilter.ALL).blockingGet()

        assertEquals(listOf(4L, 3L, 2L, 1L), history.map { it.playedAt })
    }

    @Test
    fun getHistory_filterReturnsOnlyThatOutcome() {
        // cada filtro muestra solo sus partidas, de la más reciente a la más antigua.
        insertFourSpins()

        assertEquals(4, repository.getHistory(HistoryFilter.ALL).blockingGet().size)
        assertEquals(
            listOf(1L),
            repository.getHistory(HistoryFilter.WINS).blockingGet().map { it.playedAt }
        )
        assertEquals(
            listOf(2L),
            repository.getHistory(HistoryFilter.DRAWS).blockingGet().map { it.playedAt }
        )
        assertEquals(
            listOf(4L, 3L),
            repository.getHistory(HistoryFilter.LOSSES).blockingGet().map { it.playedAt }
        )
    }

    @Test
    fun getStats_onNewDatabase_returnsZerosAndInitialRecord() {
        // sin partidas, todo a cero y el récord inicial (1.000).
        val stats = repository.getStats().blockingGet()

        assertEquals(
            GameStats(wins = 0, draws = 0, losses = 0, netBalance = 0, record = 1000),
            stats
        )
        assertEquals(0, stats.total)
    }

    @Test
    fun getStats_countsOutcomesAndAddsNetBalance() {
        // total = victorias + empates + derrotas; balance neto = suma de (premio - apuesta).
        // Netos: +180, 0, -20 y -10 = +150. El récord sale de player, no del historial.
        insertFourSpins()
        helper.writableDatabase.execSQL("UPDATE player SET record = 1250 WHERE id = 1")

        val stats = repository.getStats().blockingGet()

        assertEquals(
            GameStats(wins = 1, draws = 1, losses = 2, netBalance = 150, record = 1250),
            stats
        )
        assertEquals(4, stats.total)
    }

    @Test
    fun getStats_netBalanceCanBeNegative() {
        insertSpin(1, 20, "LEMON,LEMON,GRAPE", 0, "LOSS", 980)

        assertEquals(-20, repository.getStats().blockingGet().netBalance)
    }
}