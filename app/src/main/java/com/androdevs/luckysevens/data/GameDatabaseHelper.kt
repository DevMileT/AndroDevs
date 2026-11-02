package com.androdevs.luckysevens.data

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 20:0
 *Year: 2026
 */

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.androdevs.luckysevens.domain.GameConstants

/**
 * Crea y abre la base de datos SQLite. Solo crea las tablas: las consultas viven
 * en los repositorios. Debe haber UNA sola instancia en toda la app.
 * getWritableDatabase() puede tardar la primera vez, así que nunca se llama desde el hilo
 * principal.
 */
class GameDatabaseHelper(
    context: Context,
    name: String? = DATABASE_NAME
) : SQLiteOpenHelper(context, name, null, DATABASE_VERSION) {

    /** Se ejecuta una sola vez, cuando el archivo de la base de datos no existe. */
    override fun onCreate(db: SQLiteDatabase) {
        // execSQL ejecuta UNA sentencia por llamada.
        db.execSQL(
            """
            CREATE TABLE $TABLE_PLAYER (
                $COL_ID INTEGER PRIMARY KEY CHECK ($COL_ID = 1),
                $COL_BALANCE INTEGER NOT NULL CHECK ($COL_BALANCE >= 0),
                $COL_RECORD INTEGER NOT NULL,
                $COL_LAST_BONUS_DATE TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE $TABLE_SPIN_HISTORY (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PLAYED_AT INTEGER NOT NULL,
                $COL_BET INTEGER NOT NULL CHECK ($COL_BET > 0),
                $COL_SYMBOLS TEXT NOT NULL,
                $COL_PRIZE INTEGER NOT NULL,
                $COL_OUTCOME TEXT NOT NULL CHECK ($COL_OUTCOME IN ('WIN', 'DRAW', 'LOSS')),
                $COL_BALANCE_AFTER INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX idx_spin_history_outcome ON $TABLE_SPIN_HISTORY ($COL_OUTCOME)"
        )
        // Fila inicial del jugador: saldo y récord parten de INITIAL_BALANCE
        db.execSQL(
            "INSERT INTO $TABLE_PLAYER ($COL_ID, $COL_BALANCE, $COL_RECORD) " +
                    "VALUES (1, ${GameConstants.INITIAL_BALANCE}, ${GameConstants.INITIAL_BALANCE})"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        const val DATABASE_NAME = "lucky_sevens.db"
        const val DATABASE_VERSION = 1

        const val TABLE_PLAYER = "player"
        const val TABLE_SPIN_HISTORY = "spin_history"

        const val COL_ID = "id"
        const val COL_BALANCE = "balance"
        const val COL_RECORD = "record"
        const val COL_LAST_BONUS_DATE = "last_bonus_date"
        const val COL_PLAYED_AT = "played_at"
        const val COL_BET = "bet"
        const val COL_SYMBOLS = "symbols"
        const val COL_PRIZE = "prize"
        const val COL_OUTCOME = "outcome"
        const val COL_BALANCE_AFTER = "balance_after"
    }
}