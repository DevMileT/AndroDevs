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

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        TitleTopBar(title = stringResource(R.string.menu_settings), onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
        }
    }
}

// Nombre de versión de la app (versionName de build.gradle.kts), leído del sistema para no
// duplicarlo a mano. getPackageInfo(String, Int) está marcado como obsoleto desde Android 13,
// pero sigue funcionando y evita añadir código solo para versiones nuevas.
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