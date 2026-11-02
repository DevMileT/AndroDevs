package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 19:0
 *Year: 2026
 */

import io.reactivex.rxjava3.core.Single

/**
 * Consulta del historial y las estadísticas. Solo lee: nunca modifica la partida
 * (eso es de GameRepository). La implementación con SQLite vive en la capa de datos .
 *
 * Igual que GameRepository, la implementación ejecuta el trabajo en un hilo de fondo
 * y quien llame solo debe añadir observeOn(AndroidSchedulers.mainThread()).
 */
interface HistoryRepository {

    /**
     * Partidas guardadas, de la más reciente a la más antigua y sin límite de entradas .
     * Con ALL devuelve todas; con otro filtro, solo las de ese resultado.
     * Sin partidas devuelve una lista vacía, no un error.
     */
    fun getHistory(filter: HistoryFilter): Single<List<SpinRecord>>

    /**
     * Estadísticas calculadas desde el historial. El récord se lee del jugador,
     * no del historial.
     */
    fun getStats(): Single<GameStats>
}