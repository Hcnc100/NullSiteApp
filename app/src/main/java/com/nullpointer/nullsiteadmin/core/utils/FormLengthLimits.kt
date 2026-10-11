package com.nullpointer.nullsiteadmin.core.utils

/** App-side limits, not Firestore quotas. Keep counters and validation on the same policy. */
object FormLengthLimits {
    const val NAME = 100
    const val PROFESSION = 120
    const val DESCRIPTION = 2_000
    // Storage download URLs include encoded object paths and tokens.
    const val URL = 2_048
    const val EMAIL = 254
    // Login must accommodate existing credentials without trimming or truncation.
    const val MAX_CREDENTIAL_LENGTH = 4_096
}
