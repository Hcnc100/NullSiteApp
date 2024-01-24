package com.nullpointer.nullsiteadmin.ui.preview.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nullpointer.nullsiteadmin.ui.navigator.MainDestinations

class MainDestinationsProvider : PreviewParameterProvider<MainDestinations> {
    override val values = MainDestinations.values().asSequence()
}