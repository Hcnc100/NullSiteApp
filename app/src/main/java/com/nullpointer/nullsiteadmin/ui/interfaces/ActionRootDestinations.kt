package com.nullpointer.nullsiteadmin.ui.interfaces

import com.ramcosta.composedestinations.spec.Direction

interface ActionRootDestinations {
    fun backDestination():Boolean
    fun changeRoot(direction: Direction)
    fun logout()
}