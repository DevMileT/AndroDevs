package com.androdevs.luckysevens.domain

/**
 *Project: Luvky Seven
 *Created by: Milena
 *On: 01/11/2026 at 17:0
 *Year: 2026
 */

/**
 * Árbitro del juego. En el paso 5.4c se añadirá spin(); por ahora solo las reglas de la apuesta,
 * que están en un companion object para poder usarlas sin crear un GameEngine.
 */
class GameEngine {

    companion object {

        /**
         * Una apuesta es válida si está entre MIN_BET y MAX_BET, es un paso exacto de BET_STEP
         * a partir del mínimo (10, 20, ..., 100) y no supera el saldo (RF-04, CA-03).
         */
        fun isValidBet(balance: Int, bet: Int): Boolean =
            bet in GameConstants.MIN_BET..GameConstants.MAX_BET &&
                    (bet - GameConstants.MIN_BET) % GameConstants.BET_STEP == 0 &&
                    bet <= balance

        /**
         * La mayor apuesta permitida con ese saldo, o 0 si no hay ninguna (saldo menor que MIN_BET).
         * La interfaz la usa para ajustar la apuesta cuando el saldo baja (CA-12).
         */
        fun maxBetFor(balance: Int): Int {
            if (balance < GameConstants.MIN_BET) return 0
            val limit = minOf(balance, GameConstants.MAX_BET)
            val stepsAboveMin = (limit - GameConstants.MIN_BET) / GameConstants.BET_STEP
            return GameConstants.MIN_BET + stepsAboveMin * GameConstants.BET_STEP
        }
    }
}