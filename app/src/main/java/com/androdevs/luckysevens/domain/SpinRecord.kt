package com.androdevs.luckysevens.domain

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 19:0
 *Year: 2026
 */

/**
 * Una entrada del historial: un SpinResult más el momento en que se jugó.
 * playedAt son milisegundos desde 1970 (como System.currentTimeMillis()); se guarda
 * un Long porque un Int no tiene capacidad para esos números.
 * Va aparte de SpinResult para que el motor del juego no necesite conocer el reloj.
 */
data class SpinRecord(
    val playedAt: Long,
    val result: SpinResult
)