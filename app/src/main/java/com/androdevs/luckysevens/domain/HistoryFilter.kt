package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 19:0
 *Year: 2026
 */
/**
 * Filtro del historial por resultado .
 * Cada filtro sabe qué resultado le corresponde; ALL no filtra y por eso no tiene ninguno.
 */
enum class HistoryFilter(val outcome: SpinOutcome?) {
    ALL(null),
    WINS(SpinOutcome.WIN),
    DRAWS(SpinOutcome.DRAW),
    LOSSES(SpinOutcome.LOSS)
}