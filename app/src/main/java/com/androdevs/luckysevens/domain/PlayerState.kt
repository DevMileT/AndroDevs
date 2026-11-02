package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 17:0
 *Year: 2026
 */

import java.time.LocalDate

/**
 * Foto del jugador en un momento dado. Es inmutable: para "cambiarlo" se crea una copia.
 * lastBonusDate es null si nunca ha reclamado el bonus.
 */
data class PlayerState(
    val balance: Int,
    val record: Int,
    val lastBonusDate: LocalDate? = null
) {
    init {
        // Segunda barrera (la primera es el CHECK de SQL).
        require(balance >= 0) { "El saldo no puede ser negativo: $balance" }
    }

    /**
     * Devuelve una copia con el saldo nuevo. El récord solo puede subir:
     * si el saldo nuevo lo supera, lo sustituye; si no, se conserva.
     * Sirve tanto tras un giro como tras reclamar el bonus.
     */
    fun withBalance(newBalance: Int): PlayerState =
        copy(balance = newBalance, record = maxOf(record, newBalance))
}