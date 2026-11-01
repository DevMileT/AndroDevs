package com.androdevs.luckysevens.ui.state

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 22:0
 *Year: 2026
 */

import com.androdevs.luckysevens.domain.BonusRules
import com.androdevs.luckysevens.domain.DateProvider
import com.androdevs.luckysevens.domain.GameConstants
import com.androdevs.luckysevens.domain.GameRepository
import com.androdevs.luckysevens.domain.PlayerState
import com.androdevs.luckysevens.domain.SpinOutcome
import com.androdevs.luckysevens.domain.SpinResult
import com.androdevs.luckysevens.domain.Symbol
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas del GameViewModel en el PC (sin móvil): repositorio falso y planificador síncrono
 * (Schedulers.trampoline) en lugar del hilo principal de Android.
 */
class GameViewModelTest {

    private class FakeGameRepository : GameRepository {
        var playerState = PlayerState(balance = 1000, record = 1000, lastBonusDate = null)
        var spinSingle: Single<SpinResult> = Single.never()
        var spinCalls = 0
        var lastBet = 0
        var claimCalls = 0

        override fun getPlayerState(): Single<PlayerState> = Single.fromCallable { playerState }

        override fun spin(bet: Int): Single<SpinResult> {
            spinCalls++
            lastBet = bet
            return spinSingle
        }

        override fun claimBonus(): Single<Boolean> {
            claimCalls++
            playerState = playerState
                .withBalance(playerState.balance + GameConstants.DAILY_BONUS)
                .copy(lastBonusDate = TODAY)
            return Single.just(true)
        }
    }

    private companion object {
        val TODAY: LocalDate = LocalDate.of(2026, 11, 2)
    }

    private val repository = FakeGameRepository()
    private val rules = BonusRules(object : DateProvider {
        override fun today(): LocalDate = TODAY
    })

    private val tripleSeven = SpinResult(
        bet = 10,
        symbols = listOf(Symbol.SEVEN, Symbol.SEVEN, Symbol.SEVEN),
        prize = 100,
        outcome = SpinOutcome.WIN,
        balanceAfter = 1090
    )

    private fun loadedViewModel(): GameViewModel {
        val viewModel = GameViewModel(repository, rules, Schedulers.trampoline())
        viewModel.refreshState()
        return viewModel
    }

    @Test
    fun initialState_isLoading_andCannotSpin() {
        val viewModel = GameViewModel(repository, rules, Schedulers.trampoline())

        assertTrue(viewModel.uiState.isLoading)
        assertFalse(viewModel.uiState.canSpin)
    }

    @Test
    fun refreshState_loadsBalanceAndBonusAvailability() {
        val viewModel = loadedViewModel()

        assertFalse(viewModel.uiState.isLoading)
        assertEquals(1000, viewModel.uiState.balance)
        assertTrue(viewModel.uiState.canClaimBonus)
        assertTrue(viewModel.uiState.canSpin)
    }

    @Test
    fun refreshState_bonusClaimedToday_cannotClaim() {
        repository.playerState = PlayerState(1000, 1000, TODAY)

        assertFalse(loadedViewModel().uiState.canClaimBonus)
    }

    @Test
    fun spin_keepsVisibleBalanceUntilAnimationFinishes() {
        repository.spinSingle = Single.just(tripleSeven)
        val viewModel = loadedViewModel()

        viewModel.onSpinClicked()

        assertEquals(10, repository.lastBet)
        assertTrue(viewModel.uiState.isSpinning)
        assertEquals(1000, viewModel.uiState.balance) // el saldo visible sigue siendo el anterior
        assertEquals(tripleSeven, viewModel.uiState.lastResult)
        assertNull(viewModel.uiState.resultToShow) // el panel aún no se abre

        viewModel.onSpinAnimationFinished()

        assertFalse(viewModel.uiState.isSpinning)
        assertEquals(1090, viewModel.uiState.balance)
        assertEquals(tripleSeven, viewModel.uiState.resultToShow)
    }

    @Test
    fun spin_secondClickWhileSpinning_isIgnored() {
        // CA-10: con un giro en curso, pulsar Girar otra vez no genera otro giro.
        val viewModel = loadedViewModel() // spinSingle = never(): el giro no termina

        viewModel.onSpinClicked()
        viewModel.onSpinClicked()

        assertEquals(1, repository.spinCalls)
        assertTrue(viewModel.uiState.isSpinning)
    }

    @Test
    fun spin_error_releasesSpinningAndReportsError() {
        repository.spinSingle = Single.error(IllegalStateException("fallo de prueba"))
        val viewModel = loadedViewModel()

        viewModel.onSpinClicked()

        assertFalse(viewModel.uiState.isSpinning)
        assertEquals(GameError.SPIN_FAILED, viewModel.uiState.error)
        assertTrue(viewModel.uiState.canSpin)
    }

    @Test
    fun resultDismissed_clearsResult() {
        repository.spinSingle = Single.just(tripleSeven)
        val viewModel = loadedViewModel()
        viewModel.onSpinClicked()
        viewModel.onSpinAnimationFinished()

        viewModel.onResultDismissed()

        assertNull(viewModel.uiState.resultToShow)
        assertNull(viewModel.uiState.lastResult)
    }

    @Test
    fun lowBalance_bonusAvailable_showsAvailableWarning() {
        // CA-19: saldo menor que 10 con el bonus sin reclamar.
        repository.playerState = PlayerState(balance = 5, record = 1000, lastBonusDate = null)

        val state = loadedViewModel().uiState

        assertFalse(state.canSpin)
        assertEquals(GameWarning.NO_FUNDS_BONUS_AVAILABLE, state.warning)
    }

    @Test
    fun lowBalance_bonusClaimed_showsClaimedWarning() {
        repository.playerState = PlayerState(balance = 5, record = 1000, lastBonusDate = TODAY)

        val state = loadedViewModel().uiState

        assertFalse(state.canSpin)
        assertEquals(GameWarning.NO_FUNDS_BONUS_CLAIMED, state.warning)
    }

    @Test
    fun claimBonus_addsBonusAndDisablesButton() {
        val viewModel = loadedViewModel()

        viewModel.onClaimBonusClicked()

        assertEquals(1050, viewModel.uiState.balance)
        assertFalse(viewModel.uiState.canClaimBonus)
        assertEquals(1, repository.claimCalls)
    }

    @Test
    fun claimBonus_secondClick_isIgnored() {
        val viewModel = loadedViewModel()

        viewModel.onClaimBonusClicked()
        viewModel.onClaimBonusClicked()

        assertEquals(1, repository.claimCalls)
        assertEquals(1050, viewModel.uiState.balance)
    }

    @Test
    fun betButtons_stayInsideLimitsAndBalance() {
        // Saldo 35: la apuesta máxima es 30 y la mínima 10 (RF-04, CA-03).
        repository.playerState = PlayerState(balance = 35, record = 1000, lastBonusDate = null)
        val viewModel = loadedViewModel()
        assertEquals(10, viewModel.uiState.shownBet)

        repeat(5) { viewModel.onIncreaseBet() }
        assertEquals(30, viewModel.uiState.shownBet)
        assertFalse(viewModel.uiState.canIncrease)

        repeat(5) { viewModel.onDecreaseBet() }
        assertEquals(10, viewModel.uiState.shownBet)
        assertFalse(viewModel.uiState.canDecrease)
    }

    @Test
    fun bet_isAdjustedWhenBalanceDropsBelowIt() {
        // CA-12: apuesta 100 con saldo 150; tras perder, el saldo es 50 y la apuesta baja a 50.
        repository.playerState = PlayerState(balance = 150, record = 150, lastBonusDate = null)
        repository.spinSingle = Single.just(
            SpinResult(
                bet = 100,
                symbols = listOf(Symbol.LEMON, Symbol.LEMON, Symbol.GRAPE),
                prize = 0,
                outcome = SpinOutcome.LOSS,
                balanceAfter = 50
            )
        )
        val viewModel = loadedViewModel()
        repeat(9) { viewModel.onIncreaseBet() }
        assertEquals(100, viewModel.uiState.shownBet)

        viewModel.onSpinClicked()
        viewModel.onSpinAnimationFinished()

        assertEquals(50, viewModel.uiState.balance)
        assertEquals(50, viewModel.uiState.shownBet)
        assertTrue(viewModel.uiState.canPlay)
    }

    @Test
    fun refreshWhileSpinning_doesNotChangeVisibleBalance() {
        // D-02, RF-07: el saldo ya está guardado, pero el visible no cambia hasta que acabe la animación.
        val viewModel = loadedViewModel()
        viewModel.onSpinClicked()
        repository.playerState = PlayerState(balance = 980, record = 1000, lastBonusDate = null)

        viewModel.refreshState()

        assertEquals(1000, viewModel.uiState.balance)
    }
}