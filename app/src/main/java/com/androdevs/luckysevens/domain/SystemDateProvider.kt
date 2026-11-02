package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 18:0
 *Year: 2026
 */

import java.time.LocalDate

/**
 * Reloj real: lee la fecha del dispositivo. LocalDate.now() usa la zona horaria del móvil,
 * así que "día natural según la fecha local" se cumple sin más código.
 * LocalDate es de Java, no de Android: con minSdk 26 está disponible de forma nativa.
 */
class SystemDateProvider : DateProvider {
    override fun today(): LocalDate = LocalDate.now()
}