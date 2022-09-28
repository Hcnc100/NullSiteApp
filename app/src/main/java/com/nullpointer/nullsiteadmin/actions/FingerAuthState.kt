package com.nullpointer.nullsiteadmin.actions

enum class BiometricResult {
    PASSED, LOCKED_TIME_OUT, DISABLE_ALWAYS
}

enum class BiometricState {
    Locked, DisabledTimeOut, LockedTimeOut
}