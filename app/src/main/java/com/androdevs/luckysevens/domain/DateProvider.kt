package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 18:0
 *Year: 2026
 */

import java.time.LocalDate

/**
 * Contrato del reloj: "dime qué día es hoy".
 * BonusRules solo conoce esta interfaz, así las pruebas pueden usar un calendario falso
 * y cambiar el día a voluntad (RNF-06).
 */
interface DateProvider {
    /** La fecha de hoy según el calendario local del dispositivo. */
    fun today(): LocalDate
}