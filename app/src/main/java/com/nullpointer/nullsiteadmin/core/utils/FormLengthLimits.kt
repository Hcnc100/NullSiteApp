package com.nullpointer.nullsiteadmin.core.utils

/** App-side limits, not Firestore quotas. Keep counters and validation on the same policy. */
object FormLengthLimits {
    // Match the current web validators by resource; do not apply project limits to login.
    const val PROFILE_NAME = 120
    const val PROFILE_PROFESSION = 180
    const val PROFILE_DESCRIPTION = 5_000
    const val PROJECT_NAME = 120
    const val PROJECT_DESCRIPTION = 10_000
    // Storage download URLs include encoded object paths and tokens.
    const val URL = 2_048
    const val EMAIL = 254
    // Login must accommodate existing credentials without trimming or truncation.
    const val MAX_CREDENTIAL_LENGTH = 4_096
}
