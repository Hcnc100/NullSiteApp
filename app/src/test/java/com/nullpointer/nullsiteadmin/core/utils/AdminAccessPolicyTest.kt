package com.nullpointer.nullsiteadmin.core.utils

import com.nullpointer.nullsiteadmin.exception.NullAppException
import org.junit.Assert.assertThrows
import org.junit.Test

class AdminAccessPolicyTest {
    @Test fun boolean_admin_claim_grants_access() {
        AdminAccessPolicy.requireAdmin(mapOf("admin" to true))
    }

    @Test fun absent_false_or_malformed_claims_deny_access() {
        listOf(emptyMap(), mapOf("admin" to false), mapOf("admin" to "true"), mapOf("admin" to 1)).forEach {
            assertThrows(NullAppException.AdminRequired::class.java) { AdminAccessPolicy.requireAdmin(it) }
        }
    }
}
