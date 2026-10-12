package com.nullpointer.nullsiteadmin.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/** Observe every event directly: rapid pause/stop transitions must not coalesce. */
@Composable
fun BiometricLifecycleEffect(onPause: () -> Unit, onResume: () -> Unit) {
    val owner = LocalLifecycleOwner.current
    val pause by rememberUpdatedState(onPause)
    val resume by rememberUpdatedState(onResume)
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> pause()
                Lifecycle.Event.ON_RESUME -> resume()
                else -> Unit
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
}
