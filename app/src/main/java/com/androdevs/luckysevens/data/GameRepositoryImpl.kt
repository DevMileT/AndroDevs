package com.androdevs.luckysevens.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.androdevs.luckysevens.domain.GameEngine
import com.androdevs.luckysevens.domain.GameRepository
import com.androdevs.luckysevens.domain.PlayerState
import com.androdevs.luckysevens.domain.SpinResult
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDate

/**
 * Implementación de GameRepository con SQLite (D-03). Todo el trabajo de base de datos se
 * ejecuta en un hilo de fondo (subscribeOn), nunca en el principal (RNF-03).
 * gameEngine y nowMillis llegan desde fuera para poder sustituirlos en las pruebas (D-13).
 */
class GameRepositoryImpl(
    private val helper: GameDatabaseHelper,
    private val gameEngine: GameEngine,
    private val nowMillis: () -> Long = System::currentTimeMillis
) : GameRepository {

    override fun getPlayerState(): Single<PlayerState> =
        Single.fromCallable { readPlayerState(helper.readableDatabase) }
            .subscribeOn(Schedulers.io())

    override fun spin(bet: Int): Single<SpinResult> =
        Single.fromCallable { spinInTransaction(bet) }
            .subscribeOn(Schedulers.io())

    override fun claimBonus(): Single<Boolean> =
        TODO("Bloque B: UPDATE condicionado del bonus")

    /**
     * Resuelve un giro y guarda saldo, récord e historial en UNA transacción (D-02, RNF-04):
     * o se guarda todo o no se guarda nada. Debe llamarse desde un hilo de fondo.
     */
    private fun spinInTransaction(bet: Int): SpinResult {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val state = readPlayerState(db)
            // Lanza IllegalArgumentException si la apuesta no es válida (D-12): no se escribe nada.
            val result = gameEngine.spin(state, bet)
            val newState = state.withBalance(result.balanceAfter)

            val playerValues = ContentValues().apply {
                put(GameDatabaseHelper.COL_BALANCE, newState.balance)
                put(GameDatabaseHelper.COL_RECORD, newState.record)
            }
            val updatedRows = db.update(
                GameDatabaseHelper.TABLE_PLAYER,
                playerValues,
                "${GameDatabaseHelper.COL_ID} = 1",
                null
            )
            check(updatedRows == 1) { "No se pudo actualizar al jugador" }

            val historyValues = ContentValues().apply {
                put(GameDatabaseHelper.COL_PLAYED_AT, nowMillis())
                put(GameDatabaseHelper.COL_BET, result.bet)
                put(GameDatabaseHelper.COL_SYMBOLS, result.symbols.joinToString(",") { it.name })
                put(GameDatabaseHelper.COL_PRIZE, result.prize)
                put(GameDatabaseHelper.COL_OUTCOME, result.outcome.name)
                put(GameDatabaseHelper.COL_BALANCE_AFTER, result.balanceAfter)
            }
            // insertOrThrow lanza una excepción si falla (insert devolvería -1 en silencio).
            db.insertOrThrow(GameDatabaseHelper.TABLE_SPIN_HISTORY, null, historyValues)

            db.setTransactionSuccessful() // "confirmo": sin esto, endTransaction lo deshace todo
            return result
        } finally {
            db.endTransaction()
        }
    }

    /** Lee la única fila de la tabla player con la base indicada. Solo desde un hilo de fondo. */
    private fun readPlayerState(db: SQLiteDatabase): PlayerState {
        val sql = "SELECT ${GameDatabaseHelper.COL_BALANCE}, " +
                "${GameDatabaseHelper.COL_RECORD}, " +
                "${GameDatabaseHelper.COL_LAST_BONUS_DATE} " +
                "FROM ${GameDatabaseHelper.TABLE_PLAYER} " +
                "WHERE ${GameDatabaseHelper.COL_ID} = 1"
        return db.rawQuery(sql, null).use { cursor ->
            check(cursor.moveToFirst()) { "No existe la fila del jugador" }
            PlayerState(
                balance = cursor.getInt(0),
                record = cursor.getInt(1),
                // La fecha se guarda como texto AAAA-MM-DD; NULL significa "nunca reclamado".
                lastBonusDate = if (cursor.isNull(2)) null else LocalDate.parse(cursor.getString(2))
            )
        }
    }
}