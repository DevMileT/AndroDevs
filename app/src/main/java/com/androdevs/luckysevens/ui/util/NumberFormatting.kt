package com.androdevs.luckysevens.ui.util

import java.text.NumberFormat

// Formato de cantidades saldo, premios, contadores según el idioma del móvil:
// «1.000» en español y «1,000» en inglés.
fun formatNumber(value: Int): String = NumberFormat.getIntegerInstance().format(value)