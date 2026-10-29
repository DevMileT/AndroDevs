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
import java.text.NumberFormat
import androidx.compose.foundation.layout.offset

@Composable
fun MenuScreen(
    balance: Int,
    onPlayClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(34.dp))
        Image(
            painter = painterResource(R.drawable.logo_lucky_sevens),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.width(136.dp),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(51.dp))
        BalancePill(balance = balance)
        Spacer(modifier = Modifier.height(26.dp))
        MenuButton(
            text = stringResource(R.string.menu_play),
            iconRes = R.drawable.ic_gamepad,
            fillColor = MenuPlayFill,
            borderColor = MenuPlayBorder,
            onClick = onPlayClick
        )
        Spacer(modifier = Modifier.height(44.dp))
        MenuButton(
            text = stringResource(R.string.menu_history),
            iconRes = R.drawable.ic_list,
            fillColor = MenuHistoryFill,
            borderColor = MenuHistoryBorder,
            onClick = onHistoryClick
        )
        Spacer(modifier = Modifier.height(44.dp))
        MenuButton(
            text = stringResource(R.string.menu_stats),
            iconRes = R.drawable.ic_stats,
            fillColor = MenuStatsFill,
            borderColor = MenuStatsBorder,
            onClick = onStatsClick,
            iconTint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(44.dp))
        MenuButton(
            text = stringResource(R.string.menu_settings),
            iconRes = R.drawable.ic_settings,
            fillColor = MenuSettingsFill,
            borderColor = MenuSettingsBorder,
            onClick = onSettingsClick
        )
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
                text = NumberFormat.getIntegerInstance().format(balance),
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