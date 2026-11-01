package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 16:0
 *Year: 2026
 */

/**
 * Tabla de premios: la única definición de cuánto paga cada combinación .
 * Premio total = multiplicador x apuesta.
 */
object PayoutTable {

    // Multiplicadores con nombre: la tarjeta de premios de Juego los leerá (D-08, D-12).
    const val THREE_SEVENS = 10
    const val THREE_BELLS = 5
    const val THREE_OF_A_KIND = 3   // Tres iguales de cereza, limón, uva o naranja
    const val PAIR = 2              // Exactamente dos sietes, dos campanas o dos cerezas
    const val SINGLE_CHERRY = 1     // Exactamente una cereza: se devuelve la apuesta
    const val NO_PRIZE = 0

    /**
     * Devuelve el multiplicador de la combinación. Solo se aplica UN premio: el de mayor
     * multiplicador. Por eso las reglas se comprueban de la más valiosa a la menos valiosa
     * y gana la primera que se cumple.
     */
    fun multiplierFor(symbols: List<Symbol>): Int {
        require(symbols.size == GameConstants.REEL_COUNT) {
            "Se esperaban ${GameConstants.REEL_COUNT} símbolos y llegaron ${symbols.size}"
        }

        val allEqual = symbols.all { it == symbols.first() }
        val sevens = symbols.count { it == Symbol.SEVEN }
        val bells = symbols.count { it == Symbol.BELL }
        val cherries = symbols.count { it == Symbol.CHERRY }

        return when {
            allEqual && symbols.first() == Symbol.SEVEN -> THREE_SEVENS
            allEqual && symbols.first() == Symbol.BELL -> THREE_BELLS
            allEqual -> THREE_OF_A_KIND
            sevens == 2 || bells == 2 || cherries == 2 -> PAIR
            cherries == 1 -> SINGLE_CHERRY
            else -> NO_PRIZE
        }
    }
}