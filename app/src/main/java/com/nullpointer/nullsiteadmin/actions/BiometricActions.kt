package com.nullpointer.nullsiteadmin.actions

enum class BiometricResultState {
    PASSED, TEMPORARILY_LOCKED, DISABLE, NOT_SUPPORTED
}

enum class BiometricLockState {
    UNAVAILABLE, LOCK, LOCKED_BY_TIME_OUT, LOCKED_BY_MANY_INTENTS,
}