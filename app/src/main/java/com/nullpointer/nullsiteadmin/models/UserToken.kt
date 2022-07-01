package com.nullpointer.nullsiteadmin.models

import com.google.firebase.firestore.ServerTimestamp

data class UserToken(
    val tokens: String,
    @ServerTimestamp
    val timestamp: Long? = null,
)