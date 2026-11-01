package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 15:0
 *Year: 2026
 */


/**
 * Constantes del juego. Cada valor vive aquí y solo aquí.
 */
object GameConstants {
    const val INITIAL_BALANCE = 1000 // Saldo con el que empieza el jugador
    const val MIN_BET = 10           // Apuesta mínima
    const val MAX_BET = 100          // Apuesta máxima
    const val BET_STEP = 10          // Las apuestas suben de 10 en 10
    const val DAILY_BONUS = 50       // Monedas del bonus diario
    const val REEL_COUNT = 3         // Número de rodillos de la máquina
}