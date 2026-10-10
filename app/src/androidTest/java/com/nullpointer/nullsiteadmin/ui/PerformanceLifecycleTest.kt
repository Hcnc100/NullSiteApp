package com.nullpointer.nullsiteadmin.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Counts real subscriptions; no network, timers or frame-rate claims. */
class PerformanceLifecycleTest {
    @get:Rule val rule = createComposeRule()

    @Test fun lifecycleCollectionStopsAndResumesWithLatestValue() = checkCollection(true)
    @Test fun legacyCollectionRemainsSubscribedWhenStopped() = checkCollection(false)

    private fun checkCollection(lifecycleAware: Boolean) {
        lateinit var owner: Owner
        val values = MutableStateFlow(0)
        rule.runOnIdle { owner = Owner().apply { registry.currentState = Lifecycle.State.RESUMED } }
        rule.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                val value by if (lifecycleAware) values.collectAsStateWithLifecycle()
                    else values.collectAsState()
                Text("value=$value")
            }
        }
        rule.waitUntil { values.subscriptionCount.value == 1 }
        rule.runOnIdle { owner.registry.currentState = Lifecycle.State.CREATED }
        rule.waitUntil { values.subscriptionCount.value == if (lifecycleAware) 0 else 1 }
        rule.runOnIdle { values.value = 7 }
        rule.waitForIdle()
        rule.onNodeWithText(if (lifecycleAware) "value=0" else "value=7").assertExists()
        rule.runOnIdle { owner.registry.currentState = Lifecycle.State.RESUMED }
        rule.waitUntil { values.subscriptionCount.value == 1 }
        rule.onNodeWithText("value=7").assertExists()
        rule.runOnIdle { owner.registry.currentState = Lifecycle.State.DESTROYED }
        if (lifecycleAware) rule.waitUntil { values.subscriptionCount.value == 0 }
        rule.runOnIdle { assertEquals(if (lifecycleAware) 0 else 1, values.subscriptionCount.value) }
    }

    private class Owner : LifecycleOwner {
        val registry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle get() = registry
    }
}
