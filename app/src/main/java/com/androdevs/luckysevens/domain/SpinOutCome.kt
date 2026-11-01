package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 15:0
 *Year: 2026
 */


/** Clasificación de un giro: premio mayor que la apuesta, igual, o cero. */
enum class SpinOutcome {
    WIN,
    DRAW,
    LOSS
}