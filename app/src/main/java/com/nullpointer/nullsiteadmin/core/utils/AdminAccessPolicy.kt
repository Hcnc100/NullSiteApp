package com.nullpointer.nullsiteadmin.core.utils

import com.nullpointer.nullsiteadmin.exception.NullAppException

object AdminAccessPolicy {
    fun requireAdmin(claims: Map<String, Any>) {
        if (claims["admin"] != true) throw NullAppException.AdminRequired
    }
}
