package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 18:0
 *Year: 2026
 */

import java.time.LocalDate

/**
 * Regla del bonus diario: se puede reclamar si nunca se reclamó (null)
 * o si la última reclamación NO fue hoy. Es "distinta de hoy", no "anterior a hoy", porque el
 * UPDATE condicionado de usa la misma condición. Un reloj manipulado es una limitación
 * documentada.
 */
class BonusRules(private val dateProvider: DateProvider) {

    fun canClaim(lastClaimDate: LocalDate?): Boolean =
        lastClaimDate == null || lastClaimDate != dateProvider.today()
}