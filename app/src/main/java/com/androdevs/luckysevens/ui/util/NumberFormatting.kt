package com.androdevs.luckysevens.ui.util

import java.text.NumberFormat

// Formato de cantidades (saldo, premios, contadores) según el idioma del móvil:
// «1.000» en español y «1,000» en inglés. Es el único sitio que da formato a los números
// de la interfaz; las pantallas no deben crear su propio NumberFormat (RNF-07).
fun formatNumber(value: Int): String = NumberFormat.getIntegerInstance().format(value)