package com.androdevs.luckysevens.domain

/**
 *Project: Luvky Seven
 *Created by: Milena
 *On: 01/11/2026 at 17:0
 *Year: 2026
 */

/**
 * En el juego se resuelve un giro con las reglas de la especificación.
 * Recibe el generador de símbolos desde fuera, así las pruebas pueden darle uno con resultados
 * conocidos. No guarda nada ni tiene memoria: cada llamada a spin() es independiente.
 */
class GameEngine(private val reelGenerator: ReelGenerator) {

    /**
     * Resuelve un giro: saldo final = saldo anterior - apuesta + premio.
     * Lanza IllegalArgumentException si la apuesta no es válida.
     * No modifica el estado: devuelve el resultado, y quien lo guarde calculará el nuevo
     * PlayerState con state.withBalance(result.balanceAfter).
     */
    fun spin(state: PlayerState, bet: Int): SpinResult {
        require(isValidBet(state.balance, bet)) {
            "Apuesta inválida: $bet con saldo ${state.balance}"
        }

        val symbols = reelGenerator.nextSymbols()
        val prize = PayoutTable.multiplierFor(symbols) * bet

        val outcome = when {
            prize > bet -> SpinOutcome.WIN
            prize == bet -> SpinOutcome.DRAW
            else -> SpinOutcome.LOSS
        }

        return SpinResult(
            bet = bet,
            symbols = symbols,
            prize = prize,
            outcome = outcome,
            balanceAfter = state.balance - bet + prize
        )
    }

    companion object {

        /**
         * Una apuesta es válida si está entre MIN_BET y MAX_BET, es un paso exacto de BET_STEP
         * a partir del mínimo (10, 20, ..., 100) y no supera el saldo.
         */
        fun isValidBet(balance: Int, bet: Int): Boolean =
            bet in GameConstants.MIN_BET..GameConstants.MAX_BET &&
                    (bet - GameConstants.MIN_BET) % GameConstants.BET_STEP == 0 &&
                    bet <= balance

        /**
         * La mayor apuesta permitida con ese saldo, o 0 si no hay ninguna (saldo menor que MIN_BET).
         * La interfaz la usa para ajustar la apuesta cuando el saldo baja.
         */
        fun maxBetFor(balance: Int): Int {
            if (balance < GameConstants.MIN_BET) return 0
            val limit = minOf(balance, GameConstants.MAX_BET)
            val stepsAboveMin = (limit - GameConstants.MIN_BET) / GameConstants.BET_STEP
            return GameConstants.MIN_BET + stepsAboveMin * GameConstants.BET_STEP
        }
    }
}