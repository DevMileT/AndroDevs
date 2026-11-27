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

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Si Android mata el servicio por falta de memoria, que no lo reinicie solo.
        return START_NOT_STICKY
    }

    fun playMusic() {
        wantsToPlay = true
        val p = player ?: createPlayer()
        if (isPrepared && !p.isPlaying) p.start()
    }

    fun pauseMusic() {
        wantsToPlay = false
        val p = player
        if (isPrepared && p != null && p.isPlaying) p.pause()
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
            if (wantsToPlay) it.start()
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