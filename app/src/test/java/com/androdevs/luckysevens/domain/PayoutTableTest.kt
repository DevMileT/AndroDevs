package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 16:0
 *Year: 2026
 */
import org.junit.Assert.assertEquals
import org.junit.Test

class PayoutTableTest {

    // ---------- Ayudas privadas ----------

    /** Comprueba la combinación con el símbolo distinto en las tres posiciones posibles. */
    private fun assertPairInAnyPosition(pairSymbol: Symbol, other: Symbol, expected: Int) {
        val arrangements = listOf(
            listOf(pairSymbol, pairSymbol, other),
            listOf(pairSymbol, other, pairSymbol),
            listOf(other, pairSymbol, pairSymbol)
        )
        for (symbols in arrangements) {
            assertEquals("Combinación $symbols", expected, PayoutTable.multiplierFor(symbols))
        }
    }

    /** Las 216 combinaciones (6 x 6 x 6): un símbolo por rodillo, todas las posibilidades. */
    private fun allCombinations(): List<List<Symbol>> =
        Symbol.entries.flatMap { first ->
            Symbol.entries.flatMap { second ->
                Symbol.entries.map { third -> listOf(first, second, third) }
            }
        }

    // ---------- Tríos ----------

    @Test
    fun threeSevens_returnsThreeSevensMultiplier() {
        val symbols = listOf(Symbol.SEVEN, Symbol.SEVEN, Symbol.SEVEN)
        assertEquals(PayoutTable.THREE_SEVENS, PayoutTable.multiplierFor(symbols))
    }

    @Test
    fun threeBells_returnsThreeBellsMultiplier() {
        val symbols = listOf(Symbol.BELL, Symbol.BELL, Symbol.BELL)
        assertEquals(PayoutTable.THREE_BELLS, PayoutTable.multiplierFor(symbols))
    }

    @Test
    fun threeOfAKindOfOtherSymbols_returnsThreeOfAKindMultiplier() {
        val others = Symbol.entries - listOf(Symbol.SEVEN, Symbol.BELL)
        for (symbol in others) {
            assertEquals(
                "Tres $symbol",
                PayoutTable.THREE_OF_A_KIND,
                PayoutTable.multiplierFor(listOf(symbol, symbol, symbol))
            )
        }
    }

    // ---------- Exactamente dos iguales ----------

    @Test
    fun twoSevens_returnsPair() =
        assertPairInAnyPosition(Symbol.SEVEN, Symbol.LEMON, PayoutTable.PAIR)

    @Test
    fun twoBells_returnsPair() =
        assertPairInAnyPosition(Symbol.BELL, Symbol.GRAPE, PayoutTable.PAIR)

    @Test
    fun twoCherries_returnsPair() =
        assertPairInAnyPosition(Symbol.CHERRY, Symbol.ORANGE, PayoutTable.PAIR)

    // una cereza junto a un par de sietes o de campanas paga x2, no x1.
    @Test
    fun cherryWithPairOfSevens_paysPairNotSingleCherry() =
        assertPairInAnyPosition(Symbol.SEVEN, Symbol.CHERRY, PayoutTable.PAIR)

    @Test
    fun cherryWithPairOfBells_paysPairNotSingleCherry() =
        assertPairInAnyPosition(Symbol.BELL, Symbol.CHERRY, PayoutTable.PAIR)

    // ---------- Una cereza y sin premio ----------

    // cereza-limón-uva devuelve la apuesta.
    @Test
    fun exactlyOneCherry_returnsSingleCherry() {
        val a = listOf(Symbol.CHERRY, Symbol.LEMON, Symbol.GRAPE)
        val b = listOf(Symbol.LEMON, Symbol.GRAPE, Symbol.CHERRY)
        assertEquals(PayoutTable.SINGLE_CHERRY, PayoutTable.multiplierFor(a))
        assertEquals(PayoutTable.SINGLE_CHERRY, PayoutTable.multiplierFor(b))
    }

    // Un par de limones no paga, pero la cereza suelta sí: x1.
    @Test
    fun pairOfLemonsWithCherry_returnsSingleCherry() =
        assertPairInAnyPosition(Symbol.LEMON, Symbol.CHERRY, PayoutTable.SINGLE_CHERRY)

    @Test
    fun noCherryAndNoPrizePair_returnsNoPrize() {
        val a = listOf(Symbol.LEMON, Symbol.GRAPE, Symbol.ORANGE)
        val b = listOf(Symbol.SEVEN, Symbol.BELL, Symbol.LEMON) // un siete y una campana sueltos
        assertEquals(PayoutTable.NO_PRIZE, PayoutTable.multiplierFor(a))
        assertEquals(PayoutTable.NO_PRIZE, PayoutTable.multiplierFor(b))
    }

    // limón-limón-uva no paga.
    @Test
    fun pairOfLemonsWithGrape_returnsNoPrize() =
        assertPairInAnyPosition(Symbol.LEMON, Symbol.GRAPE, PayoutTable.NO_PRIZE)

    // ---------- Entradas incorrectas ----------

    @Test(expected = IllegalArgumentException::class)
    fun tooFewSymbols_throwsException() {
        PayoutTable.multiplierFor(listOf(Symbol.SEVEN, Symbol.SEVEN))
    }

    @Test(expected = IllegalArgumentException::class)
    fun tooManySymbols_throwsException() {
        PayoutTable.multiplierFor(listOf(Symbol.SEVEN, Symbol.SEVEN, Symbol.SEVEN, Symbol.SEVEN))
    }

    // ---------- las 216 combinaciones ----------

    @Test
    fun allCombinations_matchAgreedDistribution() {
        val combinations = allCombinations()
        assertEquals(216, combinations.size)

        val countByMultiplier = combinations
            .groupingBy { PayoutTable.multiplierFor(it) }
            .eachCount()

        val expected = mapOf(0 to 96, 1 to 69, 2 to 45, 3 to 4, 5 to 1, 10 to 1)
        assertEquals(expected, countByMultiplier)
    }

    // El RTP se calcula con la tabla: apostando 1 a cada combinación, lo devuelto entre lo apostado.
    @Test
    fun allCombinations_rtpIsAbout8611Percent() {
        val combinations = allCombinations()
        val totalReturned = combinations.sumOf { PayoutTable.multiplierFor(it) }
        val rtp = totalReturned.toDouble() / combinations.size
        assertEquals(0.8611, rtp, 0.0001)
    }
}