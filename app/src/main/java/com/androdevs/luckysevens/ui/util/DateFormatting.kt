package com.androdevs.luckysevens.ui.util

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 23:0
 *Year: 2026
 */

import java.text.DateFormat
import java.util.Date

/**
 * Fecha y hora de una partida a partir de los milisegundos guardados (D-04), con el formato
 * del idioma del móvil. En la Tarea 2 cada idioma mostrará su formato sin tocar el código.
 */
fun formatDateTime(millis: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(millis))