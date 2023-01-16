package com.nullpointer.nullsiteadmin.core.utils.extensions

import android.app.KeyguardManager
import android.content.Context

val Context.keyguardManager: KeyguardManager
    get() = this.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager