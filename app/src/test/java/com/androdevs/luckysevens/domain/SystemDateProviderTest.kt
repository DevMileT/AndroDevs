package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 18:0
 *Year: 2026
 */

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SystemDateProviderTest {

    // Se compara con la fecha antes y después de la llamada, para que la prueba no falle si
    // se ejecuta justo al cruzar la medianoche.
    @Test
    fun today_returnsTheCurrentDate() {
        val before = LocalDate.now()
        val value = SystemDateProvider().today()
        val after = LocalDate.now()
        assertTrue("Devolvió $value (antes $before, después $after)", value == before || value == after)
    }
}