package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 19:0
 *Year: 2026
 */

import io.reactivex.rxjava3.core.Single

/**
 * Contrato de la partida: saldo, giro y bonus. El ViewModel solo conoce esta interfaz;
 * la implementación con SQLite vive en la capa de datos .
 *
 * Todas las operaciones devuelven un Single: un "ticket" que no hace nada hasta que alguien
 * se suscribe y que termina con un valor o con un error. La implementación ya ejecuta el
 * trabajo en un hilo de fondo; quien llame solo debe añadir
 * observeOn(AndroidSchedulers.mainThread()) para recibir la respuesta en el hilo principal.
 */
interface GameRepository {

    /** Lee el estado guardado del jugador: saldo, récord y fecha del último bonus. */
    fun getPlayerState(): Single<PlayerState>

    /**
     * Resuelve un giro y guarda todo en una única transacción (D-02): saldo, récord y
     * entrada de historial. Termina en error con IllegalArgumentException si la apuesta
     * no es válida (decisión 2 de D-12).
     */
    fun spin(bet: Int): Single<SpinResult>

    /**
     * Intenta reclamar el bonus diario con un único UPDATE condicionado (D-04, CA-16).
     * Devuelve true si se concedió ahora y false si ya estaba reclamado hoy.
     */
    fun claimBonus(): Single<Boolean>
}