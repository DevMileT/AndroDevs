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
 * UPDATE condicionado de D-04 usa la misma condición. Un reloj manipulado es una limitación
 * documentada.
 *
 * Esta clase solo decide si el botón está activo. La reclamación real (sumar 50, guardar la fecha
 * y evitar dos pulsaciones rápidas) la hace el UPDATE atómico de la Fase 6.
 */
class BonusRules(private val dateProvider: DateProvider) {

    fun canClaim(lastClaimDate: LocalDate?): Boolean =
        lastClaimDate == null || lastClaimDate != dateProvider.today()
}