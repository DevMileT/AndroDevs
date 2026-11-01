package com.androdevs.luckysevens.data

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 21:0
 *Year: 2026
 */

import com.androdevs.luckysevens.domain.GameRepository
import com.androdevs.luckysevens.domain.PlayerState
import com.androdevs.luckysevens.domain.SpinResult
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDate

/**
 * Implementación de GameRepository con SQLite (D-03). Todo el trabajo de base de datos se
 * ejecuta en un hilo de fondo (subscribeOn), nunca en el principal (RNF-03).
 */
class GameRepositoryImpl(
    private val helper: GameDatabaseHelper
) : GameRepository {

    override fun getPlayerState(): Single<PlayerState> =
        Single.fromCallable { readPlayerState() }
            .subscribeOn(Schedulers.io())

    override fun spin(bet: Int): Single<SpinResult> =
        TODO("Paso 6.4: giro en una transacción")

    override fun claimBonus(): Single<Boolean> =
        TODO("Paso 6.5: UPDATE condicionado del bonus")

    /** Lee la única fila de la tabla player. Debe llamarse desde un hilo de fondo. */
    private fun readPlayerState(): PlayerState {
        val sql = "SELECT ${GameDatabaseHelper.COL_BALANCE}, " +
                "${GameDatabaseHelper.COL_RECORD}, " +
                "${GameDatabaseHelper.COL_LAST_BONUS_DATE} " +
                "FROM ${GameDatabaseHelper.TABLE_PLAYER} " +
                "WHERE ${GameDatabaseHelper.COL_ID} = 1"
        return helper.readableDatabase.rawQuery(sql, null).use { cursor ->
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