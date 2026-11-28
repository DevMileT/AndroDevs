package com.androdevs.luckysevens.ui.state

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 28/11/2026 at 01:0
 *Year: 2026
 */
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.androdevs.luckysevens.data.AppPreferences

/** Lo que muestra la pantalla de Ajustes en su parte de sonido. */
data class SettingsUiState(
    val musicEnabled: Boolean = true
)

/**
 * Ajustes del jugador (D-17). Guarda los cambios en AppPreferences; avisar al servicio
 * de audio lo hace MainNavigation con AudioConnection (opción A, 2026-11-28).
 */
class SettingsViewModel(
    private val preferences: AppPreferences
) : ViewModel() {

    var uiState by mutableStateOf(SettingsUiState(musicEnabled = preferences.musicEnabled))
        private set

    fun onMusicChange(enabled: Boolean) {
        preferences.musicEnabled = enabled
        uiState = uiState.copy(musicEnabled = enabled)
    }
}