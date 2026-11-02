package com.androdevs.luckysevens.ui.util

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 01/11/2026 at 23:0
 *Year: 2026
 */

import androidx.annotation.DrawableRes
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.domain.Symbol

/** Imagen de cada símbolo. El dominio solo conoce el valor; la correspondencia es de la interfaz. */
@DrawableRes
fun Symbol.drawableRes(): Int = when (this) {
    Symbol.CHERRY -> R.drawable.symbol_cherry
    Symbol.LEMON -> R.drawable.symbol_lemon
    Symbol.GRAPE -> R.drawable.symbol_grape
    Symbol.ORANGE -> R.drawable.symbol_orange
    Symbol.BELL -> R.drawable.symbol_bell
    Symbol.SEVEN -> R.drawable.symbol_seven
}