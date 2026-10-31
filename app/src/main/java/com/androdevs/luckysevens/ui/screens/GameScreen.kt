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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import com.androdevs.luckysevens.ui.theme.ButtonAmber
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.androdevs.luckysevens.ui.theme.ButtonAmberText
import com.androdevs.luckysevens.ui.theme.StatPositive
import com.androdevs.luckysevens.ui.util.formatNumber
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp

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

// Avisos de saldo insuficiente (D-09). Se muestran debajo del botón Girar.
// TEMPORAL: en la Fase 5 pasará a GameUiState y lo calculará el GameViewModel.
private enum class GameWarning { NO_FUNDS_BONUS_AVAILABLE, NO_FUNDS_BONUS_CLAIMED }

// TEMPORAL (Fase 4): resultado de prueba para ver el panel. En la Fase 5 lo calculará
// GameEngine y llegará en GameUiState (lastResult).
private enum class ResultOutcome { WIN, DRAW, LOSS }

private data class TestResult(
    val outcome: ResultOutcome,
    val symbols: List<Int>,
    val bet: Int,
    val multiplier: Int
)

// Rota entre victoria (x5), empate (x1) y derrota (x0) en cada pulsación de Girar.
private fun testResultFor(index: Int, bet: Int): TestResult = when (index % 3) {
    0 -> TestResult(
        ResultOutcome.WIN,
        listOf(R.drawable.symbol_bell, R.drawable.symbol_bell, R.drawable.symbol_bell),
        bet, 5
    )
    1 -> TestResult(
        ResultOutcome.DRAW,
        listOf(R.drawable.symbol_cherry, R.drawable.symbol_orange, R.drawable.symbol_grape),
        bet, 1
    )
    else -> TestResult(
        ResultOutcome.LOSS,
        listOf(R.drawable.symbol_orange, R.drawable.symbol_bell, R.drawable.symbol_seven),
        bet, 0
    )
}

@Composable
fun GameScreen(balance: Int, canClaimBonus: Boolean, onBackClick: () -> Unit) {
    // TEMPORAL: apuesta de prueba para ver funcionar los botones − y +.
    var bet by remember { mutableStateOf(20) }
    // TEMPORAL: resultado de prueba que se muestra en el panel al pulsar Girar.
    var result by remember { mutableStateOf<TestResult?>(null) }
    var spinCount by remember { mutableStateOf(0) }

    // Estado derivado: se calcula a partir del saldo y del bonus, no se guarda (RF-04).
    val maxBet = minOf(TEST_MAX_BET, balance / TEST_BET_STEP * TEST_BET_STEP)
    val canPlay = maxBet >= TEST_MIN_BET
    // Si el saldo baja de la apuesta elegida, la apuesta se ajusta al máximo permitido.
    val shownBet = if (canPlay) bet.coerceAtMost(maxBet) else TEST_MIN_BET
    val warning = when {
        canPlay -> null
        canClaimBonus -> GameWarning.NO_FUNDS_BONUS_AVAILABLE
        else -> GameWarning.NO_FUNDS_BONUS_CLAIMED
    }

    // Con el panel de resultado visible, Atrás equivale a Continuar (D-05, CA-30).
    BackHandler(enabled = result != null) { result = null }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
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
                bet = shownBet,
                selectorEnabled = canPlay,
                canDecrease = canPlay && shownBet > TEST_MIN_BET,
                canIncrease = canPlay && shownBet < maxBet,
                canSpin = canPlay,
                warning = warning,
                onDecreaseClick = { bet = (shownBet - TEST_BET_STEP).coerceAtLeast(TEST_MIN_BET) },
                onIncreaseClick = { bet = (shownBet + TEST_BET_STEP).coerceAtMost(maxBet) },
                onSpinClick = {
                    // TEMPORAL: muestra un resultado de prueba; no cambia el saldo.
                    result = testResultFor(spinCount, shownBet)
                    spinCount += 1
                },
                modifier = Modifier.fillMaxWidth(0.82f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrizesCard(modifier = Modifier.fillMaxWidth(0.82f))
        }

        // El panel va después del contenido, así que se dibuja encima de él.
        result?.let { shown ->
            ResultOverlay(
                result = shown,
                previousBalance = balance,
                onContinueClick = { result = null }
            )
        }
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

// Panel de apuesta: etiqueta, selector − / +, botón Girar y, si hace falta, el aviso.
// No guarda estado: recibe la apuesta y los permisos y avisa de lo que pulsa el jugador.
@Composable
private fun BetPanel(
    bet: Int,
    selectorEnabled: Boolean,
    canDecrease: Boolean,
    canIncrease: Boolean,
    canSpin: Boolean,
    warning: GameWarning?,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onSpinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectorAlpha = if (selectorEnabled) 1f else 0.4f
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
            color = TextLight.copy(alpha = selectorAlpha)
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
                color = TextLight.copy(alpha = selectorAlpha)
            )
            BetStepButton(text = "+", enabled = canIncrease, onClick = onIncreaseClick)
        }
        Spacer(modifier = Modifier.height(16.dp))
        SpinButton(enabled = canSpin, onClick = onSpinClick)
        if (warning != null) {
            Spacer(modifier = Modifier.height(12.dp))
            WarningBox(warning = warning)
        }
    }
}

// Recuadro de aviso bajo el botón Girar (diseño propio: sin mockup).
@Composable
private fun WarningBox(warning: GameWarning) {
    val textRes = when (warning) {
        GameWarning.NO_FUNDS_BONUS_AVAILABLE -> R.string.game_warning_no_funds_bonus_available
        GameWarning.NO_FUNDS_BONUS_CLAIMED -> R.string.game_warning_no_funds_bonus_claimed
    }
    Text(
        text = stringResource(textRes),
        style = MaterialTheme.typography.bodyMedium,
        color = TextLight,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(ButtonAmber.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .border(1.5.dp, ButtonAmber.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
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
// Desactivado se atenúa entero (alpha) porque el degradado no depende de los colores del botón.
@Composable
private fun SpinButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .background(
                Brush.verticalGradient(listOf(SpinGreenTop, SpinGreenBottom)),
                RoundedCornerShape(percent = 50)
            ),
        shape = RoundedCornerShape(percent = 50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextLight,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextLight
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


// Panel de resultado sobre Juego (RF-06, CA-30): fondo oscurecido, banner con el texto por
// código, combinación, línea de premio (solo si hay premio), tabla de cuatro líneas y Continuar.
@Composable
private fun ResultOverlay(
    result: TestResult,
    previousBalance: Int,
    onContinueClick: () -> Unit
) {
    val prize = result.bet * result.multiplier
    val finalBalance = previousBalance - result.bet + prize

    val iconRes = when (result.outcome) {
        ResultOutcome.WIN -> R.drawable.ic_trophy
        ResultOutcome.DRAW -> R.drawable.ic_handshake
        ResultOutcome.LOSS -> R.drawable.ic_defeat
    }
    val bannerTextRes = when (result.outcome) {
        ResultOutcome.WIN -> R.string.result_win
        ResultOutcome.DRAW -> R.string.result_draw
        ResultOutcome.LOSS -> R.string.result_loss
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            // Absorbe los toques para que nada del fondo responda con el panel abierto.
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .background(BetPanelFill, RoundedCornerShape(16.dp))
                .border(3.dp, BetPanelBorder, RoundedCornerShape(16.dp))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.height(56.dp)
            )
            // Banner único sin texto (D-06): el texto se pone encima, sobre la cinta.
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.banner_result),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                CurvedBannerText(
                    text = stringResource(bannerTextRes),
                    modifier = Modifier.matchParentSize()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                result.symbols.forEach { symbolRes ->
                    Image(
                        painter = painterResource(symbolRes),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            if (result.multiplier > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.result_prize_line,
                        formatNumber(prize),
                        result.multiplier
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLight
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            ResultRow(
                label = stringResource(R.string.result_previous_balance),
                value = formatNumber(previousBalance)
            )
            ResultRow(
                label = stringResource(R.string.result_bet),
                value = "−" + formatNumber(result.bet)
            )
            ResultRow(
                label = stringResource(R.string.result_prize),
                value = if (prize > 0) "+" + formatNumber(prize) else formatNumber(prize),
                valueColor = if (prize > 0) StatPositive else TextLight
            )
            ResultRow(
                label = stringResource(R.string.result_final_balance),
                value = formatNumber(finalBalance),
                valueColor = ButtonAmber,
                highlighted = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonAmber,
                    contentColor = ButtonAmberText
                )
            ) {
                Text(
                    text = stringResource(R.string.result_continue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Una línea de la tabla del panel: etiqueta a la izquierda y valor a la derecha.
@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = TextLight,
    highlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
            color = TextLight,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

// Medidas de la cinta de banner_result.png (707×353 px), como fracción del tamaño de la imagen.
// La línea central de la cinta es una parábola con el vértice en el centro; se reproduce con una
// curva de Bézier cuadrática con los mismos extremos y el mismo vértice.
private const val RIBBON_START_X = 130f / 707f
private const val RIBBON_END_X = 577f / 707f
private const val RIBBON_EDGE_Y = 197.5f / 353f
private const val RIBBON_CONTROL_Y = 106.5f / 353f

// Texto que sigue el arco de la cinta del banner. Excepción a D-06 («sin Canvas»), solo para esto.
// El texto no se parte en líneas: si no cabe en la cinta, se reduce hasta que quepa.
@Composable
private fun CurvedBannerText(text: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.semantics { contentDescription = text }) {
        val path = android.graphics.Path().apply {
            moveTo(size.width * RIBBON_START_X, size.height * RIBBON_EDGE_Y)
            quadTo(
                size.width * 0.5f, size.height * RIBBON_CONTROL_Y,
                size.width * RIBBON_END_X, size.height * RIBBON_EDGE_Y
            )
        }
        val pathLength = android.graphics.PathMeasure(path, false).length
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            textSize = 22.sp.toPx()
            setShadowLayer(4f, 0f, 3f, android.graphics.Color.argb(160, 0, 0, 0))
        }
        // Si el texto no cabe en la cinta, se reduce hasta que quepa.
        val maxWidth = pathLength * 0.9f
        val textWidth = paint.measureText(text)
        if (textWidth > maxWidth) paint.textSize = paint.textSize * maxWidth / textWidth
        // Centrado a lo largo de la curva. El texto se apoya en ella por la base, así que se
        // baja un poco (vOffset) para que su centro visual quede sobre la línea central.
        val hOffset = (pathLength - paint.measureText(text)) / 2f
        val vOffset = paint.textSize * 0.35f
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawTextOnPath(text, path, hOffset, vOffset, paint)
        }
    }
}