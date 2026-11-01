package com.androdevs.luckysevens.data

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 21:0
 *Year: 2026
 */
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.androdevs.luckysevens.domain.GameConstants
import com.androdevs.luckysevens.domain.PlayerState
import org.junit.After
import org.junit.Assert.assertEquals
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

    private lateinit var helper: GameDatabaseHelper
    private lateinit var repository: GameRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        helper = GameDatabaseHelper(context, null)
        repository = GameRepositoryImpl(helper)
    }

    @After
    fun tearDown() {
        helper.close()
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
}