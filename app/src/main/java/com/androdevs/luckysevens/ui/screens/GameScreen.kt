package com.androdevs.luckysevens.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.components.BalanceTopBar
import com.androdevs.luckysevens.ui.theme.BetPanelBorder
import com.androdevs.luckysevens.ui.theme.BetPanelFill
import com.androdevs.luckysevens.ui.theme.BetStepButtonFill
import com.androdevs.luckysevens.ui.theme.SpinGreenBottom
import com.androdevs.luckysevens.ui.theme.SpinGreenTop
import com.androdevs.luckysevens.ui.theme.TextLight
import com.androdevs.luckysevens.ui.theme.PrizeCardBorder
import com.androdevs.luckysevens.ui.theme.PrizeCardFill
import androidx.compose.ui.BiasAlignment

// Posición de las tres ventanas dentro de reel_frame.png (612 × 408 px), medida en la imagen.
// Si se cambia la imagen del marco, solo hay que actualizar estos valores.
private const val FRAME_WIDTH = 612f
private const val FRAME_HEIGHT = 408f
// Parte realmente visible del marco: el PNG tiene 42 px transparentes arriba y 66 abajo.
// Recortarla libera espacio en pantalla sin cambiar lo que se ve.
private const val FRAME_VISIBLE_TOP = 42f
private const val FRAME_VISIBLE_HEIGHT = 300f
private const val FRAME_CROP_BIAS =
    2f * FRAME_VISIBLE_TOP / (FRAME_HEIGHT - FRAME_VISIBLE_HEIGHT) - 1f
private const val WINDOW_TOP = 108f
private const val WINDOW_HEIGHT = 135f
private val WINDOW_LEFT = floatArrayOf(88f, 242f, 395f)
private val WINDOW_WIDTH = floatArrayOf(129f, 126f, 127f)

// TEMPORAL (Fase 4): límites de la apuesta. Los reales vivirán en GameConstants (RNF-07)
// y la apuesta en el GameViewModel (Fases 5 y 6).
private const val TEST_MIN_BET = 10
private const val TEST_MAX_BET = 100
private const val TEST_BET_STEP = 10

// Tamaño de los dibujos de la tabla de premios. La columna de dibujos mide lo que cuatro
// de ellos (la fila más ancha), para que todos los textos empiecen en la misma columna.
private val PRIZE_SYMBOL_SIZE = 22.dp
private val PRIZE_SYMBOLS_COLUMN_WIDTH = PRIZE_SYMBOL_SIZE * 4
// Una línea de la tabla de premios (D-08): dibujos de la combinación, texto y multiplicador.
private data class PrizeLine(
    val symbols: List<Int>,
    val descriptionRes: Int?,
    val multiplier: Int
)

// TEMPORAL (Fase 4): las seis líneas de D-08 con los multiplicadores escritos a mano.
// En la Fase 5 se leerán de PayoutTable (RNF-07) para que no se desincronicen de la lógica.
private val PRIZE_LINES = listOf(
    PrizeLine(
        listOf(R.drawable.symbol_seven, R.drawable.symbol_seven, R.drawable.symbol_seven),
        R.string.game_prize_three_sevens,
        10
    ),
    PrizeLine(
        listOf(R.drawable.symbol_bell, R.drawable.symbol_bell, R.drawable.symbol_bell),
        R.string.game_prize_three_bells,
        5
    ),
    PrizeLine(
        listOf(
            R.drawable.symbol_cherry,
            R.drawable.symbol_lemon,
            R.drawable.symbol_grape,
            R.drawable.symbol_orange
        ),
        R.string.game_prize_three_same,
        3
    ),
    PrizeLine(
        listOf(R.drawable.symbol_seven, R.drawable.symbol_bell, R.drawable.symbol_cherry),
        R.string.game_prize_two_same,
        2
    ),
    PrizeLine(listOf(R.drawable.symbol_cherry), R.string.game_prize_one_cherry, 1),
    PrizeLine(emptyList(), R.string.game_prize_other, 0)
)

@Composable
fun GameScreen(balance: Int, onBackClick: () -> Unit) {
    // TEMPORAL: apuesta de prueba para ver funcionar los botones − y +.
    var bet by remember { mutableStateOf(20) }

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
        Spacer(modifier = Modifier.height(12.dp))
        BetPanel(
            bet = bet,
            canDecrease = bet > TEST_MIN_BET,
            canIncrease = bet < TEST_MAX_BET,
            onDecreaseClick = { bet = (bet - TEST_BET_STEP).coerceAtLeast(TEST_MIN_BET) },
            onIncreaseClick = { bet = (bet + TEST_BET_STEP).coerceAtMost(TEST_MAX_BET) },
            onSpinClick = { /* TEMPORAL: Girar no hace nada hasta las Fases 5 y 6 */ },
            modifier = Modifier.fillMaxWidth(0.82f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrizesCard(modifier = Modifier.fillMaxWidth(0.82f))
    }
}

// Marco de los rodillos con un símbolo dentro de cada ventana.
// Marco de los rodillos con un símbolo dentro de cada ventana.
// Solo se muestra la parte visible del marco (sin sus márgenes transparentes).
@Composable
private fun ReelsView(symbols: List<Int>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.aspectRatio(FRAME_WIDTH / FRAME_VISIBLE_HEIGHT)) {
        Image(
            painter = painterResource(R.drawable.reel_frame),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(horizontalBias = 0f, verticalBias = FRAME_CROP_BIAS)
        )
        symbols.forEachIndexed { index, symbolRes ->
            Image(
                painter = painterResource(symbolRes),
                contentDescription = null,
                modifier = Modifier
                    .offset(
                        x = maxWidth * (WINDOW_LEFT[index] / FRAME_WIDTH),
                        y = maxHeight * ((WINDOW_TOP - FRAME_VISIBLE_TOP) / FRAME_VISIBLE_HEIGHT)
                    )
                    .size(
                        width = maxWidth * (WINDOW_WIDTH[index] / FRAME_WIDTH),
                        height = maxHeight * (WINDOW_HEIGHT / FRAME_VISIBLE_HEIGHT)
                    )
                    .padding(8.dp)
            )
        }
    }
}

// Panel de apuesta: etiqueta, selector − / + y botón Girar.
// No guarda estado: recibe la apuesta y avisa de lo que pulsa el jugador.
@Composable
private fun BetPanel(
    bet: Int,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onSpinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(BetPanelFill, RoundedCornerShape(14.dp))
            .border(3.dp, BetPanelBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 28.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.game_bet_label),
            style = MaterialTheme.typography.bodyMedium,
            color = TextLight
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BetStepButton(text = "−", enabled = canDecrease, onClick = onDecreaseClick)
            Text(
                text = bet.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            BetStepButton(text = "+", enabled = canIncrease, onClick = onIncreaseClick)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SpinButton(onClick = onSpinClick)
    }
}

// Botón − o +; se atenúa cuando se llega al límite.
@Composable
private fun BetStepButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.width(50.dp).height(38.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BetStepButtonFill,
            contentColor = TextLight,
            disabledContainerColor = BetStepButtonFill.copy(alpha = 0.4f),
            disabledContentColor = TextLight.copy(alpha = 0.4f)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

// Botón Girar con degradado verde. El botón es transparente y el degradado va detrás.
@Composable
private fun SpinButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
            .background(
                Brush.verticalGradient(listOf(SpinGreenTop, SpinGreenBottom)),
                RoundedCornerShape(percent = 50)
            ),
        shape = RoundedCornerShape(percent = 50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextLight
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_refresh),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.game_spin_button),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }

}

// Tarjeta con la tabla de premios completa, en seis líneas agrupadas (D-08).
@Composable
private fun PrizesCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(PrizeCardFill, RoundedCornerShape(14.dp))
            .border(2.dp, PrizeCardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.game_prizes_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextLight
        )
        Modifier.height(6.dp)
        PRIZE_LINES.forEach { line ->
            PrizeRow(line)
            Modifier.height(4.dp)
        }
    }
}

// Una fila: dibujos de la combinación, descripción (si la hay) y multiplicador a la derecha.
// Una fila: dibujos de la combinación, descripción y multiplicador a la derecha.
@Composable
private fun PrizeRow(line: PrizeLine) {
    val description = line.descriptionRes
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna de dibujos de ancho fijo: así todos los textos empiezan en la misma columna.
        Row(
            modifier = Modifier.width(PRIZE_SYMBOLS_COLUMN_WIDTH),
            verticalAlignment = Alignment.CenterVertically
        ) {
            line.symbols.forEach { symbolRes ->
                Image(
                    painter = painterResource(symbolRes),
                    contentDescription = null,
                    modifier = Modifier.size(PRIZE_SYMBOL_SIZE)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (description != null) {
            Text(
                text = stringResource(description),
                style = MaterialTheme.typography.bodyMedium,
                color = TextLight,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        Text(
            text = stringResource(R.string.game_prize_multiplier, line.multiplier),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextLight
        )
    }
}