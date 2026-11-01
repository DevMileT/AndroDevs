package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 18:0
 *Year: 2026
 */

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Generador falso (los "dados trucados"): devuelve siempre los símbolos que le demos
 * y cuenta cuántas veces se le piden.
 */
private class FakeReelGenerator(private val symbols: List<Symbol>) : ReelGenerator {
    var calls = 0
        private set

    override fun nextSymbols(): List<Symbol> {
        calls++
        return symbols
    }
}

class GameEngineSpinTest {

    private val balance = GameConstants.INITIAL_BALANCE
    private val state = PlayerState(balance = balance, record = balance)
    private val bet = 20 // la apuesta de los ejemplos de la especificación (CA-04 a CA-07)
    private val anySymbols = listOf(Symbol.LEMON, Symbol.GRAPE, Symbol.ORANGE)
    private val threeSevens = listOf(Symbol.SEVEN, Symbol.SEVEN, Symbol.SEVEN)

    // ---------- Ayudas privadas ----------

    private fun spin(
        symbols: List<Symbol>,
        betAmount: Int = bet,
        from: PlayerState = state
    ): SpinResult = GameEngine(FakeReelGenerator(symbols)).spin(from, betAmount)

    private fun validBets() = GameConstants.MIN_BET..GameConstants.MAX_BET step GameConstants.BET_STEP

    /** Las 216 combinaciones (6 x 6 x 6). */
    private fun allCombinations(): List<List<Symbol>> =
        Symbol.entries.flatMap { first ->
            Symbol.entries.flatMap { second ->
                Symbol.entries.map { third -> listOf(first, second, third) }
            }
        }

    // ---------- Los ejemplos de la especificación (CA-04 a CA-07) ----------

    // CA-04: 7-7-7 con apuesta 20 -> premio = apuesta x multiplicador de tres sietes.
    @Test
    fun spin_threeSevens_paysThreeSevensMultiplier() {
        val result = spin(threeSevens)
        val expectedPrize = bet * PayoutTable.THREE_SEVENS
        assertEquals(expectedPrize, result.prize)
        assertEquals(balance - bet + expectedPrize, result.balanceAfter)
        assertEquals(SpinOutcome.WIN, result.outcome)
    }

    // CA-05: campana-campana-uva -> premio de par.
    @Test
    fun spin_twoBellsAndGrape_paysPair() {
        val result = spin(listOf(Symbol.BELL, Symbol.BELL, Symbol.GRAPE))
        val expectedPrize = bet * PayoutTable.PAIR
        assertEquals(expectedPrize, result.prize)
        assertEquals(balance - bet + expectedPrize, result.balanceAfter)
        assertEquals(SpinOutcome.WIN, result.outcome)
    }

    // CA-06: cereza-limón-uva -> se devuelve la apuesta y el saldo no cambia.
    @Test
    fun spin_cherryLemonGrape_returnsBetAndKeepsBalance() {
        val result = spin(listOf(Symbol.CHERRY, Symbol.LEMON, Symbol.GRAPE))
        assertEquals(bet * PayoutTable.SINGLE_CHERRY, result.prize)
        assertEquals(balance, result.balanceAfter)
        assertEquals(SpinOutcome.DRAW, result.outcome)
    }

    // CA-07: limón-limón-uva -> sin premio y el saldo baja la apuesta.
    @Test
    fun spin_lemonLemonGrape_paysNothing() {
        val result = spin(listOf(Symbol.LEMON, Symbol.LEMON, Symbol.GRAPE))
        assertEquals(bet * PayoutTable.NO_PRIZE, result.prize)
        assertEquals(balance - bet, result.balanceAfter)
        assertEquals(SpinOutcome.LOSS, result.outcome)
    }

    // ---------- Contenido del resultado y uso del generador ----------

    @Test
    fun spin_returnsTheBetAndTheSymbolsFromTheGenerator() {
        val symbols = listOf(Symbol.GRAPE, Symbol.ORANGE, Symbol.BELL)
        val result = spin(symbols, betAmount = 50)
        assertEquals(50, result.bet)
        assertEquals(symbols, result.symbols)
    }

    @Test
    fun spin_asksTheGeneratorExactlyOnce() {
        val generator = FakeReelGenerator(anySymbols)
        GameEngine(generator).spin(state, bet)
        assertEquals(1, generator.calls)
    }

    // ---------- Todas las combinaciones y todas las apuestas ----------

    // Para cada una de las 216 combinaciones y cada apuesta válida: premio, saldo final y
    // clasificación salen de la tabla. La clasificación esperada se deduce del multiplicador
    // (mayor que 1, igual a 1, cero), no de la comparación premio-apuesta que usa el motor.
    @Test
    fun everyCombinationAndBet_followsBalanceFormulaAndOutcomeRule() {
        for (symbols in allCombinations()) {
            val multiplier = PayoutTable.multiplierFor(symbols)
            for (betAmount in validBets()) {
                val result = spin(symbols, betAmount)
                val context = "$symbols, apuesta $betAmount"
                val expectedPrize = multiplier * betAmount
                val expectedOutcome = when {
                    multiplier > 1 -> SpinOutcome.WIN
                    multiplier == 1 -> SpinOutcome.DRAW
                    else -> SpinOutcome.LOSS
                }
                assertEquals(context, expectedPrize, result.prize)
                assertEquals(context, balance - betAmount + expectedPrize, result.balanceAfter)
                assertEquals(context, expectedOutcome, result.outcome)
            }
        }
    }

    // Conteos acordados en las secciones 2.3 y 2.6: 51 victorias, 69 empates y 96 derrotas.
    @Test
    fun allCombinations_outcomeCountsMatchAgreedDistribution() {
        val counts = allCombinations().groupingBy { spin(it).outcome }.eachCount()
        val expected = mapOf(SpinOutcome.WIN to 51, SpinOutcome.DRAW to 69, SpinOutcome.LOSS to 96)
        assertEquals(expected, counts)
    }

    // CA-11: con cualquier saldo de 0 a 200 y cualquier apuesta válida, el saldo final nunca es negativo.
    @Test
    fun balanceNeverNegative_forEveryValidBetAndBalance() {
        val combinations = allCombinations()
        for (startBalance in 0..2 * GameConstants.MAX_BET) {
            val from = PlayerState(balance = startBalance, record = startBalance)
            for (betAmount in validBets().filter { GameEngine.isValidBet(startBalance, it) }) {
                for (symbols in combinations) {
                    val result = spin(symbols, betAmount, from)
                    assertTrue(
                        "Saldo $startBalance, apuesta $betAmount, $symbols",
                        result.balanceAfter >= 0
                    )
                }
            }
        }
    }

    // ---------- Apuestas inválidas (decisión 2 de D-12) ----------

    @Test(expected = IllegalArgumentException::class)
    fun spin_betNotAStep_throws() {
        spin(anySymbols, betAmount = GameConstants.MIN_BET + GameConstants.BET_STEP / 2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun spin_betAboveMax_throws() {
        spin(anySymbols, betAmount = GameConstants.MAX_BET + GameConstants.BET_STEP)
    }

    @Test(expected = IllegalArgumentException::class)
    fun spin_betAboveBalance_throws() {
        val poor = PlayerState(balance = 3 * GameConstants.MIN_BET, record = balance)
        spin(anySymbols, betAmount = 4 * GameConstants.MIN_BET, from = poor)
    }

    @Test(expected = IllegalArgumentException::class)
    fun spin_balanceBelowMin_throws() {
        val broke = PlayerState(balance = GameConstants.MIN_BET - 1, record = balance)
        spin(anySymbols, betAmount = GameConstants.MIN_BET, from = broke)
    }

    // Una apuesta inválida se rechaza antes de pedir símbolos: no se gasta azar.
    @Test
    fun spin_invalidBet_doesNotAskTheGenerator() {
        val generator = FakeReelGenerator(anySymbols)
        try {
            GameEngine(generator).spin(state, GameConstants.MAX_BET + GameConstants.BET_STEP)
        } catch (e: IllegalArgumentException) {
            // esperado: lo que comprobamos es el contador de abajo
        }
        assertEquals(0, generator.calls)
    }

    // ---------- Con PlayerState (récord) ----------

    @Test
    fun spin_bigWin_thenWithBalanceRaisesRecord() {
        val result = spin(threeSevens, betAmount = GameConstants.MAX_BET)
        val updated = state.withBalance(result.balanceAfter)
        val expected = balance - GameConstants.MAX_BET + GameConstants.MAX_BET * PayoutTable.THREE_SEVENS
        assertEquals(expected, updated.balance)
        assertEquals(expected, updated.record)
    }

    @Test
    fun spin_loss_thenWithBalanceKeepsRecord() {
        val result = spin(listOf(Symbol.LEMON, Symbol.GRAPE, Symbol.ORANGE))
        val updated = state.withBalance(result.balanceAfter)
        assertEquals(balance - bet, updated.balance)
        assertEquals(balance, updated.record)
    }
}