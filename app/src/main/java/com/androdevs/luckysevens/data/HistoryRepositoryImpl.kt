package com.androdevs.luckysevens.data

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 22:0
 *Year: 2026
 */

import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.COL_BALANCE_AFTER
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.COL_BET
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.COL_ID
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.COL_OUTCOME
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.COL_PLAYED_AT
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.COL_PRIZE
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.COL_RECORD
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.COL_SYMBOLS
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.TABLE_PLAYER
import com.androdevs.luckysevens.data.GameDatabaseHelper.Companion.TABLE_SPIN_HISTORY
import com.androdevs.luckysevens.domain.GameStats
import com.androdevs.luckysevens.domain.HistoryFilter
import com.androdevs.luckysevens.domain.HistoryRepository
import com.androdevs.luckysevens.domain.SpinOutcome
import com.androdevs.luckysevens.domain.SpinRecord
import com.androdevs.luckysevens.domain.SpinResult
import com.androdevs.luckysevens.domain.Symbol
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Implementación de HistoryRepository con SQLite (D-03, D-04). Solo lee: nunca modifica la
 * partida. El trabajo se ejecuta en un hilo de fondo (RNF-03).
 */
class HistoryRepositoryImpl(
    private val helper: GameDatabaseHelper
) : HistoryRepository {

    override fun getHistory(filter: HistoryFilter): Single<List<SpinRecord>> =
        Single.fromCallable { readHistory(filter) }
            .subscribeOn(Schedulers.io())

    override fun getStats(): Single<GameStats> =
        Single.fromCallable { readStats() }
            .subscribeOn(Schedulers.io())

    /** Partidas de la más reciente a la más antigua (RF-10), filtradas por resultado (CA-22). */
    private fun readHistory(filter: HistoryFilter): List<SpinRecord> {
        val outcome = filter.outcome
        // ALL no filtra; los demás filtros añaden WHERE con el resultado como parámetro (?).
        val where = if (outcome == null) "" else " WHERE $COL_OUTCOME = ?"
        val args = if (outcome == null) null else arrayOf(outcome.name)
        val sql = "SELECT $COL_PLAYED_AT, $COL_BET, $COL_SYMBOLS, $COL_PRIZE, " +
                "$COL_OUTCOME, $COL_BALANCE_AFTER FROM $TABLE_SPIN_HISTORY$where " +
                "ORDER BY $COL_ID DESC"
        return helper.readableDatabase.rawQuery(sql, args).use { cursor ->
            val records = ArrayList<SpinRecord>()
            while (cursor.moveToNext()) {
                records.add(
                    SpinRecord(
                        playedAt = cursor.getLong(0),
                        result = SpinResult(
                            bet = cursor.getInt(1),
                            // Los símbolos se guardan como texto separado por comas (D-04).
                            symbols = cursor.getString(2).split(",").map { Symbol.valueOf(it) },
                            prize = cursor.getInt(3),
                            outcome = SpinOutcome.valueOf(cursor.getString(4)),
                            balanceAfter = cursor.getInt(5)
                        )
                    )
                )
            }
            records
        }
    }

    /**
     * Estadísticas con COUNT/SUM sobre el historial (D-04, CA-23). En SQLite una comparación
     * vale 1 o 0, así que SUM(outcome = 'WIN') cuenta las victorias. El récord sale de player.
     */
    private fun readStats(): GameStats {
        val db = helper.readableDatabase
        val statsSql = "SELECT " +
                "COALESCE(SUM($COL_OUTCOME = '${SpinOutcome.WIN.name}'), 0), " +
                "COALESCE(SUM($COL_OUTCOME = '${SpinOutcome.DRAW.name}'), 0), " +
                "COALESCE(SUM($COL_OUTCOME = '${SpinOutcome.LOSS.name}'), 0), " +
                "COALESCE(SUM($COL_PRIZE - $COL_BET), 0) " +
                "FROM $TABLE_SPIN_HISTORY"
        var wins = 0
        var draws = 0
        var losses = 0
        var netBalance = 0
        db.rawQuery(statsSql, null).use { cursor ->
            cursor.moveToFirst() // un SELECT con SUM siempre devuelve una fila
            wins = cursor.getInt(0)
            draws = cursor.getInt(1)
            losses = cursor.getInt(2)
            netBalance = cursor.getInt(3)
        }
        val record = db.rawQuery(
            "SELECT $COL_RECORD FROM $TABLE_PLAYER WHERE $COL_ID = 1", null
        ).use { cursor ->
            check(cursor.moveToFirst()) { "No existe la fila del jugador" }
            cursor.getInt(0)
        }
        return GameStats(
            wins = wins,
            draws = draws,
            losses = losses,
            netBalance = netBalance,
            record = record
        )
    }
}