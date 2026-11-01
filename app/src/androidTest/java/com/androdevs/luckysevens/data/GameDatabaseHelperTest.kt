package com.androdevs.luckysevens.data

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 20:0
 *Year: 2026
 */
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.androdevs.luckysevens.domain.GameConstants
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.database.sqlite.SQLiteConstraintException

/**
 * Prueba la creación de la base de datos (D-04). Se ejecuta en el móvil.
 * Usa una base EN MEMORIA (name = null): no toca la base real de la app y desaparece al cerrarse.
 */
@RunWith(AndroidJUnit4::class)
class GameDatabaseHelperTest {

    private lateinit var helper: GameDatabaseHelper
    private lateinit var db: SQLiteDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        helper = GameDatabaseHelper(context, null)
        db = helper.writableDatabase // aquí se ejecuta onCreate
    }

    @After
    fun tearDown() {
        helper.close()
    }

    @Test
    fun onCreate_insertsInitialPlayerRow() {
        db.rawQuery("SELECT balance, record, last_bonus_date FROM player", null).use { cursor ->
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            assertEquals(GameConstants.INITIAL_BALANCE, cursor.getInt(0))
            assertEquals(GameConstants.INITIAL_BALANCE, cursor.getInt(1))
            assertTrue(cursor.isNull(2))
        }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun player_negativeBalance_isRejectedByCheck() {
        db.execSQL("UPDATE player SET balance = -1 WHERE id = 1")
    }

    @Test(expected = SQLiteConstraintException::class)
    fun player_secondRow_isRejectedByCheck() {
        // Se da todo lo obligatorio (balance y record) para que lo único que pueda
        // rechazar la fila sea el CHECK (id = 1) y no un NOT NULL.
        db.execSQL("INSERT INTO player (id, balance, record) VALUES (2, 500, 500)")
    }

    @Test
    fun spinHistory_startsEmpty() {
        db.rawQuery("SELECT COUNT(*) FROM spin_history", null).use { cursor ->
            cursor.moveToFirst()
            assertEquals(0, cursor.getInt(0))
        }
    }

}