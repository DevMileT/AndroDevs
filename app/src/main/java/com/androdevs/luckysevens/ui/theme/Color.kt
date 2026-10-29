package com.androdevs.luckysevens.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de referencia de la especificación (sección 1).
// Desviación visual (D-10): el mockup del equipo usa #160D3D como fondo de todas las pantallas,
// así que los dos fondos intercambian su papel respecto al texto de la especificación.
val BackgroundDark = Color(0xFF160D3D )  // fondo de todas las pantallas
val SurfaceDark = Color(0xFF0B1033)     // tono más oscuro para tarjetas y paneles (provisional)
val Gold = Color(0xFFFFD700)            // dorado: botones y detalles
val AccentPurple = Color(0xFF7B2FF7)    // morado de acento

// Color de texto claro sobre fondos oscuros (no está en la especificación)
val TextLight = Color(0xFFFFFFFF)
// Medidos en el mockup de la Bienvenida (captura del equipo): botón ámbar con texto marrón.
val ButtonAmber = Color(0xFFEBA81C)
val ButtonAmberText = Color(0xFF622222)
// Color crema de la frase de la Bienvenida, medido en el mockup.
val TaglineCream = Color(0xFFF9EDAC)

// Botones del Menú, medidos en el mockup (relleno y borde de cada uno).
val MenuPlayFill = Color(0xFF6A1B9A)
val MenuPlayBorder = Color(0xFF9C27B0)
val MenuHistoryFill = Color(0xFF0F52BA)
val MenuHistoryBorder = Color(0xFF028CE9)
val MenuStatsFill = Color(0xFF008F39)
val MenuStatsBorder = Color(0xFF00A864)
val MenuSettingsFill = Color(0xFFED760E)
val MenuSettingsBorder = Color(0xFFDE8613)