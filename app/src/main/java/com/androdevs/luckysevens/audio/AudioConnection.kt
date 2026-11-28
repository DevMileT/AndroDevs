package com.androdevs.luckysevens.audio

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 27/11/2026 at 22:0
 *Year: 2026
 */
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

/**
 * Gestiona el enlace entre MainActivity y AudioService.
 * Si todavía no hay servicio conectado, sus funciones no hacen nada.
 */
class AudioConnection(private val context: Context) {

    private var service: AudioService? = null
    private var isBound = false

    // Android avisa aquí cuando el enlace con el servicio se establece o se pierde.
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            service = (binder as AudioService.LocalBinder).getService()
            service?.playMusic()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            service = null
        }
    }

    /** Se llama en MainActivity.onStart: inicia el servicio y se enlaza a él. */
    fun start() {
        val intent = Intent(context, AudioService::class.java)
        context.startService(intent)
        isBound = context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    /** Se llama en MainActivity.onStop: pausa la música y se desenlaza; el servicio sigue vivo. */
    fun stop() {
        service?.pauseMusic()
        if (isBound) {
            context.unbindService(connection)
            isBound = false
        }
        service = null
    }
    /** Avisa al servicio de que ha cambiado un ajuste. Si no hay servicio, no hace nada. */
    fun refreshSettings() {
        service?.refreshSettings()
    }

    /** Se llama solo cuando el jugador sale de la app de verdad. */
    fun stopService() {
        context.stopService(Intent(context, AudioService::class.java))
    }
}