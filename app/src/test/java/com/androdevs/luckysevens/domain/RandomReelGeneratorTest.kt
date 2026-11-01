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
import kotlin.math.abs
import kotlin.random.Random

class RandomReelGeneratorTest {

    @Test
    fun nextSymbols_returnsOneSymbolPerReel() {
        val generator = RandomReelGenerator(Random(42))
        assertEquals(GameConstants.REEL_COUNT, generator.nextSymbols().size)
    }

    // Con la misma semilla, el "azar" es repetible: así las pruebas no fallan a veces sí y a veces no.
    @Test
    fun sameSeed_producesSameSequence() {
        val a = RandomReelGenerator(Random(42))
        val b = RandomReelGenerator(Random(42))
        repeat(100) {
            assertEquals(a.nextSymbols(), b.nextSymbols())
        }
    }

    // Prueba estadística: en 60.000 giros, cada símbolo debe salir ≈ 1/6 de las veces en cada rodillo.
    @Test
    fun eachReel_showsEverySymbolWithRoughlyEqualFrequency() {
        val spins = 60_000
        val generator = RandomReelGenerator(Random(42))
        val results = List(spins) { generator.nextSymbols() }

        val expected = spins / Symbol.entries.size // 10.000 veces por símbolo y rodillo
        val tolerance = expected * 5 / 100         // ±5 % = ±500

        for (reel in 0 until GameConstants.REEL_COUNT) {
            val counts = results.groupingBy { it[reel] }.eachCount()
            for (symbol in Symbol.entries) {
                val count = counts[symbol] ?: 0
                assertTrue(
                    "Rodillo $reel, $symbol: salió $count veces, esperado $expected ± $tolerance",
                    abs(count - expected) <= tolerance
                )
            }
        }
    }
}