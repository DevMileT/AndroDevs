package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 17:0
 *Year: 2026
 */

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PlayerStateTest {

    private val base = GameConstants.INITIAL_BALANCE

    @Test(expected = IllegalArgumentException::class)
    fun negativeBalance_throwsException() {
        PlayerState(balance = -10, record = base)
    }

    @Test
    fun zeroBalance_isAllowed() {
        val state = PlayerState(balance = 0, record = base)
        assertEquals(0, state.balance)
    }

    @Test(expected = IllegalArgumentException::class)
    fun withBalance_negative_throwsException() {
        PlayerState(balance = base, record = base).withBalance(-10)
    }

    @Test
    fun withBalance_higherThanRecord_raisesRecord() {
        val updated = PlayerState(balance = base, record = base).withBalance(base + 200)
        assertEquals(base + 200, updated.balance)
        assertEquals(base + 200, updated.record)
    }

    @Test
    fun withBalance_equalToRecord_keepsRecord() {
        val updated = PlayerState(balance = base - 100, record = base).withBalance(base)
        assertEquals(base, updated.balance)
        assertEquals(base, updated.record)
    }

    // CA-24: si el saldo baja, el récord se conserva.
    @Test
    fun withBalance_lowerThanRecord_keepsRecord() {
        val updated = PlayerState(balance = base, record = base).withBalance(base - 300)
        assertEquals(base - 300, updated.balance)
        assertEquals(base, updated.record)
    }

    @Test
    fun withBalance_keepsLastBonusDate() {
        val date = LocalDate.of(2026, 11, 1)
        val updated = PlayerState(balance = base, record = base, lastBonusDate = date).withBalance(base + 50)
        assertEquals(date, updated.lastBonusDate)
    }

    // El estado es inmutable: withBalance devuelve una copia y no toca el original.
    @Test
    fun withBalance_doesNotModifyOriginal() {
        val original = PlayerState(balance = base, record = base)
        original.withBalance(base + 500)
        assertEquals(base, original.balance)
        assertEquals(base, original.record)
    }

    // CA-24: a lo largo de una secuencia de saldos, el récord nunca baja.
    @Test
    fun record_neverDecreasesAlongASequence() {
        var state = PlayerState(balance = base, record = base)
        for (newBalance in listOf(base + 100, base - 400, base + 300, 0, base + 250)) {
            val previousRecord = state.record
            state = state.withBalance(newBalance)
            assertTrue("El récord bajó de $previousRecord a ${state.record}", state.record >= previousRecord)
        }
        // El mayor saldo de la secuencia fue base + 300.
        assertEquals(base + 300, state.record)
    }
}