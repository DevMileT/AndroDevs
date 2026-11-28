package com.androdevs.luckysevens.data

/**
 *Project: Luchy Sevens
 *Created by: Milena
 *On: 28/11/2026 at 00:0
 *Year: 2026
 */
import android.content.Context
import android.content.SharedPreferences

/**
 * Ajustes del jugador guardados en SharedPreferences.
 * Una sola instancia, creada en LuckySevensApp.
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    /** ¿Debe sonar la música de fondo? Por defecto, sí. */
    var musicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()
        }

    companion object {
        private const val FILE_NAME = "lucky_sevens_prefs"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
    }
}