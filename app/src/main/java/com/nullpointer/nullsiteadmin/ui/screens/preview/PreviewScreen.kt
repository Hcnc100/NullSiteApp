package com.nullpointer.nullsiteadmin.ui.screens.preview

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PreviewScreen() {
    val webState= rememberWebViewState(url = "https://nullpointer-716ae.web.app/")
    Scaffold() { it ->
        WebView(
            modifier= Modifier.padding(it),
            state = webState,
            onCreated = { it.settings.javaScriptEnabled = true }
        )
    }
}