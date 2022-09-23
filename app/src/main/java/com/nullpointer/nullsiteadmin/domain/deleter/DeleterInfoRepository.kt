package com.nullpointer.nullsiteadmin.domain.deleter

interface DeleterInfoRepository {
    suspend fun deleterAllInformation()
}