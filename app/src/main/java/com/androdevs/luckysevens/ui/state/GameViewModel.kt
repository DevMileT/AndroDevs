package com.androdevs.luckysevens.ui.state

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 22:0
 *Year: 2026
 */

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.androdevs.luckysevens.LuckySevensApp
import com.androdevs.luckysevens.domain.BonusRules
import com.androdevs.luckysevens.domain.GameConstants
import com.androdevs.luckysevens.domain.GameRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * Estado y acciones de la partida (D-03, D-13). El repositorio ya trabaja en un hilo de fondo;
 * aquí se recibe la respuesta en el hilo principal (observeOn) para poder tocar el estado.
 * mainScheduler se puede sustituir en las pruebas del PC.
 */
class GameViewModel(
    private val gameRepository: GameRepository,
    private val bonusRules: BonusRules,
    private val mainScheduler: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel() {

    /** Estado observable por Compose: cuando cambia, las pantallas que lo leen se repintan. */
    var uiState by mutableStateOf(GameUiState())
        private set

    // Guarda las suscripciones para cancelarlas al destruirse el ViewModel.
    private val disposables = CompositeDisposable()

    /** Lee el saldo y el estado del bonus de SQLite. Se llama en onResume (CA-20). */
    fun refreshState() {
        disposables.add(
            gameRepository.getPlayerState()
                .observeOn(mainScheduler)
                .subscribe(
                    { state ->
                        uiState = uiState.copy(
                            // Durante un giro el saldo visible no cambia (RF-07, D-02).
                            balance = if (uiState.isSpinning) uiState.balance else state.balance,
                            canClaimBonus = bonusRules.canClaim(state.lastBonusDate),
                            isLoading = false
                        )
                    },
                    { uiState = uiState.copy(isLoading = false, error = GameError.LOAD_FAILED) }
                )
        )
    }

    fun onDecreaseBet() {
        if (uiState.canDecrease) {
            uiState = uiState.copy(bet = uiState.shownBet - GameConstants.BET_STEP)
        }
    }

    fun onIncreaseBet() {
        if (uiState.canIncrease) {
            uiState = uiState.copy(bet = uiState.shownBet + GameConstants.BET_STEP)
        }
    }

    /**
     * Pulsar Girar. isSpinning se activa YA, antes de que llegue el resultado, así un segundo
     * toque no genera otro giro (CA-10). El giro se guarda en SQLite al pedirlo (D-02).
     */
    fun onSpinClicked() {
        val state = uiState
        if (!state.canSpin) return
        uiState = state.copy(isSpinning = true, lastResult = null)
        disposables.add(
            gameRepository.spin(state.shownBet)
                .observeOn(mainScheduler)
                .subscribe(
                    { result -> uiState = uiState.copy(lastResult = result) },
                    // Todo error libera isSpinning, o el Atrás quedaría bloqueado (decisión B).
                    { uiState = uiState.copy(isSpinning = false, error = GameError.SPIN_FAILED) }
                )
        )
    }

    /** La pantalla avisa de que la animación terminó: ahora sí cambia el saldo visible. */
    fun onSpinAnimationFinished() {
        val result = uiState.lastResult ?: return
        uiState = uiState.copy(balance = result.balanceAfter, isSpinning = false)
    }

    /** Continuar o Atrás en el panel de resultado. */
    fun onResultDismissed() {
        uiState = uiState.copy(lastResult = null)
    }

    /** Reclamar el bonus. La barrera real contra dobles pulsaciones es el UPDATE (CA-16). */
    fun onClaimBonusClicked() {
        if (!uiState.canClaimBonus) return
        disposables.add(
            gameRepository.claimBonus()
                .flatMap { gameRepository.getPlayerState() }
                .observeOn(mainScheduler)
                .subscribe(
                    { state ->
                        uiState = uiState.copy(
                            balance = state.balance,
                            canClaimBonus = bonusRules.canClaim(state.lastBonusDate)
                        )
                    },
                    { uiState = uiState.copy(error = GameError.BONUS_FAILED) }
                )
        )
    }

    /** La pantalla ya mostró el error. */
    fun onErrorShown() {
        uiState = uiState.copy(error = null)
    }

    override fun onCleared() {
        disposables.clear()
    }
}

/** Crea el GameViewModel con las dependencias de la Application (sin Hilt, D-03). */
class GameViewModelFactory(private val app: LuckySevensApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        require(modelClass.isAssignableFrom(GameViewModel::class.java)) {
            "ViewModel desconocido: $modelClass"
        }
        return GameViewModel(app.gameRepository, app.bonusRules) as T
    }
}