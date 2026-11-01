package com.androdevs.luckysevens.ui.state

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 23:0
 *Year: 2026
 */
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.androdevs.luckysevens.domain.GameStats
import com.androdevs.luckysevens.domain.HistoryRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable

/** Lo que muestra la pantalla de Estadísticas. stats es null mientras llegan los datos. */
data class StatsUiState(
    val stats: GameStats? = null,
    val hasError: Boolean = false
)

/** Estadísticas calculadas desde el historial (RF-11, CA-23). Se crea al entrar en la pantalla. */
class StatsViewModel(
    historyRepository: HistoryRepository,
    mainScheduler: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel() {

    var uiState by mutableStateOf(StatsUiState())
        private set

    private val disposables = CompositeDisposable()

    init {
        disposables.add(
            historyRepository.getStats()
                .observeOn(mainScheduler)
                .subscribe(
                    { stats -> uiState = StatsUiState(stats = stats) },
                    { uiState = StatsUiState(hasError = true) }
                )
        )
    }

    override fun onCleared() {
        disposables.clear()
    }
}