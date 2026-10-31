package com.androdevs.luckysevens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.theme.ButtonAmber
import com.androdevs.luckysevens.ui.theme.MenuHistoryBorder
import com.androdevs.luckysevens.ui.theme.MenuHistoryFill
import com.androdevs.luckysevens.ui.theme.MenuPlayBorder
import com.androdevs.luckysevens.ui.theme.MenuPlayFill
import com.androdevs.luckysevens.ui.theme.MenuSettingsBorder
import com.androdevs.luckysevens.ui.theme.MenuSettingsFill
import com.androdevs.luckysevens.ui.theme.MenuStatsBorder
import com.androdevs.luckysevens.ui.theme.MenuStatsFill
import com.androdevs.luckysevens.ui.theme.TextLight
import com.androdevs.luckysevens.ui.util.formatNumber
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


// Separación vertical entre los botones del Menú. En el mockup eran 33 px (≈ 44 dp);
// reducida por decisión del equipo (desviación visual, D-10).
private val MenuButtonSpacing = 28.dp

@Composable
fun MenuScreen(
    balance: Int,
    canClaimBonus: Boolean,
    onPlayClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onClaimBonusClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.logo_lucky_sevens),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 34.dp)
                    .width(136.dp),
                contentScale = ContentScale.FillWidth
            )
            NavigationDropdown(
                onPlayClick = onPlayClick,
                onHistoryClick = onHistoryClick,
                onStatsClick = onStatsClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        BalancePill(balance = balance)
        Spacer(modifier = Modifier.height(26.dp))
        MenuButton(
            text = stringResource(R.string.menu_play),
            iconRes = R.drawable.ic_gamepad,
            fillColor = MenuPlayFill,
            borderColor = MenuPlayBorder,
            onClick = onPlayClick
        )
        Spacer(modifier = Modifier.height(MenuButtonSpacing))
        MenuButton(
            text = stringResource(R.string.menu_history),
            iconRes = R.drawable.ic_list,
            fillColor = MenuHistoryFill,
            borderColor = MenuHistoryBorder,
            onClick = onHistoryClick
        )
        Spacer(modifier = Modifier.height(MenuButtonSpacing))
        MenuButton(
            text = stringResource(R.string.menu_stats),
            iconRes = R.drawable.ic_stats,
            fillColor = MenuStatsFill,
            borderColor = MenuStatsBorder,
            onClick = onStatsClick,
            iconTint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(MenuButtonSpacing))
        MenuButton(
            text = stringResource(R.string.menu_settings),
            iconRes = R.drawable.ic_settings,
            fillColor = MenuSettingsFill,
            borderColor = MenuSettingsBorder,
            onClick = onSettingsClick
        )
        Spacer(modifier = Modifier.height(MenuButtonSpacing))
        BonusButton(canClaim = canClaimBonus, onClick = onClaimBonusClick)
    }
}

// Píldora con la moneda, el saldo y la etiqueta «Saldo actual».
@Composable
private fun BalancePill(balance: Int) {
    Column(
        modifier = Modifier
            .width(219.dp)
            .border(width = 2.dp, color = ButtonAmber, shape = RoundedCornerShape(percent = 50))
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.offset(x = (-14).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_coin),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatNumber(balance),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ButtonAmber
            )
        }
        Text(
            text = stringResource(R.string.menu_balance_label),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// Botón de sección: icono a la izquierda y texto alineado, con relleno y borde de color.
@Composable
private fun MenuButton(
    text: String,
    iconRes: Int,
    fillColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    iconTint: Color = TextLight
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(0.73f).height(60.dp),
        shape = RoundedCornerShape(11.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = fillColor,
            contentColor = TextLight
        ),
        border = BorderStroke(2.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
// Icono de tres rayas con su desplegable de navegación (RF-02, CA-29).
// Ofrece las mismas cuatro opciones que los botones; el bonus no entra porque no es navegación.
@Composable
private fun NavigationDropdown(
    onPlayClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_menu),
                contentDescription = stringResource(R.string.menu_open_description),
                modifier = Modifier.size(40.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_play)) },
                onClick = {
                    expanded = false
                    onPlayClick()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_history)) },
                onClick = {
                    expanded = false
                    onHistoryClick()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_stats)) },
                onClick = {
                    expanded = false
                    onStatsClick()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_settings)) },
                onClick = {
                    expanded = false
                    onSettingsClick()
                }
            )
        }
    }
}
// Botón del bonus diario (RF-13): dorado y distinto de los de navegación.
// Si ya se reclamó hoy queda atenuado y desactivado.
@Composable
private fun BonusButton(
    canClaim: Boolean,
    onClick: () -> Unit
) {
    val fill = MaterialTheme.colorScheme.primary
    val content = MaterialTheme.colorScheme.onPrimary
    Button(
        onClick = onClick,
        enabled = canClaim,
        modifier = Modifier.fillMaxWidth(0.73f).height(60.dp),
        shape = RoundedCornerShape(11.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = fill,
            contentColor = content,
            disabledContainerColor = fill.copy(alpha = 0.3f),
            disabledContentColor = TextLight.copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            2.dp,
            if (canClaim) ButtonAmber else ButtonAmber.copy(alpha = 0.3f)
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(
                if (canClaim) R.string.menu_bonus_available else R.string.menu_bonus_claimed
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}