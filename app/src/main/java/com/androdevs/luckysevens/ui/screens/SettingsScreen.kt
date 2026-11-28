package com.androdevs.luckysevens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.components.TitleTopBar
import com.androdevs.luckysevens.ui.theme.ButtonAmber
import com.androdevs.luckysevens.ui.theme.HistoryCardBorder
import com.androdevs.luckysevens.ui.theme.HistoryCardFill
import com.androdevs.luckysevens.ui.theme.TextLight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.semantics.Role
import com.androdevs.luckysevens.ui.state.SettingsUiState

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onMusicChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        TitleTopBar(title = stringResource(R.string.menu_settings), onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SettingsCard(title = stringResource(R.string.settings_sound_title)) {
                SwitchRow(
                    label = stringResource(R.string.settings_music_switch),
                    checked = uiState.musicEnabled,
                    onCheckedChange = onMusicChange
                )
            }
            InfoCard(
                title = stringResource(R.string.settings_about_title),
                lines = listOf(
                    stringResource(R.string.app_name),
                    stringResource(R.string.settings_about_version, appVersionName()),
                    stringResource(R.string.settings_about_project)
                )
            )
            InfoCard(
                title = stringResource(R.string.settings_virtual_title),
                lines = listOf(stringResource(R.string.settings_virtual_body))
            )
            InfoCard(
                title = stringResource(R.string.settings_bonus_title),
                lines = listOf(stringResource(R.string.settings_bonus_body))
            )
            HelpRow(
                title = stringResource(R.string.settings_help_title),
                onClick = onHelpClick
            )
        }

    }
}

@Suppress("DEPRECATION")
@Composable
private fun appVersionName(): String {
    val context = LocalContext.current
    return context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
}

// Tarjeta informativa: título en ámbar y una o varias líneas de texto debajo.
@Composable
private fun InfoCard(title: String, lines: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HistoryCardFill, RoundedCornerShape(16.dp))
            .border(2.dp, HistoryCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ButtonAmber
        )
        Spacer(modifier = Modifier.height(8.dp))
        lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
// Fila pulsable que lleva a la Ayuda. Mismo aspecto que InfoCard, con un símbolo › a la derecha.
@Composable
private fun HelpRow(title: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(HistoryCardFill, RoundedCornerShape(16.dp))
            .border(2.dp, HistoryCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ButtonAmber
        )
        Text(
            text = "›",
            style = MaterialTheme.typography.titleLarge,
            color = ButtonAmber
        )
    }
}
// Tarjeta con el mismo aspecto que InfoCard, pero que acepta cualquier contenido (D-17).
@Composable
private fun SettingsCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HistoryCardFill, RoundedCornerShape(16.dp))
            .border(2.dp, HistoryCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ButtonAmber
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

// Fila con un texto y un interruptor. Toda la fila es pulsable, no solo el interruptor.
@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextLight
        )
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = HistoryCardFill,
                checkedTrackColor = ButtonAmber,
                checkedBorderColor = ButtonAmber,
                uncheckedThumbColor = TextLight,
                uncheckedTrackColor = HistoryCardFill,
                uncheckedBorderColor = TextLight
            )
        )
    }
}