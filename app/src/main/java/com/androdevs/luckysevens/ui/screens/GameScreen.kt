package com.androdevs.luckysevens.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.components.BalanceTopBar

// Posición de las tres ventanas dentro de reel_frame.png (612 × 408 px), medida en la imagen.
// Si se cambia la imagen del marco, solo hay que actualizar estos valores.
private const val FRAME_WIDTH = 612f
private const val FRAME_HEIGHT = 408f
private const val WINDOW_TOP = 108f
private const val WINDOW_HEIGHT = 135f
private val WINDOW_LEFT = floatArrayOf(88f, 242f, 395f)
private val WINDOW_WIDTH = floatArrayOf(129f, 126f, 127f)

@Composable
fun GameScreen(balance: Int, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            BalanceTopBar(balance = balance, onBackClick = onBackClick)
            Image(
                painter = painterResource(R.drawable.logo_lucky_sevens),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 42.dp)
                    .width(109.dp),
                contentScale = ContentScale.FillWidth
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Símbolos fijos de prueba; el resultado real llega con la lógica del juego.
        ReelsView(
            symbols = listOf(
                R.drawable.symbol_cherry,
                R.drawable.symbol_seven,
                R.drawable.symbol_bell
            ),
            modifier = Modifier.fillMaxWidth(0.82f)
        )
    }
}

// Marco de los rodillos con un símbolo dentro de cada ventana.
@Composable
private fun ReelsView(symbols: List<Int>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.aspectRatio(FRAME_WIDTH / FRAME_HEIGHT)) {
        Image(
            painter = painterResource(R.drawable.reel_frame),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        symbols.forEachIndexed { index, symbolRes ->
            Image(
                painter = painterResource(symbolRes),
                contentDescription = null,
                modifier = Modifier
                    .offset(
                        x = maxWidth * (WINDOW_LEFT[index] / FRAME_WIDTH),
                        y = maxHeight * (WINDOW_TOP / FRAME_HEIGHT)
                    )
                    .size(
                        width = maxWidth * (WINDOW_WIDTH[index] / FRAME_WIDTH),
                        height = maxHeight * (WINDOW_HEIGHT / FRAME_HEIGHT)
                    )
                    .padding(8.dp)
            )
        }
    }
}