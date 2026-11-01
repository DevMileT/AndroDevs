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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.androdevs.luckysevens.domain.PayoutTable
import com.androdevs.luckysevens.domain.SpinOutcome
import com.androdevs.luckysevens.domain.SpinResult
import com.androdevs.luckysevens.ui.state.GameUiState
import com.androdevs.luckysevens.ui.state.GameWarning
import com.androdevs.luckysevens.ui.util.drawableRes

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
private const val WINDOW_TOP = 76f
private const val WINDOW_HEIGHT = 228f
private val WINDOW_LEFT = floatArrayOf(89f, 243f, 395f)
private val WINDOW_WIDTH = floatArrayOf(127f, 125f, 126f)



// Los seis símbolos posibles de un rodillo (sección 2).
private val ALL_SYMBOLS = listOf(
    R.drawable.symbol_cherry,
    R.drawable.symbol_lemon,
    R.drawable.symbol_grape,
    R.drawable.symbol_orange,
    R.drawable.symbol_bell,
    R.drawable.symbol_seven
)

// Celdas que recorre cada rodillo durante el giro y su duración. El primero para antes y el
// último después, así los rodillos se detienen de forma progresiva (RF-05).
private val REEL_CELLS = intArrayOf(10, 16, 22)
private val REEL_DURATION_MS = intArrayOf(1200, 1700, 2200)

// Franja de un rodillo: el símbolo con el que estaba, símbolos al azar y, al final, el
// resultado. La animación la desliza hasta que el resultado queda a la vista.
private fun buildStrip(from: Int, result: Int, cells: Int): List<Int> =
    listOf(from) + List(cells - 1) { ALL_SYMBOLS.random() } + result

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

// Las seis líneas de D-08. Los multiplicadores se leen de PayoutTable, así la tarjeta no puede
// desincronizarse de la lógica (RNF-07, D-12).
private val PRIZE_LINES = listOf(
    PrizeLine(
        listOf(R.drawable.symbol_seven, R.drawable.symbol_seven, R.drawable.symbol_seven),
        R.string.game_prize_three_sevens,
        PayoutTable.THREE_SEVENS
    ),
    PrizeLine(
        listOf(R.drawable.symbol_bell, R.drawable.symbol_bell, R.drawable.symbol_bell),
        R.string.game_prize_three_bells,
        PayoutTable.THREE_BELLS
    ),
    PrizeLine(
        listOf(
            R.drawable.symbol_cherry,
            R.drawable.symbol_lemon,
            R.drawable.symbol_grape,
            R.drawable.symbol_orange
        ),
        R.string.game_prize_three_same,
        PayoutTable.THREE_OF_A_KIND
    ),
    PrizeLine(
        listOf(R.drawable.symbol_seven, R.drawable.symbol_bell, R.drawable.symbol_cherry),
        R.string.game_prize_two_same,
        PayoutTable.PAIR
    ),
    PrizeLine(
        listOf(R.drawable.symbol_cherry),
        R.string.game_prize_one_cherry,
        PayoutTable.SINGLE_CHERRY
    ),
    PrizeLine(emptyList(), R.string.game_prize_other, PayoutTable.NO_PRIZE)
)

@Composable
fun GameScreen(
    uiState: GameUiState,
    onBackClick: () -> Unit,
    onDecreaseBet: () -> Unit,
    onIncreaseBet: () -> Unit,
    onSpinClick: () -> Unit,
    onSpinAnimationFinished: () -> Unit,
    onResultDismissed: () -> Unit
) {
    // Estado SOLO de la animación (presentación): lo que se ve en reposo y las franjas al girar.
    // El saldo, la apuesta y el resultado viven en el GameViewModel (D-13).
    var restSymbols by remember {
        mutableStateOf(
            listOf(R.drawable.symbol_cherry, R.drawable.symbol_seven, R.drawable.symbol_bell)
        )
    }
    var strips by remember { mutableStateOf<List<List<Int>>>(emptyList()) }
    // Un Animatable por rodillo: guarda cuántas celdas se ha desplazado su franja.
    val offsets = remember { List(3) { Animatable(0f) } }

    // Corrutina del giro: arranca cuando el ViewModel ya tiene el resultado (calculado y guardado
    // antes de animar, D-02). La animación solo lo representa y nunca lo determina (RF-05, CA-13).
    LaunchedEffect(uiState.isSpinning, uiState.lastResult) {
        val finalResult = uiState.lastResult
        if (!uiState.isSpinning || finalResult == null) return@LaunchedEffect
        val finalSymbols = finalResult.symbols.map { it.drawableRes() }
        strips = finalSymbols.mapIndexed { index, final ->
            buildStrip(restSymbols[index], final, REEL_CELLS[index])
        }
        coroutineScope {
            offsets.forEachIndexed { index, offset ->
                launch {
                    offset.animateTo(
                        targetValue = REEL_CELLS[index].toFloat(),
                        animationSpec = tween(
                            durationMillis = REEL_DURATION_MS[index],
                            easing = LinearOutSlowInEasing
                        )
                    )
                }
            }
        }
        delay(300) // pausa breve para ver la combinación antes del panel
        // Fin del giro: cada rodillo vuelve a un solo símbolo, el resultado.
        restSymbols = finalSymbols
        strips = emptyList()
        offsets.forEach { it.snapTo(0f) }
        // Lo último: avisa al ViewModel (cambia isSpinning, lo que cancela esta corrutina).
        onSpinAnimationFinished()
    }

    // Con el panel de resultado visible, Atrás equivale a Continuar (D-05, CA-30).
    BackHandler(enabled = uiState.resultToShow != null) { onResultDismissed() }
    // Girando: el botón Atrás del sistema no hace nada (RF-07, CA-28).
    BackHandler(enabled = uiState.isSpinning) { }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                BalanceTopBar(
                    balance = uiState.balance,
                    onBackClick = onBackClick,
                    backEnabled = !uiState.isSpinning
                )
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
            ReelsView(
                // En reposo, un solo símbolo por rodillo; girando, la franja completa.
                strips = if (strips.isEmpty()) restSymbols.map { listOf(it) } else strips,
                offsetOf = { index -> offsets[index].value },
                modifier = Modifier.fillMaxWidth(0.82f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            BetPanel(
                bet = uiState.shownBet,
                selectorEnabled = uiState.canPlay && !uiState.isSpinning,
                canDecrease = uiState.canDecrease,
                canIncrease = uiState.canIncrease,
                canSpin = uiState.canSpin,
                warning = uiState.warning,
                onDecreaseClick = onDecreaseBet,
                onIncreaseClick = onIncreaseBet,
                onSpinClick = onSpinClick,
                modifier = Modifier.fillMaxWidth(0.82f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            PrizesCard(modifier = Modifier.fillMaxWidth(0.82f))
        }

        // El panel va después del contenido, así que se dibuja encima de él.
        uiState.resultToShow?.let { shown ->
            ResultOverlay(result = shown, onContinueClick = onResultDismissed)
        }
    }
}
// Marco de los rodillos con una ventana por rodillo. Solo se muestra la parte visible del marco
// (sin sus márgenes transparentes). Cada ventana recibe su franja de símbolos y cuánto se desplaza.
@Composable
private fun ReelsView(
    strips: List<List<Int>>,
    offsetOf: (Int) -> Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.aspectRatio(FRAME_WIDTH / FRAME_VISIBLE_HEIGHT)) {
        Image(
            painter = painterResource(R.drawable.reel_frame),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(horizontalBias = 0f, verticalBias = FRAME_CROP_BIAS)
        )
        strips.forEachIndexed { index, strip ->
            ReelWindow(
                symbols = strip,
                offsetCells = { offsetOf(index) },
                modifier = Modifier
                    .offset(
                        x = maxWidth * (WINDOW_LEFT[index] / FRAME_WIDTH),
                        y = maxHeight * ((WINDOW_TOP - FRAME_VISIBLE_TOP) / FRAME_VISIBLE_HEIGHT)
                    )
                    .size(
                        width = maxWidth * (WINDOW_WIDTH[index] / FRAME_WIDTH),
                        height = maxHeight * (WINDOW_HEIGHT / FRAME_VISIBLE_HEIGHT)
                    )
            )
        }
    }
}

// Una ventana: muestra una franja vertical de símbolos y solo deja ver lo que cabe dentro
// (clipToBounds). El desplazamiento se mide en celdas: 1 celda = el alto de la ventana.
@Composable
private fun ReelWindow(
    symbols: List<Int>,
    offsetCells: () -> Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val cellHeight = maxHeight
        Column(
            modifier = Modifier
                // Sin este modificador, la columna no podría ser más alta que la ventana.
                .wrapContentHeight(align = Alignment.Top, unbounded = true)
                .offset {
                    // Se lee dentro de offset { }: se aplica al colocar, sin recomponer.
                    val cells = offsetCells().coerceIn(0f, (symbols.size - 1).toFloat())
                    IntOffset(0, -(cells * cellHeight.toPx()).roundToInt())
                }
        ) {
            symbols.forEach { symbolRes ->
                Image(
                    painter = painterResource(symbolRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cellHeight)
                        .padding(8.dp)
                )
            }
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

@Composable
private fun ResultOverlay(
    result: SpinResult,
    onContinueClick: () -> Unit
) {
    // Todo sale del SpinResult (D-13, A): con un saldo real, el saldo visible ya es el nuevo
    // cuando se abre el panel, así que el saldo anterior se deriva del resultado.
    val prize = result.prize
    val multiplier = prize / result.bet
    val previousBalance = result.balanceAfter + result.bet - prize
    val finalBalance = result.balanceAfter

    val iconRes = when (result.outcome) {
        SpinOutcome.WIN -> R.drawable.ic_trophy
        SpinOutcome.DRAW -> R.drawable.ic_handshake
        SpinOutcome.LOSS -> R.drawable.ic_defeat
    }
    val bannerTextRes = when (result.outcome) {
        SpinOutcome.WIN -> R.string.result_win
        SpinOutcome.DRAW -> R.string.result_draw
        SpinOutcome.LOSS -> R.string.result_loss
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
                result.symbols.forEach { symbol ->
                    Image(
                        painter = painterResource(symbol.drawableRes()),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            if (multiplier > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.result_prize_line,
                        formatNumber(prize),
                        multiplier
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