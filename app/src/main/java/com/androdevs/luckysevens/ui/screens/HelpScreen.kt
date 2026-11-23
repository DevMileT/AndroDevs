package com.androdevs.luckysevens.ui.screens

/**
 *Project: Lucky Sevens
 *Created by: Milena
 *On: 22/11/2026 at 23:0
 *Year: 2026
 */
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewClientCompat
import com.androdevs.luckysevens.R
import com.androdevs.luckysevens.ui.components.TitleTopBar
import android.graphics.drawable.GradientDrawable
import android.os.Build

// Color de la barra de desplazamiento: el color del texto de la ayuda (#F2E9D8) con algo de transparencia.
private val SCROLLBAR_COLOR = 0xCCF2E9D8.toInt()

@Composable
fun HelpScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    // Carpeta de la ayuda según el idioma de la app: "es", "en" o "fr" (ver help_folder en strings.xml).
    val helpFolder = stringResource(R.string.help_folder)
    // Dirección virtual de la ayuda: el dominio es ficticio y no sale a internet.
    val helpUrl = "https://appassets.androidplatform.net/assets/help/$helpFolder/index.html"
    // traduce /assets/... a la carpeta assets de la app. Se crea una sola vez.
    val assetLoader = remember {
        WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(context))
            .build()
    }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        TitleTopBar(title = stringResource(R.string.settings_help_title), onBackClick = onBackClick)
        // AndroidView: puente entre Compose y el WebView, que es una vista clásica de Android.
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                WebView(viewContext).apply {
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    isVerticalScrollBarEnabled = true
                    isScrollbarFadingEnabled = false
                    // Barra más gruesa y de color claro para que se vea sobre el fondo oscuro.
                    scrollBarSize = (4 * resources.displayMetrics.density).toInt()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        verticalScrollbarThumbDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 12f
                            setColor(SCROLLBAR_COLOR)
                        }
                    }
                    webViewClient = LocalContentWebViewClient(assetLoader)
                    loadUrl(helpUrl)
                }
            }
        )
    }
}

// Antes de salir a internet, el WebView pregunta aquí si la dirección es un archivo local.
private class LocalContentWebViewClient(
    private val assetLoader: WebViewAssetLoader
) : WebViewClientCompat() {
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return assetLoader.shouldInterceptRequest(request.url)
    }
}