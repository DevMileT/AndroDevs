package com.androdevs.luckysevens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.androdevs.luckysevens.ui.navigation.MainNavigation
import com.androdevs.luckysevens.ui.state.GameViewModel
import com.androdevs.luckysevens.ui.state.GameViewModelFactory
import com.androdevs.luckysevens.ui.theme.LuckySevensTheme

class MainActivity : ComponentActivity() {

    // Un único GameViewModel para toda la app; la factory le da sus dependencias.
    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(application as LuckySevensApp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuckySevensTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(viewModel = gameViewModel)
                }
            }
        }
    }

    // Al abrir la app y al volver de segundo plano se relee el saldo y el estado del bonus
    // desde SQLite: si cambió el día, el botón del bonus se actualiza.
    override fun onResume() {
        super.onResume()
        gameViewModel.refreshState()
    }
}