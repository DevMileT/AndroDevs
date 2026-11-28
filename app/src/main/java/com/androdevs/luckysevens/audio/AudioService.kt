package com.androdevs.luckysevens.audio

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 27/11/2026 at 21:0
 *Year: 2026
 */
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.LuckySevensApp
import com.androdevs.luckysevens.data.AppPreferences

/**
 * Servicio de audio de la app: iniciado y enlazado.
 * En este primer bloque solo reproduce la melodía oficial en bucle.
 */
class AudioService : Service() {

    // Binder local: lo que recibe la Activity al enlazarse para poder llamar al servicio.
    inner class LocalBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    private val binder = LocalBinder()
    companion object {
        // Volumen de la música respecto al del archivo (0.0 = silencio, 1.0 = original).
        private const val MUSIC_VOLUME = 0.35f
    }
    private var player: MediaPlayer? = null
    private var isPrepared = false   // ¿ya terminó de cargar el MediaPlayer?
    private var wantsToPlay = false  // ¿debe sonar en cuanto esté listo?

    // Ajustes del jugador: los pide a la Application, que es su único dueño.
    private val preferences: AppPreferences
        get() = (application as LuckySevensApp).appPreferences

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Si Android mata el servicio por falta de memoria, que no lo reinicie solo.
        return START_NOT_STICKY
    }

    /** La app está a la vista: la música suena si el jugador la tiene activada. */
    fun playMusic() {
        wantsToPlay = true
        updatePlayback()
    }

    /** La app ya no está a la vista: se pausa la música. */
    fun pauseMusic() {
        wantsToPlay = false
        updatePlayback()
    }

    /** Se llama cuando cambia un ajuste (por ejemplo, el interruptor de música). */
    fun refreshSettings() {
        updatePlayback()
    }

    // Único sitio que decide si suena o no: app a la vista Y música activada.
    private fun updatePlayback() {
        val shouldPlay = wantsToPlay && preferences.musicEnabled
        if (shouldPlay) {
            val p = player ?: createPlayer()
            if (isPrepared && !p.isPlaying) p.start()
        } else {
            val p = player
            if (isPrepared && p != null && p.isPlaying) p.pause()
        }
    }
    private fun createPlayer(): MediaPlayer {
        val p = MediaPlayer()
        p.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        resources.openRawResourceFd(R.raw.music_official).use { afd ->
            p.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        }
        p.isLooping = true
        p.setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
        p.setOnPreparedListener {
            isPrepared = true
            updatePlayback()
        }
        p.prepareAsync()
        player = p
        return p
    }

    override fun onDestroy() {
        player?.release()
        player = null
        isPrepared = false
        super.onDestroy()
    }
}