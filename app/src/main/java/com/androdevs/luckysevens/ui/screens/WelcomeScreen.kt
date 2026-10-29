package com.androdevs.luckysevens.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.theme.ButtonAmber
import com.androdevs.luckysevens.ui.theme.ButtonAmberText
import com.androdevs.luckysevens.ui.theme.TaglineCream

@Composable
fun WelcomeScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(2.4f))
        Image(
            painter = painterResource(R.drawable.bg_welcome),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.welcome_tagline),
            style = MaterialTheme.typography.titleMedium,
            color = TaglineCream
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onStartClick,
            modifier = Modifier.width(205.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonAmber,
                contentColor = ButtonAmberText
            )
        ) {
            Text(
                text = stringResource(R.string.welcome_start_button),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.weight(2.4f))
    }
}