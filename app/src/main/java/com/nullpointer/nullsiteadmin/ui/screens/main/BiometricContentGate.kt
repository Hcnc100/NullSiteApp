package com.nullpointer.nullsiteadmin.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy

/** A local lock never changes graph/entry identity or disposes picker registrations. */
@Composable
fun BiometricContentGate(locked: Boolean, lockContent: @Composable () -> Unit, content: @Composable () -> Unit) {
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(locked) {
        if (locked) { focus.clearFocus(force = true); keyboard?.hide() }
    }
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().graphicsLayer { alpha = if (locked) 0f else 1f }
            .then(if (locked) Modifier.clearAndSetSemantics {} else Modifier)) { content() }
        BackHandler(enabled = locked) { /* Never pop an editor hidden by the lock. */ }
        // Do not add a modal window while the external picker owns the foreground.
        if (locked && lifecycleState.isAtLeast(Lifecycle.State.RESUMED)) Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false,
                usePlatformDefaultWidth = false, decorFitsSystemWindows = false,
                securePolicy = SecureFlagPolicy.SecureOn)
        ) { Surface(Modifier.fillMaxSize()) { lockContent() } }
    }
}
