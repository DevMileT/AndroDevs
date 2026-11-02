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
import com.androdevs.luckysevens.domain.HistoryFilter
import com.androdevs.luckysevens.domain.HistoryRepository
import com.androdevs.luckysevens.domain.SpinRecord
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable

/** Lo que muestra la pantalla de Historial. */
data class HistoryUiState(
    val filter: HistoryFilter = HistoryFilter.ALL,
    val records: List<SpinRecord> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false
)
/**
 * Historial con filtro. Se crea al entrar en la pantalla y carga todas las partidas;
 * cada cambio de filtro vuelve a pedir al repositorio solo las del resultado elegido.
 */
class HistoryViewModel(
    private val historyRepository: HistoryRepository,
    private val mainScheduler: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel() {

    var uiState by mutableStateOf(HistoryUiState())
        private set

    private var loading: Disposable? = null

    init {
        load(HistoryFilter.ALL)
    }

    fun onFilterSelected(filter: HistoryFilter) {
        load(filter)
    }

    private fun load(filter: HistoryFilter) {
        uiState = uiState.copy(filter = filter, isLoading = true, hasError = false)
        // Si todavía llegaba la respuesta de un filtro anterior, se descarta.
        loading?.dispose()
        loading = historyRepository.getHistory(filter)
            .observeOn(mainScheduler)
            .subscribe(
                { records -> uiState = uiState.copy(records = records, isLoading = false) },
                { uiState = uiState.copy(isLoading = false, hasError = true) }
            )
    }

    override fun onCleared() {
        loading?.dispose()
    }
}