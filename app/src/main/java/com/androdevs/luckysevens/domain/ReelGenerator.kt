package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Seven
 *Created by: Milena
 *On: 01/11/2026 at 17:0
 *Year: 2026
 */

/**
 * Contrato del generador de resultados: "dame los símbolos de un giro".
 * GameEngine solo conoce esta interfaz, no de dónde salen los símbolos. Así las pruebas
 * pueden usar un generador falso con resultados conocidos.
 */
interface ReelGenerator {
    /** Devuelve un símbolo por rodillo, en orden (izquierda a derecha). */
    fun nextSymbols(): List<Symbol>
}