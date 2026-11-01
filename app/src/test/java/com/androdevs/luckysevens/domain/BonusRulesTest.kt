package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 18:0
 *Year: 2026
 */

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** Calendario falso: "hoy" es la fecha que le demos, y se puede cambiar durante la prueba. */
private class FakeDateProvider(var currentDate: LocalDate) : DateProvider {
    override fun today(): LocalDate = currentDate
}

class BonusRulesTest {

    // JUnit crea un objeto nuevo de esta clase para cada prueba: el reloj vuelve a empezar en "hoy".
    private val today = LocalDate.of(2026, 11, 1)
    private val clock = FakeDateProvider(today)
    private val rules = BonusRules(clock)

    @Test
    fun neverClaimed_canClaim() {
        assertTrue(rules.canClaim(null))
    }

    @Test
    fun claimedToday_cannotClaim() {
        assertFalse(rules.canClaim(today))
    }

    // CA-18: al día natural siguiente se puede reclamar.
    @Test
    fun claimedYesterday_canClaim() {
        assertTrue(rules.canClaim(today.minusDays(1)))
    }

    // CA-18: pasar varios días sin abrir la app permite reclamar (y el importe de 50 no se acumula,
    // pero eso lo garantiza el UPDATE de la Fase 6).
    @Test
    fun claimedSeveralDaysAgo_canClaim() {
        for (days in listOf(2L, 7L, 30L, 365L)) {
            assertTrue("Hace $days días", rules.canClaim(today.minusDays(days)))
        }
    }

    // Limitación documentada (RNF-12, CA-26): si el reloj retrocede, la fecha guardada queda en el
    // futuro y la regla "distinta de hoy" deja reclamar.
    @Test
    fun claimedInTheFuture_canClaim() {
        assertTrue(rules.canClaim(today.plusDays(1)))
    }

    // CA-20: con la app abierta, al cambiar el día el mismo BonusRules pasa de "no" a "sí".
    @Test
    fun dayChangesWhileAppIsOpen_becomesClaimable() {
        assertFalse(rules.canClaim(today))
        clock.currentDate = today.plusDays(1)
        assertTrue(rules.canClaim(today))
    }

    @Test
    fun monthBoundary_canClaim() {
        clock.currentDate = LocalDate.of(2026, 11, 1)
        assertTrue(rules.canClaim(LocalDate.of(2026, 10, 31)))
    }

    @Test
    fun yearBoundary_canClaim() {
        clock.currentDate = LocalDate.of(2027, 1, 1)
        assertTrue(rules.canClaim(LocalDate.of(2026, 12, 31)))
    }

    // Protege contra comparar solo el día del mes: mismo día, mes o año distintos.
    @Test
    fun sameDayOfMonthInAnotherMonthOrYear_canClaim() {
        assertTrue(rules.canClaim(LocalDate.of(2026, 10, 1)))
        assertTrue(rules.canClaim(LocalDate.of(2025, 11, 1)))
    }

    // Fija el formato AAAA-MM-DD que D-04 usa para last_bonus_date. Esta prueba comprueba el
    // comportamiento de Java, no código nuestro: documenta el supuesto en el que se apoyará la Fase 6.
    @Test
    fun isoText_isYearMonthDayWithZeroPadding() {
        assertEquals("2026-11-01", LocalDate.of(2026, 11, 1).toString())
        assertEquals("2026-01-05", LocalDate.of(2026, 1, 5).toString())
        assertEquals(LocalDate.of(2026, 1, 5), LocalDate.parse("2026-01-05"))
    }
}