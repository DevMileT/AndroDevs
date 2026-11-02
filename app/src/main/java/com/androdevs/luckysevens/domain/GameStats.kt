package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 19:0
 *Year: 2026
 */

/**
 * Estadísticas del jugador, calculadas desde el historial.
 * netBalance es la suma de (premio - apuesta) de todos los giros, sin contar el bonus ,
 * así que puede ser negativo.
 */
data class GameStats(
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val netBalance: Int,
    val record: Int
) {
    /**Total de partidas: no se guarda, se calcula, así nunca puede desincronizarse.*/
    val total: Int get() = wins + draws + losses
}