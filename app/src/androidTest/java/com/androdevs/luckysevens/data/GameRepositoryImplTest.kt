package com.androdevs.luckysevens.data

import android.database.SQLException
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.androdevs.luckysevens.domain.GameConstants
import com.androdevs.luckysevens.domain.GameEngine
import com.androdevs.luckysevens.domain.PlayerState
import com.androdevs.luckysevens.domain.RandomReelGenerator
import com.androdevs.luckysevens.domain.ReelGenerator
import com.androdevs.luckysevens.domain.SpinOutcome
import com.androdevs.luckysevens.domain.Symbol
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Pruebas de GameRepositoryImpl. Se ejecutan en el móvil con una base EN MEMORIA
 * (name = null): no tocan la base real de la app y desaparecen al cerrarse.
 */
@RunWith(AndroidJUnit4::class)
class GameRepositoryImplTest {

    private companion object {
        const val FIXED_TIME = 1_700_000_000_000L
    }

    private lateinit var helper: GameDatabaseHelper
    private lateinit var repository: GameRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        helper = GameDatabaseHelper(context, null)
        repository = GameRepositoryImpl(helper, GameEngine(RandomReelGenerator()))
    }

    @After
    fun tearDown() {
        helper.close()
    }

    /** Repositorio cuyo giro siempre da estos símbolos y cuya hora es fija. */
    private fun repositoryWith(vararg symbols: Symbol): GameRepositoryImpl {
        val generator = object : ReelGenerator {
            override fun nextSymbols(): List<Symbol> = symbols.toList()
        }
        return GameRepositoryImpl(helper, GameEngine(generator), { FIXED_TIME })
    }

    private fun countHistoryRows(): Int =
        helper.writableDatabase.rawQuery("SELECT COUNT(*) FROM spin_history", null).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

    @Test
    fun getPlayerState_onNewDatabase_returnsInitialState() {
        // blockingGet() espera al resultado: solo se usa en pruebas, nunca en la app.
        val state = repository.getPlayerState().blockingGet()

        assertEquals(
            PlayerState(
                balance = GameConstants.INITIAL_BALANCE,
                record = GameConstants.INITIAL_BALANCE,
                lastBonusDate = null
            ),
            state
        )
    }

    @Test
    fun getPlayerState_readsEachColumnFromItsOwnPlace() {
        // Valores distintos entre sí, para detectar columnas intercambiadas.
        helper.writableDatabase.execSQL(
            "UPDATE player SET balance = 750, record = 1200, " +
                    "last_bonus_date = '2026-11-01' WHERE id = 1"
        )

        val state = repository.getPlayerState().blockingGet()

        assertEquals(
            PlayerState(
                balance = 750,
                record = 1200,
                lastBonusDate = LocalDate.of(2026, 11, 1)
            ),
            state
        )
    }

    @Test
    fun spin_threeSevens_savesBalanceRecordAndHistory() {
        // CA-04: apuesta 20 y 7-7-7 = premio 200 y saldo 1000 - 20 + 200 = 1180.
        val result = repositoryWith(Symbol.SEVEN, Symbol.SEVEN, Symbol.SEVEN)
            .spin(20).blockingGet()

        assertEquals(200, result.prize)
        assertEquals(SpinOutcome.WIN, result.outcome)
        assertEquals(1180, result.balanceAfter)

        val state = repository.getPlayerState().blockingGet()
        assertEquals(1180, state.balance)
        assertEquals(1180, state.record)

        helper.writableDatabase.rawQuery(
            "SELECT played_at, bet, symbols, prize, outcome, balance_after FROM spin_history",
            null
        ).use { cursor ->
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            assertEquals(FIXED_TIME, cursor.getLong(0))
            assertEquals(20, cursor.getInt(1))
            assertEquals("SEVEN,SEVEN,SEVEN", cursor.getString(2))
            assertEquals(200, cursor.getInt(3))
            assertEquals("WIN", cursor.getString(4))
            assertEquals(1180, cursor.getInt(5))
        }
    }

    @Test
    fun spin_loss_reducesBalanceAndKeepsRecord() {
        // CA-07: apuesta 20 y limón-limón-uva = premio 0 y saldo -20; el récord no baja.
        val result = repositoryWith(Symbol.LEMON, Symbol.LEMON, Symbol.GRAPE)
            .spin(20).blockingGet()

        assertEquals(0, result.prize)
        assertEquals(SpinOutcome.LOSS, result.outcome)

        val state = repository.getPlayerState().blockingGet()
        assertEquals(980, state.balance)
        assertEquals(1000, state.record)
        assertEquals(1, countHistoryRows())
    }

    @Test
    fun spin_invalidBet_throwsAndWritesNothing() {
        try {
            repositoryWith(Symbol.SEVEN, Symbol.SEVEN, Symbol.SEVEN).spin(5).blockingGet()
            fail("Debía lanzar IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // esperado (CA-03)
        }

        val state = repository.getPlayerState().blockingGet()
        assertEquals(PlayerState(balance = 1000, record = 1000, lastBonusDate = null), state)
        assertEquals(0, countHistoryRows())
    }

    @Test
    fun spin_ifHistoryInsertFails_balanceIsNotChanged() {
        // Atomicidad (D-02): se borra la tabla del historial a propósito. El saldo se actualiza
        // primero y el INSERT del historial falla después: la transacción debe deshacerlo todo.
        helper.writableDatabase.execSQL("DROP TABLE spin_history")

        try {
            repositoryWith(Symbol.SEVEN, Symbol.SEVEN, Symbol.SEVEN).spin(20).blockingGet()
            fail("Debía fallar al guardar el historial")
        } catch (e: SQLException) {
            // esperado
        }

        val state = repository.getPlayerState().blockingGet()
        assertEquals(1000, state.balance)
        assertEquals(1000, state.record)
    }
}