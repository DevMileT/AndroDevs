package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 17:0
 *Year: 2026
 */

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    private val rich = GameConstants.INITIAL_BALANCE // saldo de sobra para cualquier apuesta

    // ---------- isValidBet  ----------

    @Test
    fun isValidBet_everyStepInRange_isValidWithEnoughBalance() {
        for (bet in GameConstants.MIN_BET..GameConstants.MAX_BET step GameConstants.BET_STEP) {
            assertTrue("Apuesta $bet", GameEngine.isValidBet(rich, bet))
        }
    }

    @Test
    fun isValidBet_belowMin_isInvalid() {
        for (bet in listOf(0, -GameConstants.MIN_BET, GameConstants.MIN_BET - 1)) {
            assertFalse("Apuesta $bet", GameEngine.isValidBet(rich, bet))
        }
    }

    @Test
    fun isValidBet_aboveMax_isInvalid() {
        for (bet in listOf(GameConstants.MAX_BET + GameConstants.BET_STEP, GameConstants.MAX_BET + 1)) {
            assertFalse("Apuesta $bet", GameEngine.isValidBet(rich, bet))
        }
    }

    // Dentro del rango pero que no es un paso de BET_STEP: 15 y 99 con los valores actuales.
    @Test
    fun isValidBet_notAStepOfBetStep_isInvalid() {
        val offStep = listOf(
            GameConstants.MIN_BET + GameConstants.BET_STEP / 2,
            GameConstants.MAX_BET - 1
        )
        for (bet in offStep) {
            assertFalse("Apuesta $bet", GameEngine.isValidBet(rich, bet))
        }
    }

    @Test
    fun isValidBet_greaterThanBalance_isInvalid() {
        val balance = 3 * GameConstants.MIN_BET
        assertFalse(GameEngine.isValidBet(balance, balance + GameConstants.BET_STEP))
    }

    @Test
    fun isValidBet_equalToBalance_isValid() {
        val balance = 3 * GameConstants.MIN_BET
        assertTrue(GameEngine.isValidBet(balance, balance))
    }

    @Test
    fun isValidBet_balanceBelowMin_noBetIsValid() {
        for (balance in listOf(0, GameConstants.MIN_BET - 1)) {
            for (bet in GameConstants.MIN_BET..GameConstants.MAX_BET step GameConstants.BET_STEP) {
                assertFalse("Saldo $balance, apuesta $bet", GameEngine.isValidBet(balance, bet))
            }
        }
    }

    // ---------- maxBetFor (CA-12) ----------

    @Test
    fun maxBetFor_richPlayer_returnsMaxBet() {
        assertEquals(GameConstants.MAX_BET, GameEngine.maxBetFor(rich))
        assertEquals(GameConstants.MAX_BET, GameEngine.maxBetFor(GameConstants.MAX_BET))
    }

    @Test
    fun maxBetFor_roundsDownToAStep() {
        assertEquals(30, GameEngine.maxBetFor(35))   // 35 -> 30
        assertEquals(90, GameEngine.maxBetFor(99))   // 99 -> 90
        assertEquals(100, GameEngine.maxBetFor(105)) // nunca más que MAX_BET
    }

    @Test
    fun maxBetFor_exactlyMinBalance_returnsMinBet() {
        assertEquals(GameConstants.MIN_BET, GameEngine.maxBetFor(GameConstants.MIN_BET))
    }

    @Test
    fun maxBetFor_belowMinBalance_returnsZero() {
        assertEquals(0, GameEngine.maxBetFor(GameConstants.MIN_BET - 1))
        assertEquals(0, GameEngine.maxBetFor(0))
    }

    // Comprobación cruzada: para cada saldo de 0 a 200, la mayor apuesta que isValidBet acepta
    // (buscada una a una) debe coincidir con lo que calcula maxBetFor.
    @Test
    fun maxBetFor_matchesBruteForceSearchForEveryBalance() {
        for (balance in 0..2 * GameConstants.MAX_BET) {
            val validBets = (GameConstants.MIN_BET..GameConstants.MAX_BET step GameConstants.BET_STEP)
                .filter { GameEngine.isValidBet(balance, it) }
            assertEquals("Saldo $balance", validBets.maxOrNull() ?: 0, GameEngine.maxBetFor(balance))
        }
    }
}