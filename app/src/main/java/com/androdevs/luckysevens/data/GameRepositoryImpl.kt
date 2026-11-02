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
import com.androdevs.luckysevens.domain.DateProvider
import com.androdevs.luckysevens.domain.GameConstants
import com.androdevs.luckysevens.domain.SystemDateProvider
/**
 * Implementación de GameRepository con SQLite. Todo el trabajo de base de datos se
 * ejecuta en un hilo de fondo (subscribeOn), nunca en el principal.
 * gameEngine y nowMillis llegan desde fuera para poder sustituirlos en las pruebas.
 */
class GameRepositoryImpl(
    private val helper: GameDatabaseHelper,
    private val gameEngine: GameEngine,
    private val nowMillis: () -> Long = System::currentTimeMillis,
    private val dateProvider: DateProvider = SystemDateProvider()
) : GameRepository {

    override fun getPlayerState(): Single<PlayerState> =
        Single.fromCallable { readPlayerState(helper.readableDatabase) }
            .subscribeOn(Schedulers.io())

    override fun spin(bet: Int): Single<SpinResult> =
        Single.fromCallable { spinInTransaction(bet) }
            .subscribeOn(Schedulers.io())

    override fun claimBonus(): Single<Boolean> =
        Single.fromCallable { claimBonusNow() }
            .subscribeOn(Schedulers.io())

    /**
     * Resuelve un giro y guarda saldo, récord e historial en UNA transacción:
     * o se guarda todo o no se guarda nada. Debe llamarse desde un hilo de fondo.
     */
    private fun spinInTransaction(bet: Int): SpinResult {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val state = readPlayerState(db)
            // Lanza IllegalArgumentException si la apuesta no es válida: no se escribe nada.
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

    /**
     * Reclama el bonus con un único UPDATE condicionado: comprobar y escribir
     * ocurren a la vez, así que dos llamadas seguidas no pueden sumar dos veces.
     * Devuelve true si se concedió ahora (1 fila modificada) y false si ya estaba reclamado hoy.
     */
    private fun claimBonusNow(): Boolean {
        val today = dateProvider.today().toString() // formato ISO AAAA-MM-DD
        val sql = "UPDATE ${GameDatabaseHelper.TABLE_PLAYER} SET " +
                "${GameDatabaseHelper.COL_BALANCE} = ${GameDatabaseHelper.COL_BALANCE} + ?, " +
                "${GameDatabaseHelper.COL_RECORD} = " +
                "MAX(${GameDatabaseHelper.COL_RECORD}, ${GameDatabaseHelper.COL_BALANCE} + ?), " +
                "${GameDatabaseHelper.COL_LAST_BONUS_DATE} = ? " +
                "WHERE ${GameDatabaseHelper.COL_ID} = 1 AND " +
                "(${GameDatabaseHelper.COL_LAST_BONUS_DATE} IS NULL " +
                "OR ${GameDatabaseHelper.COL_LAST_BONUS_DATE} <> ?)"
        return helper.writableDatabase.compileStatement(sql).use { statement ->
            statement.bindLong(1, GameConstants.DAILY_BONUS.toLong())
            statement.bindLong(2, GameConstants.DAILY_BONUS.toLong())
            statement.bindString(3, today)
            statement.bindString(4, today)
            statement.executeUpdateDelete() == 1
        }
    }
}