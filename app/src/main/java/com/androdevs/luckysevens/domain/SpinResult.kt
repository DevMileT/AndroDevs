package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 17:0
 *Year: 2026
 */

/**
 * Parte del árbitro tras un giro: todo lo necesario para mostrar el resultado y guardarlo.
 * prize es el premio TOTAL (multiplicador x apuesta), no el beneficio neto.
 */
data class SpinResult(
    val bet: Int,
    val symbols: List<Symbol>,
    val prize: Int,
    val outcome: SpinOutcome,
    val balanceAfter: Int
)