package com.nullpointer.nullsiteadmin.ui.interfaces

import android.net.Uri
import com.ramcosta.composedestinations.spec.Direction

interface ActionRootDestinations {
    fun backDestination():Boolean
    fun changeRoot(direction: Direction)
    fun changeRoot(route: Uri)
}