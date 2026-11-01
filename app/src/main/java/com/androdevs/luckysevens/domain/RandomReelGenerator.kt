package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 17:0
 *Year: 2026
 */

import kotlin.random.Random

/**
 * Generador real: cada rodillo se sortea de forma independiente y los seis símbolos
 * tienen la misma probabilidad (1/6), como pide la sección 2 de la especificación.
 */
class RandomReelGenerator(
    private val random: Random = Random.Default
) : ReelGenerator {

    override fun nextSymbols(): List<Symbol> =
        List(GameConstants.REEL_COUNT) {
            Symbol.entries[random.nextInt(Symbol.entries.size)]
        }
}