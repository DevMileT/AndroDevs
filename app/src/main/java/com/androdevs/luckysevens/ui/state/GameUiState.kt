package com.androdevs.luckysevens.ui.state

/**
 *Project: Lucky Seven
 *Created by: Milena
 *On: 01/11/2026 at 22:0
 *Year: 2026
 */

import com.androdevs.luckysevens.domain.GameConstants
import com.androdevs.luckysevens.domain.GameEngine
import com.androdevs.luckysevens.domain.SpinResult

/** Avisos de saldo insuficiente. La pantalla elige el texto desde strings.xml. */
enum class GameWarning { NO_FUNDS_BONUS_AVAILABLE, NO_FUNDS_BONUS_CLAIMED }

/** Errores que puede mostrar la interfaz. Son un tipo y no un texto. */
enum class GameError { LOAD_FAILED, SPIN_FAILED, BONUS_FAILED }

/**
 * Todo lo que la pantalla de Juego necesita ver. Los datos guardados son los de arriba; lo
 * demás se DERIVA y no se guarda, así no puede desincronizarse.
 * balance es el saldo VISIBLE: durante un giro es el anterior al giro.
 */
data class GameUiState(
    val balance: Int = 0,
    val bet: Int = GameConstants.MIN_BET,
    val isLoading: Boolean = true,
    val isSpinning: Boolean = false,
    val lastResult: SpinResult? = null,
    val canClaimBonus: Boolean = false,
    val error: GameError? = null
) {
    /** La apuesta más alta permitida con el saldo actual (0 si no hay ninguna). */
    val maxBet: Int get() = GameEngine.maxBetFor(balance)

    val canPlay: Boolean get() = maxBet >= GameConstants.MIN_BET

    /** Apuesta que se muestra: se ajusta sola si el saldo baja de la elegida. */
    val shownBet: Int get() = if (canPlay) bet.coerceAtMost(maxBet) else GameConstants.MIN_BET

    val canDecrease: Boolean get() = canPlay && !isSpinning && shownBet > GameConstants.MIN_BET
    val canIncrease: Boolean get() = canPlay && !isSpinning && shownBet < maxBet

    /** Girar se bloquea mientras carga, mientras gira o si no hay saldo. */
    val canSpin: Boolean get() = !isLoading && canPlay && !isSpinning

    /** Resultado para el panel: solo se muestra cuando la animación ha terminado. */
    val resultToShow: SpinResult? get() = if (isSpinning) null else lastResult

    val warning: GameWarning?
        get() = when {
            isLoading || canPlay -> null
            canClaimBonus -> GameWarning.NO_FUNDS_BONUS_AVAILABLE
            else -> GameWarning.NO_FUNDS_BONUS_CLAIMED
        }
}