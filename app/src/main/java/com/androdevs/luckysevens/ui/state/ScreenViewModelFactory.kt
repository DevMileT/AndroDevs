package com.androdevs.luckysevens.ui.state

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 23:0
 *Year: 2026
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.androdevs.luckysevens.LuckySevensApp

/** Crea los ViewModels de Historial y Estadísticas con el repositorio de la Application (sin Hilt). */
class ScreenViewModelFactory(private val app: LuckySevensApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(app.historyRepository) as T
            modelClass.isAssignableFrom(StatsViewModel::class.java) ->
                StatsViewModel(app.historyRepository) as T
            else -> throw IllegalArgumentException("ViewModel desconocido: $modelClass")
        }
}